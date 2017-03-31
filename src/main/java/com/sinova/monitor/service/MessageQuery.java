package com.sinova.monitor.service;

import com.sinova.monitor.model.Message;

import java.util.Date;
import java.util.List;

/**
 * 业务处理接口
 */
public interface MessageQuery {

	List<Message> queryTid(String mobile, String transid, String channel,
	                       Date startDate, Date endDate, String env);

	String queryIndex(String inter, String keywords, String channel,
	                  Date startDate, Date endDate, int pageNum, int pagesize, String env);
}
