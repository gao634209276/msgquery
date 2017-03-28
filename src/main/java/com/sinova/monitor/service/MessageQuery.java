package com.sinova.monitor.service;

import static com.sinova.monitor.util.Constant.*;
import static com.sinova.monitor.util.DateUtil.parse;

import com.sinova.monitor.util.ProUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 1，匹配索引
 * 2，查询
 * 3，处理结果
 */
//@Scope("request")
//@Service
public class MessageQuery {
	private static final Log log = LogFactory.getLog(MessageQuery.class);

	public Map<String, Object> getMessage(String inter, String keywords, String channel, String startDay, String endDay, String pageNum, String env) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("reponse_code", "0000");
		String pagesize = ProUtil.getProperties("common.properties")
				.getProperty("message.pagesize");//56
		//index ${channel}-${env}-YYYY.MM.dd
		// index prefix = ${channel}-${env}
		StringBuffer prefixBuf = new StringBuffer();
		if ("product".equals(env)) {
			prefixBuf.append(channel).append("message");
		} else {
			// test-mob test-web, pre-mob,pre-web
			prefixBuf.append(env).append("-").append(channel);
		}
		String indexPrefix = prefixBuf.toString();
		if (StringUtils.isEmpty(indexPrefix))
			return resultMap;
		// get All ES indices
		Client client = SpringContextUtil.getBean("client");
		ClusterStateResponse csr = client.admin().cluster().prepareState()
				.execute().actionGet();
		String[] allIndices = csr.getState().getMetaData()
				.concreteAllOpenIndices();
		// get indices match channel and env
		Set<String> indicex = new HashSet<>();
		String index;

		SimpleDateFormat df = new SimpleDateFormat("YYYY.MM.dd");
		long startTimeMillis = 0;
		long endTimeMillis = new Date().getTime();
		try {
			if (StringUtils.isEmpty(startDay))
				startTimeMillis = df.parse(startDay).getTime();
			if (StringUtils.isEmpty(endDay))
				endTimeMillis = df.parse(endDay).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//YYYY.MM.dd -->
		for (int i = 0; i < allIndices.length; i++) {
			index = allIndices[i];
			if (index.startsWith(indexPrefix)) {
				// ge startDay && le endDay

				indicex.add(allIndices[i]);
			}
		}
		indicex.contains("s");

		// date
		//if(StringUtils.isEmpty(startDay))
		Date startD = parse(startDay);//yyyy-MM-dd HH:mm:ss
		Date endD = parse(endDay);
		HashSet<String> set = new HashSet<>();
		set.retainAll(set);
		return null;

	}

	/**
	 * 所有的indices -->符合env和channle的indices
	 * -->规定日期范围的indices
	 */
	public List<String> filterIndex(List<String> indices) {

		return null;
	}

	List<String> getIndices(String channel, Date startDay, Date endDay, String huanjing) {

		return null;
	}


}
