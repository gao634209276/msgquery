package com.sinova.monitor.service;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.springframework.util.StringUtils;


public class ESQuery {
	public static Client client = SpringContextUtil.getBean("client");

	public static SearchRequestBuilder buildRequest(String[] indices, String types, int pageNum, int pagesize) {
		SearchRequestBuilder requestBuilder = client.prepareSearch(indices).setExplain(true);
		int from = pageNum - 1 / pagesize;
		if (!StringUtils.isEmpty(types))
			requestBuilder.setTypes(types);
		if (from > 0)
			requestBuilder.setFrom(pageNum - 1 / pagesize);
		if (pagesize >= 0)
			requestBuilder.setSize(pagesize);

		return requestBuilder;
	}

}
