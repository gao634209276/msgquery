package com.sinova.monitor.service;


import com.alibaba.fastjson.JSON;
import com.sinova.monitor.model.MessageBaseInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.sinova.monitor.service.ESQuery.*;
import static com.sinova.monitor.util.DateUtil.*;
import static com.sinova.monitor.util.ProUtil.getProperties;

/**
 * 业务逻辑类，调用原生es接口，或自定义通用es接口
 * 1，匹配索引
 * 2，查询
 * 3，处理结果
 */
@Service
public class MessageQuery {


	private static final Log log = LogFactory.getLog(MessageQuery.class);

	public String queryIndex(String inter, String keywords, String channel, Date startDate, Date endDate, int pageNum, int pagesize, String env) {
		//1,index ${channel}-${env}-YYYY.MM.dd
		String indexPrefix = getIndexPref(channel, env);
		if (StringUtils.isEmpty(indexPrefix))
			return "";
		Date start = new Date();
		String[] indices = getIndices(indexPrefix, startDate, endDate, start);
		if (indices.length == 0)
			return "";
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, channel, pageNum, pagesize);
		// 3,构建Query
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(inter) && !inter.equals("all"))
			query.must(QueryBuilders.termQuery("inter", inter.substring(0, inter.indexOf("||")).toLowerCase()));
		// keyword
		if (keywords.length() <= 11) {
			request.setRouting(keywords);
			query.must(QueryBuilders.termQuery("mobile", keywords.toLowerCase()));
		} else {
			query.must(QueryBuilders.termQuery("transid", keywords.toLowerCase()));
		}
		// @timestamp-->range doQuery
		//RangeQueryBuilder timestampRange = rangeQuery("@timestamp",startDate, endDate);
		if (null != startDate || null != endDate) {
			RangeQueryBuilder timestampRange = QueryBuilders.rangeQuery("@timestamp");
			if (null != startDate) timestampRange.from(UTC_Format.format(startDate));
			if (null != endDate) timestampRange.to(UTC_Format.format(endDate));
			query.must(timestampRange);
		}
		//request设置query构建完成
		String[] includes = new String[]{"@timestamp", "mobile", "transid", "inter", "type"};
		request.setQuery(query)
				.setFetchSource(includes, null)
				.addSort("@timestamp", SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		// 遍历-->1:所有，
		// 2：包含1组,以Transid去重rmap,
		//MessageTable
		int total = (int) response.getHits().totalHits();
		SearchHit[] hits = response.getHits().getHits();
		Properties interList = getProperties("interList.properties");
		List<MessageBaseInfo> infoList = new LinkedList<MessageBaseInfo>();
		Set<String> tidCheck = new HashSet<>();
		int j = 0;
		for (int i = 0; i < hits.length; i++) {
			// check
			Map<String, Object> hit = hits[i].getSource();
			String jsonHit = hits[i].getSourceAsString();
			log.info(jsonHit);
			String tid = String.valueOf(hit.get("transid"));
			if (tidCheck.contains(tid)) {
				continue;
			}
			// set should not null
			MessageBaseInfo info = new MessageBaseInfo();
			if (null != hit.get("inter")) {
				String interStr = String.valueOf(hit.get("inter"));
				info.setInter(interStr);
				if ((interList.containsKey(interStr))) {
					info.setIntercode(interList.getProperty(interStr));
				}
			}
			String tempstamp = String.valueOf(hit.get("@timestamp")).replace("T", " ");
			String date = tempstamp.substring(0, tempstamp.indexOf("."));
			// set
			info.setTransid(tid);
			//info.setFlag(j);
			info.setType(String.valueOf(hit.get("type")));
			info.setMobile(String.valueOf(hit.get("mobile")));
			info.setTimestamp(date.substring(date.indexOf("-") + 1));
			// add
			infoList.add(info);
			tidCheck.add(tid);
			j++;
		}
		Collections.sort(infoList);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rows", total);
		map.put("listPage", infoList);
		map.put("maxPage", (total + 1) / pagesize);
		map.put("pageNo", pageNum);
		map.put("tablesize", infoList.size());
		map.put("error", 0);
		String json = JSON.toJSONString(map);
		long useTime = (new Date().getTime() - start.getTime()) / 1000;
		log.info("请求用时为" + useTime + "秒");
		log.info(json);
		return json;
	}

	public String queryTid(String mobile, String transid, String channel, Date startDate, Date endDate, String env) {
		//1,index ${channel}-${env}-YYYY.MM.dd
		String indexPrefix = getIndexPref(channel, env);
		if (StringUtils.isEmpty(indexPrefix))
			return "";
		Date start = new Date();
		String[] indices = getIndices(indexPrefix, startDate, endDate, start);
		if (indices.length == 0)
			return "";


		// 3,构建Query
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		// mobile, transid
		query.must(QueryBuilders.termQuery("transid", transid.toLowerCase()))
				.must(QueryBuilders.termQuery("mobile", mobile.toLowerCase()));
		if (null != startDate || null != endDate) {
			RangeQueryBuilder timestampRange = QueryBuilders.rangeQuery("@timestamp");
			if (null != startDate) timestampRange.from(UTC_Format.format(startDate));
			if (null != endDate) timestampRange.to(UTC_Format.format(endDate));
			query.must(timestampRange);
		}
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, channel, 0, 100);
		request.setRouting(mobile).setQuery(query).addSort("@timpstamps", SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		SearchHit[] hits = response.getHits().getHits();
		List<String> listjson = new ArrayList<String>();
		for (SearchHit h : hits) {
			// message.compile("INFO\\s\\S+\\:[0-9]+");
			// 获取日志的行号，xxxx[yyyy-DD-mm HH:mm:ss] INFO xx.xxx.xx:dd -mobile-xxxx
			// -->dd-->code
			// 去掉@符号，@timestamp-->timestamp
			// to JSON
			// 报文加上服务器，节点; message = ip+path+message
			// 组装对象，对象按时间排序
			// line = message.split("\n"),
			// line >50000 --> 用\n截取第一个
			// \n----\n+line+\n---\n+line+...
			// 左右括号处理< ==> "&lt;  > ==> "&gt;
			// 换行处理 \n --> <br/>
			// 空格 &nbsp
			// request.set(messageDetail,message)
			//
			listjson.add(h.getSourceAsString());
		}
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("reponse_code", "ok");
		resultMap.put("result", listjson);
		resultMap.put("total", response.getHits().totalHits());
		return null;
	}

	public String getIndexPref(String channel, String env) {
		StringBuilder prefixBuf = new StringBuilder();
		if ("product".equals(env)) {
			prefixBuf.append(channel).append("message");
		} else {
			// test-mob test-web, pre-mob,pre-web
			prefixBuf.append(env).append("-").append(channel);
		}
		return prefixBuf.toString();
	}


	/**
	 * @param indexPrefix Elasticsearch的前缀，一般为: $env-$channel
	 * @param startDate   可为空，确保为yyyy-MM-dd HH:mm:ss的时间格式，否则解析错误
	 * @param endDate     可为空，确保为yyyy-MM-dd HH:mm:ss的时间格式，否则解析错误
	 * @return 通过es查询所有indices与用户指定时间区间，返回可用的indices
	 */
	public String[] getIndices(String indexPrefix, Date startDate, Date endDate, Date now) {
		// 初始化构建一个可查询的index的集合，目前固定保持20天内的数据，这里限制最多可查询20天
		Calendar begin = Calendar.getInstance();
		begin.setTime(now);
		begin.add(Calendar.DATE, -20);
		Calendar end = Calendar.getInstance();
		end.setTime(now);
		if (null != startDate)
			begin.setTime(startDate);
		if (null != endDate)
			end.setTime(endDate);
		Set<String> daySet = new HashSet<>();
		while (begin.compareTo(end) <= 0) {
			// prefix-yyyy.MM.dd
			daySet.add(indexPrefix + "-" + msgFormat.format(begin.getTime()));
			begin.add(Calendar.DATE, 1);
		}
		// get All ES indices from Elasticsearch
		Client client = SpringContextUtil.getBean("client");
		ClusterStateResponse csr = client.admin().cluster().prepareState()
				.execute().actionGet();
		String[] allIndices = csr.getState().getMetaData()
				.concreteAllOpenIndices();
		// get indices match channel and env
		List<String> indices = new ArrayList<>();
		for (int i = 0; i < allIndices.length; i++) {
			if (allIndices[i].startsWith(indexPrefix) && daySet.contains(allIndices[i]))
				indices.add(allIndices[i]);
		}
		return indices.toArray(new String[indices.size()]);
	}
}
