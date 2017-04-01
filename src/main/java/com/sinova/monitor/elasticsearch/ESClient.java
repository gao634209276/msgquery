package com.sinova.monitor.elasticsearch;

import com.sinova.monitor.util.SpringContextUtil;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.*;

import static com.sinova.monitor.elasticsearch.SearchEnv.TIMESTAMP;
import static com.sinova.monitor.util.DateUtil.UTC_Format;
import static com.sinova.monitor.util.DateUtil.msgFormat;


/**
 * 持用ElasticSearch TransportClient对象,提供通用处理方法
 * Created by Noah on 2017/3/31.
 */
public class ESClient {
	public static Client client = SpringContextUtil.getBean("client");

	private static List<String> allOpenindices;

	/**
	 * 通用方法,构建一个查询请求对象
	 */
	public static SearchRequestBuilder buildRequest(String[] indices, int pageNum, int pagesize) {
		SearchRequestBuilder requestBuilder = client.prepareSearch(indices).setExplain(true);
		int from = pageNum - 1 / pagesize;
		if (from > 0)
			requestBuilder.setFrom(pageNum - 1 / pagesize);
		if (pagesize >= 0)
			requestBuilder.setSize(pagesize);
		if (!indices[0].contains("message")) {
			return requestBuilder;
		}
		// 如果是生产集群,生产的indices是有channel和date构成,所以type可以从indices中获取
		Set<String> typeSet = new HashSet<String>();
		for (String index : indices) {
			String type = index.substring(0, index.indexOf("message"));
			typeSet.add(type);
		}
		requestBuilder.setTypes(typeSet.toArray(new String[typeSet.size()]));
		return requestBuilder;
	}

	public static RangeQueryBuilder timestampRange(Date startDate, Date endDate) {
		if (endDate == null) endDate = new Date();
		RangeQueryBuilder timestampRange = QueryBuilders
				.rangeQuery(TIMESTAMP)
				.to(UTC_Format.format(endDate));
		if (null != startDate) timestampRange.from(UTC_Format.format(startDate));
		return timestampRange;
	}


	/**
	 * 仅用于生产,因为测试的index固定
	 *
	 * @param indexPrefix Elasticsearch的前缀，一般为: $env-$channel
	 * @param startDate   可为空，确保为yyyy-MM-dd HH:mm:ss的时间格式，否则解析错误
	 * @param endDate     可为空，确保为yyyy-MM-dd HH:mm:ss的时间格式，否则解析错误
	 * @return 通过es查询所有indices与用户指定时间区间，返回可用的indices
	 */
	public static String[] getIndices(String indexPrefix, Date startDate, Date endDate) {
		// 初始化构建一个可查询的index的集合，目前固定保持20天内的数据，这里限制最多可查询20天
		Date now = new Date();
		Calendar begin = Calendar.getInstance();
		begin.setTime(now);
		begin.add(Calendar.DATE, -20);// default 20 Day ago
		Calendar end = Calendar.getInstance();
		end.setTime(now); // default now
		if (null != startDate)
			begin.setTime(startDate);
		if (null != endDate)
			end.setTime(endDate);
		Set<String> daySet = new HashSet<>();
		while (begin.compareTo(end) <= 0) {
			// prefix-yyyy.MM.dd
			daySet.add(indexPrefix + "-" + msgFormat.format(begin.getTime()));
			begin.add(Calendar.DATE, 1);
		}
		// get All ES allOpenindices from Elasticsearch
		if (null == allOpenindices) {
			updateIndices();
		}
		// get allOpenindices match channel and env
		List<String> matchindices = new ArrayList<>();
		for (String index : allOpenindices) {
			if (index.startsWith(indexPrefix) && daySet.contains(index))
				matchindices.add(index);
		}
		return matchindices.toArray(new String[matchindices.size()]);
	}

	/**
	 * All ES allOpenindices from Elasticsearch
	 * date --> 用于更新updateTime，格式化为：yyyy.MM.dd
	 */
	public static void updateIndices() {
		ClusterStateResponse csr = client.admin().cluster().prepareState()
				.execute().actionGet();
		String[] allOpenIndices = csr.getState().getMetaData()
				.concreteAllOpenIndices();
		allOpenindices = Arrays.asList(allOpenIndices);
	}
}
