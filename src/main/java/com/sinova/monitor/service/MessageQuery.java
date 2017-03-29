package com.sinova.monitor.service;


import com.alibaba.fastjson.JSON;
import com.sinova.monitor.model.MessageTable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
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
public class MessageQuery {
	private static final Log log = LogFactory.getLog(MessageQuery.class);

	public void query(String inter, String keywords, String channel, Date startDate, Date endDate, String pageNum, String pagesize, String env) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		//1,index ${channel}-${env}-YYYY.MM.dd
		String indexPrefix = getIndexPref(channel, env);
		if (StringUtils.isEmpty(indexPrefix))
			return;
		String[] indices = getIndices(indexPrefix, startDate, endDate);
		if (indices.length == 0)
			return;
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, channel, Integer.parseInt(pageNum), Integer.parseInt(pagesize));
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
		RangeQueryBuilder timestampRange = QueryBuilders.rangeQuery("@timestamp");
		if (startDate != null) timestampRange.from(UTC_Format.format(startDate));
		if (endDate != null) timestampRange.to(UTC_Format.format(endDate));
		query.must(timestampRange);
		//request设置query构建完成
		request.setQuery(query).addSort("@timestamp", SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		Iterator<SearchHit> it = response.getHits().iterator();
		String json;
		MessageTable messageTable;
		SearchHits hit = response.getHits();
		Properties interList = getProperties("interList.properties");
		// 遍历-->1:所有，
		// 2：包含1组,以Transid去重rmap,
		LinkedHashMap<String, MessageTable> rmap = new LinkedHashMap<String, MessageTable>();
		for (int i = 0; i < hit.totalHits(); i++) {
			json = hit.getAt(i).getSourceAsString().replace("@", "");// @timestamp
			messageTable = JSON.parseObject(json, MessageTable.class);
			// timestamp
			String time = messageTable.getTimestamp()//2016-07-08T17:47:25.570Z
					.replace("T", " ");//2016-07-08 17:47:25.570Z
			time = time.substring(0, time.indexOf("."));//2016-07-08 17:47:25
			messageTable.setTimestamp(time.substring(time.indexOf("-") + 1));//07-08 17:47:25
			// flag
			messageTable.setFlag(i);
			// interCode
			messageTable.setIntercode(interList.getProperty(messageTable.getInter()));
			rmap.put(messageTable.getTransid(), messageTable);

		}

		/*Collections.sort(list, new Comparator<MessageTable>() {
			public int compare(MessageTable o1, MessageTable o2) {
				return -o1.getTimestamp().compareTo(o2.getTimestamp());
			}
		});

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rows", hit.totalHits());
		map.put("listPage", rlist);
		map.put("maxPage", maxPage);
		map.put("pageNo", pageNum);
		map.put("tablesize", rlist.size());
		map.put("error", 0);
		String json = JSON.toJSONString(map);*/
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
	public String[] getIndices(String indexPrefix, Date startDate, Date endDate) {
		// 初始化构建一个可查询的index的集合，目前固定保持20天内的数据，这里限制最多可查询20天
		Date now = new Date();
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
		return (String[]) indices.toArray();
	}
}
