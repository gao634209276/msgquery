package com.sinova.monitor.service;

import com.alibaba.fastjson.JSON;
import com.sinova.monitor.model.MessageDTO;
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

import static com.sinova.monitor.elasticsearch.ESClient.buildRequest;

/**
 * 测试
 * Created by Noah on 2017/3/31.
 */
@Service
public class MessageQueryTest implements MessageQuery {
	@Override
	public String queryIndex(String[] indices, String inter, String keywords,
	                         Date startDate, Date endDate, int pageNum, int pagesize) {
		SearchRequestBuilder request = buildRequest(indices, pageNum, pagesize);
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		if (!StringUtils.isEmpty(inter) && !inter.equals("all")) {
			String interStr = inter.substring(0, inter.indexOf("||")).toLowerCase();
			query.must(QueryBuilders.termQuery("inter", interStr));
		}
		query.must(QueryBuilders.termQuery("message", keywords.toLowerCase()));
		request.setQuery(query).addSort("@timestamp", SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		int total = (int) response.getHits().totalHits();
		SearchHit[] hits = response.getHits().getHits();
		List<MessageDTO> infoList = new LinkedList<MessageDTO>();
		for (SearchHit hit : hits) {
			Map<String, Object> hitMap = hit.getSource();
			MessageDTO info = new MessageDTO();
			//todo set

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
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery("transid", transid.toLowerCase()));
		SearchRequestBuilder request = buildRequest(indices, 0, 100);
		request.setQuery(query).addSort("@timestamp", SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		Pattern pattern = Pattern.compile("INFO\\s\\S+\\:[0-9]+");
		StringBuffer msgBuf = new StringBuffer();
		for (SearchHit hit : hits) {
			Map<String, Object> hitMap = hit.getSource();
			//页面显示处理
			String message = (String) hitMap.get("message");
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
