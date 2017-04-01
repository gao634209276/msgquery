package com.sinova.monitor.service;

import com.sinova.monitor.model.Message;
import org.elasticsearch.search.SearchHit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sinova.monitor.elasticsearch.SearchEnv.*;

/**
 * 为一个service提供一个通用的可选处理方法
 * Created by noah on 2017/4/1.
 */
public class MessageDeal {

	public static Pattern msgPattern = Pattern.compile("INFO\\s\\S+\\:[0-9]+");


	/**
	 *
	 * @param hits Elasticsearch查询结果的SearchHit
	 * @return 经过遍历转化和匹配，返回数据保证在页面能正常显示
	 */
	public static String hits2Detail(SearchHit[] hits) {
		List<Message> msgList = new ArrayList<Message>();
		for (SearchHit hit : hits) {
			Map<String, Object> hitMap = hit.getSource();
			// 获取日志的行号
			Matcher m = msgPattern.matcher((String) hitMap.get(MESSAGE));
			int codeNum = 0;
			if (m.find()) {
				String logInfo = m.group(0);
				int codeSplit = logInfo.indexOf(":");
				if (codeSplit > 0) {
					codeNum = Integer.parseInt(logInfo.substring(codeSplit + 1));
				}
			}
			// 报文加上服务器，节点; message = ip+path+message
			String msgstr = "服务器：" + hitMap.get(SERVERIP) + "      "
					+ "节点：" + hitMap.get(PATH) + "\n"
					+ hitMap.get(MESSAGE);
			// 对象按log code Number排序
			Message message = new Message();
			message.setCode(codeNum);
			message.setMessage(msgstr);
			msgList.add(message);
		}
		Collections.sort(msgList);
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
		return msgBuf.toString();

	}


	public static String prefix_matcherFirst(String prefix, String patten, String resource, int flags) {
		if (resource == null || resource.equals("")) return null;
		Pattern p = Pattern.compile(prefix, flags);
		Matcher m = p.matcher(resource);
		Pattern pp = Pattern.compile(prefix + patten, flags);
		Matcher mpp = pp.matcher(resource);

		while (m.find() && mpp.find()) {
			return resource.substring(m.end(), mpp.end());
		}
		return null;
	}

	public static String prefix_matcherFirst_surfix(String prefix, String surfix, String patten, String resource, int flags) {
		if (resource == null || resource.equals("")) return null;
		Pattern p = Pattern.compile(prefix + patten + surfix, flags);
		Matcher m = p.matcher(resource);
		while (m.find()) {
			return resource.substring(prefix.length() + m.start(), m.end() - surfix.length());
		}
		return null;
	}

	public static String parseXML(String xml, String beginTag, String endTag) {
		String value = null;
		int startTagIndex = xml.indexOf(beginTag);
		if (startTagIndex < 0) return null;
		int startIndex = startTagIndex + beginTag.length();
		int endIndex = xml.indexOf(endTag, startIndex);
		try {
			// value=xml.substring(xml.indexOf(beginTag)+beginTag.length(), xml.indexOf(endTag));
			value = xml.substring(startIndex, endIndex);
		} catch (Exception e) {
			e.printStackTrace();
			// log.info(message);
		}
		return value;
	}


}
