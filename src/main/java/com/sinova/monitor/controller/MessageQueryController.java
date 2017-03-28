package com.sinova.monitor.controller;

import com.sinova.monitor.service.ESConfig;
import com.sinova.monitor.service.MessageQuery;
import com.sinova.monitor.service.SpringContextUtil;
import com.sinova.monitor.util.ProUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 报文查询大类
 */
@Controller
@RequestMapping("/msgquery")
public class MessageQueryController {
	private Logger log = LoggerFactory.getLogger(MessageQueryController.class);

	@RequestMapping("/list.htm")
	public String getMessageList(HttpServletRequest request) {
		List<String> intersList = ProUtil.toList(ProUtil.getProperties("interList.properties"));
		request.setAttribute("inters", intersList);
		return "msgquery/list";
	}

	@RequestMapping("/messageQuery.json")
	public
	@ResponseBody
	String getMessage(String inter, String keywords, String channel, String startDay, String endDay, @RequestParam(defaultValue = "1") String pageNum, String huanjing) {
		String pagesize = ProUtil.getProperties("common.properties")
				.getProperty("message.pagesize");//56

		// message query
		new MessageQuery().getMessage(inter, keywords, channel, startDay, endDay, pageNum, huanjing);
		return null;
	}

	/**
	 * txt文件导出
	 */
	@RequestMapping("/gettxt.json")
	public
	@ResponseBody
	String getTxt(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
//        request.setCharacterEncoding("UTF-8");
		String detail = request.getParameter("detail");
		String fileName = "message" + new Date().getTime() + ".txt";
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		return detail;
	}

	/**
	 * 详情页面展示
	 */
	@RequestMapping("/messageDetail.htm")
	public String messageDetail(HttpServletRequest request, String transid, String type, String startDay, String endDay, String keywords, String channels, String hightlight, String smstype, String huanjing) {
		return null;
	}
}
