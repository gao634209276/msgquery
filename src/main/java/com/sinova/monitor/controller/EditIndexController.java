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

/**
 * 编辑索引的控制器
 * 由于逻辑简单，直接在控制器中完成
 */
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
			return null;// 异常
		}
		// week ago
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.add(Calendar.DATE, -7);
		if (date.getTime() > c.getTime().getTime()) {
			map.put("code", "9999");//不允许关闭7天之内的索引
			return JSON.toJSONString(map);
		}
		if (!"product".equals(huanjing)) {
			map.put("code", "0003");//测试集群,有且仅有一个index
			return JSON.toJSONString(map);
		}
		String indexPrefix = channels + "message";
		String[] indices = getIndices(indexPrefix, date, date);
		if (indices.length == 0)
			map.put("code", "000");//该索引不存在或已关闭
		CloseIndexResponse response = client.admin()
				.indices().prepareClose(indices).get();
		if (response.isAcknowledged()) {
			map.put("code", "0001");//操作成功
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
		if (!"product".equals(huanjing)) {
			map.put("code", "0003");//测试集群,有且仅有一个index
			return JSON.toJSONString(map);
		}
		try {
			OpenIndexResponse response = client.admin().indices()
					.prepareOpen(channels + "message-" + thisDate.replace("-", ".")).get();
			if (response.isAcknowledged()) {
				map.put("code", "0001");
				return JSON.toJSONString(map);//操作成功
			} else {
				map.put("code", "0002");//操作失败
				return JSON.toJSONString(map);
			}
		} catch (Exception e) {
			log.info(e.toString());// 索引不存在
			return null;
		}
	}

}
