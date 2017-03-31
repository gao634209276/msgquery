package com.sinova.monitor.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static com.sinova.monitor.util.DateUtil.dayFormat;

@Controller
@RequestMapping("/editindex")
public class EditIndexController {

	private Logger log = LoggerFactory
			.getLogger(EditIndexController.class);


	@RequestMapping("/closeIndex.json")
	public
	@ResponseBody
	String closeIndex(HttpServletRequest request, String thisDate, String channels, String huanjing) {
		HashMap<String, String> map = new HashMap<String, String>();
		Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		c.add(Calendar.DATE, -7);
		String weekAgo = dayFormat.format( c.getTime());//yyyy-MM-dd
		if (thisDate.compareTo(weekAgo) > 0) {
			map.put("code", "9999");
			return JSON.toJSONString(map);
		}
		return null;
	}


	@RequestMapping("/openorclose.htm")
	public String openOrClose(HttpServletRequest request) {

		return "msgquery/openIndex";
	}

	@RequestMapping("/openIndex.json")
	public
	@ResponseBody
	String openIndex(HttpServletRequest request, String thisDate, String channels, String huanjing) {
//        Map<String,Object> resultMap = new HashMap<String,Object>();
//        resultMap.put("reponse_code", "0000");

		return null;
	}

	public String prefixIndex(String thisDate, String channels, String huanjing) {

		return null;
	}

}
