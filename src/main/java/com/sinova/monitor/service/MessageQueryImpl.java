package com.sinova.monitor.service;

import com.alibaba.fastjson.JSON;
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

import static com.sinova.monitor.elasticsearch.ESClient.*;
import static com.sinova.monitor.elasticsearch.SearchEnv.*;
import static com.sinova.monitor.util.ProUtil.load;

/**
 * 业务逻辑类，调用原生es接口，或自定义通用es接口
 * 1, 构建请求
 * 2，查询
 * 3，处理结果
 * Created by Noah on 2017/3/31.
 */
@Service
public class MessageQueryImpl implements MessageQuery {

	private static final Log log = LogFactory.getLog(MessageQueryImpl.class);
	// search指定需要的field，不全部查询
	private static final String[] includes = new String[]{TIMESTAMP, MOBILE, TRANSID, INTER, TYPE};

	@Override
	public String queryMessage(String[] indices, String interTuple, String keywords,
	                           Date startDate, Date endDate,
	                           int pageNum, int pagesize) {
		String inter = null;
		String interCode = null;
		// 1,构建Query
		// @timestamp-->range doQuery
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(timestampRange(startDate, endDate));
		// inter
		if (!StringUtils.isEmpty(interTuple) && !interTuple.equals(INTER_ALL)) {
			int separatorIndex = interTuple.indexOf("||");
			inter = interTuple.substring(0, separatorIndex);
			interCode = interTuple.substring(separatorIndex + 2);
			query.must(QueryBuilders.termQuery(INTER, inter.toLowerCase()));
		}
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, pageNum, pagesize);
		// keyword--> mobile --> set request / add query
		if (keywords.length() <= 11) {
			request.setRouting(keywords);
			query.must(QueryBuilders.termQuery(MOBILE, keywords.toLowerCase()));
		} else {
			query.must(QueryBuilders.termQuery(TRANSID, keywords.toLowerCase()));
		}
		//request构建完成
		request.setQuery(query)
				.setFetchSource(includes, null)
				.addSort(TIMESTAMP, SortOrder.DESC);
		// search
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		//Transid去重，构建DTO
		Set<String> tidCheck = new HashSet<>();
		int total = (int) response.getHits().totalHits();
		SearchHit[] hits = response.getHits().getHits();
		Properties interList = load("interList.properties");
		List<MessageDTO> infoList = new LinkedList<MessageDTO>();
		for (SearchHit hit : hits) {
			// check
			Map<String, Object> hitMap = hit.getSource();
			String jsonHit = hit.getSourceAsString();
			log.info(jsonHit);
			String tid = (String) hitMap.get(TRANSID);
			if (tidCheck.contains(tid) || StringUtils.isEmpty(tid)) {
				continue;
			}
			// set value should not null
			MessageDTO info = new MessageDTO();
			if (null != inter) {//用户查询时候指定，则不需要从hip中获取
				info.setInter(inter);
				info.setIntercode(interCode);
			} else if (null != hitMap.get(INTER)) {
				String interStr = (String) hitMap.get(INTER);
				info.setInter(interStr);
				if ((interList.containsKey(interStr))) {
					info.setIntercode(interList.getProperty(interStr));
				}
			}
			String timestamp = String.valueOf(hitMap.get(TIMESTAMP)).replace("T", " ");
			String date = timestamp.substring(0, timestamp.indexOf("."));
			// set
			info.setTransid(tid);
			info.setType(String.valueOf(hitMap.get(TYPE)));
			info.setMobile(String.valueOf(hitMap.get(MOBILE)));
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
	public String queryDetail(String[] indices, String mobile, String transid,
	                          Date startDate, Date endDate) {
		//todo mobile rout以及查询，目前有问题
		// 1,构建Query
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(TRANSID, transid.toLowerCase()))
				//.must(QueryBuilders.termQuery(MOBILE, mobile.toLowerCase()))
				.must(timestampRange(startDate, endDate));
		// 2,构建SearchRequest
		SearchRequestBuilder request = buildRequest(indices, 0, 100);
		request.setQuery(query).addSort(TIMESTAMP, SortOrder.DESC);//setRouting(mobile).
		SearchResponse response = request.execute().actionGet();
		log.info("查询时间为(ms):" + (response.getTookInMillis()));
		return MessageDeal.hits2Detail(response.getHits().getHits());
	}
}
