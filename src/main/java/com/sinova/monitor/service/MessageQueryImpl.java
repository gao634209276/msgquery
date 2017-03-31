package com.sinova.monitor.service;

import com.alibaba.fastjson.JSON;
import com.sinova.monitor.model.Message;
import com.sinova.monitor.model.MessageDTO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sinova.monitor.elasticsearch.ESClient.*;
import static com.sinova.monitor.util.ProUtil.getProperties;

/**
 * 业务逻辑类，调用原生es接口，或自定义通用es接口
 * 1，匹配索引
 * 2，查询
 * 3，处理结果
 * Created by Noah on 2017/3/31.
 */
@Service
public class MessageQueryImpl implements MessageQuery {

	private static final Log log = LogFactory.getLog(MessageQueryImpl.class);

	@Override
	public String queryIndex(String[] indices, String inter, String keywords,
	                         Date startDate, Date endDate,
	                         int pageNum, int pagesize) {

		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, pageNum, pagesize);
		// 3,构建Query
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(inter) && !inter.equals("all"))
			query.must(QueryBuilders
					.termQuery("inter", inter.substring(0, inter.indexOf("||")).toLowerCase()));
		// keyword
		if (keywords.length() <= 11) {
			request.setRouting(keywords);
			query.must(QueryBuilders.termQuery("mobile", keywords.toLowerCase()));
		} else {
			query.must(QueryBuilders.termQuery("transid", keywords.toLowerCase()));
		}
		// @timestamp-->range doQuery
		query.must(timestampRange(startDate, endDate));
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
		List<MessageDTO> infoList = new LinkedList<MessageDTO>();
		Set<String> tidCheck = new HashSet<>();//去重
		for (SearchHit hit : hits) {
			// check
			Map<String, Object> hitMap = hit.getSource();
			String jsonHit = hit.getSourceAsString();
			log.info(jsonHit);
			String tid = String.valueOf(hitMap.get("transid"));
			if (tidCheck.contains(tid)) {
				continue;
			}
			// set value should not null
			MessageDTO info = new MessageDTO();
			if (null != hitMap.get("inter")) {
				String interStr = String.valueOf(hitMap.get("inter"));
				info.setInter(interStr);
				if ((interList.containsKey(interStr))) {
					info.setIntercode(interList.getProperty(interStr));
				}
			}
			String timestamp = String.valueOf(hitMap.get("@timestamp")).replace("T", " ");
			String date = timestamp.substring(0, timestamp.indexOf("."));
			// set
			info.setTransid(tid);
			//info.setFlag(j);
			info.setType(String.valueOf(hitMap.get("type")));
			info.setMobile(String.valueOf(hitMap.get("mobile")));
			info.setTimestamp(date.substring(date.indexOf("-") + 1));
			// add
			infoList.add(info);
			tidCheck.add(tid);
		}
		Collections.sort(infoList);
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("rows", total);
		map.put("listPage", infoList);
		map.put("maxPage", (total + 1) / pagesize);
		map.put("pageNo", pageNum);
		map.put("tablesize", infoList.size());
		map.put("error", 0);
		return JSON.toJSONString(map);
	}

	@Override
	public String queryTid(String[] indices, String mobile, String transid,
	                       Date startDate, Date endDate) {
		// 1,构建Query
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("transid", transid.toLowerCase()))
				//.must(QueryBuilders.termQuery("mobile", mobile.toLowerCase()))
				.must(timestampRange(startDate, endDate));
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, 0, 100);
		request.setQuery(query).addSort("@timestamp", SortOrder.DESC);//setRouting(mobile).
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		SearchHit[] hits = response.getHits().getHits();

		Pattern pattern = Pattern.compile("INFO\\s\\S+\\:[0-9]+");
		List<Message> msgList = new ArrayList<Message>();
		for (SearchHit hit : hits) {
			Map<String, Object> hitMap = hit.getSource();
			// 获取日志的行号
			Matcher m = pattern.matcher((String) hitMap.get("message"));
			int codeNum = 0;
			if (m.find()) {
				String logInfo = m.group(0);
				int codeSplit = logInfo.indexOf(":");
				if (codeSplit > 0) {
					codeNum = Integer.parseInt(logInfo.substring(codeSplit + 1));
				}
			}
			// 报文加上服务器，节点; message = ip+path+message
			String msgstr = "服务器：" + hitMap.get("serverip") + "      "
					+ "节点：" + hitMap.get("path") + "\n"
					+ hitMap.get("message");
			// 组装对象，对象按时间排序
			Message message = new Message();
			message.setCode(codeNum);
			message.setMessage(msgstr);
			msgList.add(message);
		}
		Collections.sort(msgList);
		//页面显示处理
		StringBuffer msgBuf = new StringBuffer();
		for (Message msg : msgList) {
			String message = msg.getMessage();
			// line = message.split("\n"),
			// line >50000 --> 用\n截取第一个
			if (message.split("\n").length >= 500) {
				Matcher m = Pattern.compile("\n").matcher(message);
				int mIdx = 0;
				while (m.find()) {
					mIdx++;
					if (mIdx == 500) break;//当"\n"符号第500次出现的位置
				}
				message = message.substring(0, m.start());
			}
			// \n----\n+line+\n---\n+line+...
			msgBuf.append("\n--------------------------------------------------------------------------------------------------------------------------------------\n")
					.append(message);
		}
		return msgBuf.toString();
	}
}
