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
import java.util.regex.Pattern;

import static com.sinova.monitor.elasticsearch.ESClient.*;
import static com.sinova.monitor.elasticsearch.SearchEnv.*;
import static com.sinova.monitor.service.MessageDeal.*;
import static com.sinova.monitor.util.ProUtil.load;

/**
 * 测试
 * Created by Noah on 2017/3/31.
 */
@Service
public class MessageQueryTest implements MessageQuery {
	private static final Log log = LogFactory.getLog(MessageQueryTest.class);

	private static final String TID_PREF = "transid\\W+";
	private static final String TID_REGEX = "(\\d?(null\\d)?){20,50}";
	private static final String XML_SEP = "<Uniss>";
	private static int flag = Pattern.CASE_INSENSITIVE;

	@Override
	public String queryIndex(String[] indices, String interTuple, String keywords,
	                         Date startDate, Date endDate, int pageNum, int pagesize) {
		String inter = null;
		String interCode = null;

		SearchRequestBuilder request = buildRequest(indices, pageNum, pagesize);
		BoolQueryBuilder query = QueryBuilders.boolQuery();
		query.must(QueryBuilders.termQuery(MESSAGE, keywords.toLowerCase()));
		if (!StringUtils.isEmpty(interTuple) && !interTuple.equals("all")) {
			int separatorIndex = interTuple.indexOf("||");
			inter = interTuple.substring(0, separatorIndex);
			interCode = interTuple.substring(separatorIndex + 2);
			query.must(QueryBuilders.termQuery(MESSAGE, inter.toLowerCase()));
			String[] interArray = inter.split("\\.");
			for (String interSplit : interArray) { // 2个
				query.must(QueryBuilders.termQuery(MESSAGE, interSplit));
			}
		}
		request.setQuery(query).addSort(TIMESTAMP, SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		int total = (int) response.getHits().totalHits();
		SearchHit[] hits = response.getHits().getHits();
		List<MessageDTO> infoList = new LinkedList<MessageDTO>();
		Properties interList = load("interList.properties");
		Set<String> tidCheck = new HashSet<String>();
		for (SearchHit hit : hits) {
			Map<String, Object> hitMap = hit.getSource();

			String message = String.valueOf(hitMap.get(MESSAGE));
			String[] strs = message.split(XML_SEP);
			String head = strs[0];
			String xml = null;
			if (strs.length == 2) {
				xml = strs[1];
			}
			// mobile || transid
			String transid;
			String mobile = "";
			if (keywords.length() <= 11) {
				mobile = keywords;
				// 必须获取transid
				transid = prefix_matcherFirst(TID_PREF, TID_REGEX, head, flag);
				if (StringUtils.isEmpty(transid)) {
					transid = prefix_matcherFirst_surfix("--", "--", TID_REGEX, head, flag);
					if (StringUtils.isEmpty(transid)) {
						transid = prefix_matcherFirst_surfix("--", "--缓存数据生效中", TID_REGEX, head, flag);
						if (StringUtils.isEmpty(transid) && !StringUtils.isEmpty(xml)) {
							transid = parseXML(xml, "<transid>", "</transid>");
						}
					}
				}
			} else {
				transid = keywords;
			}
			if (StringUtils.isEmpty(transid)) {
				// 打印无法tid解析message
				log.warn(message);
				continue;
			}
			if (tidCheck.contains(transid))
				continue;
			// set
			MessageDTO info = new MessageDTO();
			info.setTransid(transid);
			info.setMobile(mobile);
			// @timestamp
			String timestamp = String.valueOf(hitMap.get(TIMESTAMP)).replace("T", " ");
			String date = timestamp.substring(0, timestamp.indexOf("."));
			info.setTimestamp(date.substring(date.indexOf("-") + 1));
			// inter & interCode
			if (null != inter) {
				info.setInter(inter);
				info.setIntercode(interCode);
			} else if (strs.length == 2 && !StringUtils.isEmpty(xml)) {
				inter = parseXML(xml, "<reqbusicode>", "</reqbusicode>");
				if (!StringUtils.isEmpty(inter)) {
					info.setInter(inter);
					if ((interList.containsKey(inter))) {
						info.setIntercode(interList.getProperty(inter));
					}
				}
			}
			infoList.add(info);
			tidCheck.add(transid);
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
		log.info(json);
		return json;
	}

	@Override
	public String queryTid(String[] indices, String mobile, String transid,
	                       Date startDate, Date endDate) {
		//transid确定不为空，在message的field中查询transid
		BoolQueryBuilder query = QueryBuilders.boolQuery()
				.must(QueryBuilders.termQuery(MESSAGE, transid.toLowerCase()));
		SearchRequestBuilder request = buildRequest(indices, 0, 100);
		request.setQuery(query).addSort(TIMESTAMP, SortOrder.DESC);
		SearchResponse response = request.execute().actionGet();
		return MessageDeal.hits2Detail(response.getHits().getHits());
	}


}
