package com.sinova.monitor.service;

import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 测试
 * Created by Noah on 2017/3/31.
 */
@Service
public class MessageQueryTest implements MessageQuery{
	@Override
	public String queryIndex(String[] indices, String inter, String keywords,
	                         Date startDate, Date endDate, int pageNum, int pagesize) {
		return null;
	}

	@Override
	public String queryTid(String[] indices, String mobile, String transid,
	                       Date startDate, Date endDate) {
		return null;
	}
}
