package com.sinova.monitor.elasticsearch;

import com.sinova.monitor.util.ProUtil;

/**
 * 查询常量配置
 * Created by noah on 2017/4/1.
 */
public class SearchEnv {
	public static String TIMESTAMP = ProUtil.get("es.timestamp", "@timestamp");
	public static String MESSAGE = ProUtil.get("es.source.message");
	public static String TYPE = ProUtil.get("es.source.type");
	public static String MOBILE = ProUtil.get("es.source.mobile");
	public static String TRANSID = ProUtil.get("es.source.transid");
	public static String INTER = ProUtil.get("es.source.inter");
	public static String PATH = ProUtil.get("es.source.path");
	public static String SERVERIP = ProUtil.get("es.source.serverip");
	public static String INTER_ALL = ProUtil.get("inter.all");
	public static String HOST = ProUtil.get("es.source.host");
}
