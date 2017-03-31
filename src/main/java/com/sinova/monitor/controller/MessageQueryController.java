package com.sinova.monitor.controller;

import com.sinova.monitor.model.Message;
import com.sinova.monitor.service.MessageQuery;
import com.sinova.monitor.util.DateUtil;
import com.sinova.monitor.util.ProUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
@Controller
@RequestMapping("/msgquery")
public class MessageQueryController {
	private Logger log = LoggerFactory.getLogger(MessageQueryController.class);

	@Resource
	private MessageQuery messageQuery;
	@Value("#{common['message.pagesize']}")
	private String pagesize;

	@RequestMapping("/list.htm")
	public String getMessageList(HttpServletRequest request) {
		List<String> intersList = ProUtil.toList(ProUtil.getProperties("interList.properties"));
		request.setAttribute("inters", intersList);
		return "msgquery/list";
	}

	@RequestMapping("/messageQuery.json")
	public
	@ResponseBody
	String getMessage(String inter, String keywords, String channel,
	                  String startDay, String endDay,
	                  @RequestParam(defaultValue = "1") String pageNum,
	                  String huanjing) {
		Date endDate = DateUtil.parse(endDay);
		if (null == endDate) {
			endDate = new Date();
		}
		String json = messageQuery.queryIndex(inter, keywords, channel,
				DateUtil.parse(startDay), endDate,
				Integer.parseInt(pageNum),
				Integer.parseInt(pagesize), huanjing);
		/*if(StringUtils.isEmpty(json)){
		}*/
		return json;
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
	public String messageDetail(HttpServletRequest request, String mobile, String transid, String type,
	                            String startDay, String endDay, String huanjing) {
		Date endDate = DateUtil.parse(endDay);
		if (null == endDate) {
			endDate = new Date();
		}
		List<Message> msgList = messageQuery.queryTid(mobile, transid, type,
				DateUtil.parse(startDay), endDate, huanjing);
		//页面显示处理
		StringBuffer msgBuf = new StringBuffer();
		for (Message msg : msgList) {
			String message = msg.getMessage();
			// line = message.split("\n"),
			// line >50000 --> 用\n截取第一个
			if (message.split("\n").length >= 500) {
				Matcher m = Pattern.compile("\n").matcher(message);
				int mIdx = 0;
				while (m.find()) {
					mIdx++;
					if (mIdx == 500) break;//当"\n"符号第500次出现的位置
				}
				message = message.substring(0, m.start());
			}
			// \n----\n+line+\n---\n+line+...
			msgBuf.append("\n--------------------------------------------------------------------------------------------------------------------------------------\n")
					.append(message);
		}
		// 左右括号处理< ==> "&lt;  > ==> "&gt;
		String dloadDetail = msgBuf.toString().replace("<", "&lt;").replace(">", "&gt;");
		// 换行处理 \n --> <br/>
		String br = dloadDetail.replace("\n", "<br/>");
		// 空格 &nbsp
		String msgDetail = br.replace(" ", "&nbsp");
		request.setAttribute("downloadDetail", dloadDetail);
		request.setAttribute("messageDetail", msgDetail);
		return "msgquery/detail";
	}
}
