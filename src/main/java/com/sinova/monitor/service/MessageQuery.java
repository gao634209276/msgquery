package com.sinova.monitor.service;

import java.util.Date;

/**
 * 业务处理接口
 * Created by Noah on 2017/3/31.
 */
public interface MessageQuery {

	String queryIndex(String[] indices, String inter, String keywords,
	                  Date startDate, Date endDate,
	                  int pageNum, int pagesize);

	String queryTid(String[] indices, String mobile, String transid,
	                Date startDate, Date endDate);
}
