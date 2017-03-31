package com.sinova.monitor.service;

import com.sinova.monitor.model.Message;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.sinova.monitor.elasticsearch.ESClient.buildRequest;
import static com.sinova.monitor.elasticsearch.ESClient.getIndexPref;

/**
 * 预发布：yfb
 * 测试：test
 */
@Service
public class MessageQueryTest implements MessageQuery {

	@Override
	public String queryIndex(String inter, String keywords, String channel,
	                         Date startDate, Date endDate,
	                         int pageNum, int pagesize, String env) {
		//SearchRequestBuilder request = buildRequest("test", channel, pageNum, pagesize);
		return null;
	}

	@Override
	public List<Message> queryTid(String mobile, String transid, String channel,
	                              Date startDate, Date endDate, String env) {
		return null;
	}
}
