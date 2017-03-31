package com.sinova.monitor.controller;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.close.CloseIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;


import static com.sinova.monitor.elasticsearch.ESClient.*;
import static com.sinova.monitor.util.DateUtil.*;

@Controller
@RequestMapping("/editindex")
public class EditIndexController {
	private Logger log = LoggerFactory.getLogger(EditIndexController.class);

	@RequestMapping("/closeIndex.json")
	public
	@ResponseBody
	String closeIndex(String thisDate, String channels, String huanjing) {

		HashMap<String, String> map = new HashMap<String, String>();

		Date date;
		try {
			date = dayFormat.parse(thisDate);
		} catch (ParseException e) {
			e.printStackTrace();
			map.put("code", "0004");// 日期格式错误
			return JSON.toJSONString(map);
		}
		// week ago
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.add(Calendar.DATE, -7);
		if (date.getTime() > c.getTime().getTime()) {
			map.put("code", "9999");//不允许关闭7天之内的索引
			return JSON.toJSONString(map);
		}
		String indexPrefix = getIndexPref(channels, huanjing);
		String[] index = getIndices(indexPrefix, date, date);
		if (index.length == 0) {
			map.put("code", "0001");//无索引
			return JSON.toJSONString(map);
		}
		CloseIndexResponse response = client.admin()
				.indices().prepareClose(index).get();
		if (response.isAcknowledged()) {
			map.put("code", "0001");
			return JSON.toJSONString(map);
		} else {
			map.put("code", "0002");
			return JSON.toJSONString(map);
		}
	}


	@RequestMapping("/openorclose.htm")
	public String openOrClose() {
		return "msgquery/openIndex";
	}

	@RequestMapping("/openIndex.json")
	public
	@ResponseBody
	String openIndex(String thisDate, String channels, String huanjing) {
		HashMap<String, String> map = new HashMap<String, String>();
		String indexPrefix = getIndexPref(channels, huanjing);
		try {
			OpenIndexResponse response = client.admin().indices()
					.prepareOpen(indexPrefix + "-" + thisDate.replace("-", ".")).get();
			if (response.isAcknowledged()) {
				map.put("code", "0001");
				return JSON.toJSONString(map);
			} else {
				map.put("code", "0003");
				return JSON.toJSONString(map);
			}
		} catch (Exception e) {
			log.info(e.toString());// 索引不存在
			return null;
		}
	}

}
