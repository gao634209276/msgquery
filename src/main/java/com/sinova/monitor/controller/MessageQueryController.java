package com.sinova.monitor.controller;

import com.sinova.monitor.service.MessageQuery;
import com.sinova.monitor.util.DateUtil;
import com.sinova.monitor.util.ProUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.sinova.monitor.elasticsearch.ESClient.getIndices;
import static com.sinova.monitor.elasticsearch.ESClient.updateIndices;

/**
 * Controller根据环境调用不同的service层处理
 * Created by Noah on 2017/3/31.
 */
@Controller
@RequestMapping("/msgquery")
public class MessageQueryController {
	private Logger log = LoggerFactory.getLogger(MessageQueryController.class);

	@Autowired
	@Qualifier(value = "messageQueryImpl")
	private MessageQuery msgQueryImpl;
	@Autowired
	@Qualifier(value = "messageQueryTest")
	private MessageQuery msgQueryTest;

	@Value("#{common['message.pagesize']}")
	private String pagesize;

	@RequestMapping("/list.htm")
	public String getMessageList(HttpServletRequest request) {
		List<String> intersList = ProUtil.toList(ProUtil.load("interList.properties"));
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
		// 用于计算时间
		long beginTime = System.currentTimeMillis();
		Date endDate = DateUtil.parse(endDay);
		if (null == endDate) {
			endDate = new Date();
		}
		Date startDate = DateUtil.parse(startDay);
		updateIndices();
		String json;
		if ("product".equals(huanjing)) {
			String indexPrefix = channel + "message";
			String[] indices = getIndices(indexPrefix, startDate, endDate);
			if (indices.length == 0)
				return "0002";
			json = msgQueryImpl.queryMessage(indices, inter, keywords, startDate, endDate,
					Integer.parseInt(pageNum), Integer.parseInt(pagesize));
		} else if ("pre".equals(huanjing)) {
			json = msgQueryTest.queryMessage(new String[]{"yfb-web-2017.04.01"},
					inter, keywords, startDate, endDate,
					Integer.parseInt(pageNum), Integer.parseInt(pagesize));
		} else {
			json = msgQueryTest.queryMessage(new String[]{huanjing},
					inter, keywords, startDate, endDate,
					Integer.parseInt(pageNum), Integer.parseInt(pagesize));
		}
		long useTime = (System.currentTimeMillis() - beginTime) / 1000;
		log.info("请求用时为" + useTime + "秒");
		return json;
	}

	/**
	 * txt文件导出
	 */
	@RequestMapping("/gettxt.json")
	public
	@ResponseBody
	String getTxt(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		// request.setCharacterEncoding("UTF-8");
		String detail = request.getParameter("detail");
		String fileName = "message" + new Date().getTime() + ".txt";
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		return detail;
	}

	/**
	 * 详情页面展示
	 */
	@RequestMapping("/messageDetail.htm")
	public String messageDetail(HttpServletRequest request, String mobile, String transid, String channel,
	                            String startDay, String endDay, String huanjing) {
		long beginTime = System.currentTimeMillis();
		Date endDate = DateUtil.parse(endDay);
		if (null == endDate) {
			endDate = new Date();
		}
		Date startDate = DateUtil.parse(startDay);
		String msg ;
		if ("product".equals(huanjing)) {
			String indexPrefix = channel + "message";
			String[] indices = getIndices(indexPrefix, startDate, endDate);
			if (indices.length == 0) return "0002";
			msg = msgQueryImpl.queryDetail(indices, mobile, transid, startDate, endDate);
		} else if ("pre".equals(huanjing)) {
			msg = msgQueryTest.queryDetail(new String[]{"yfb-web-2017.04.01"}, mobile, transid, startDate, endDate);
		} else {
			msg = msgQueryTest.queryDetail(new String[]{huanjing}, mobile, transid, startDate, endDate);
		}
		// 左右括号处理< ==> "&lt;  > ==> "&gt;
		String dloadDetail = msg.replace("<", "&lt;").replace(">", "&gt;");
		// 换行处理 \n --> <br/>
		String br = dloadDetail.replace("\n", "<br/>");
		// 空格 &nbsp
		String msgDetail = br.replace(" ", "&nbsp");
		request.setAttribute("downloadDetail", dloadDetail);
		request.setAttribute("messageDetail", msgDetail);
		long useTime = (System.currentTimeMillis() - beginTime) / 1000;
		log.info("请求用时为" + useTime + "秒");
		return "msgquery/detail";
	}
}
