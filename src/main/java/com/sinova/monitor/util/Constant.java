package com.sinova.monitor.util;

public class Constant {
	//	public static final String  INDEX_PREFIX="message";
	// 三厅
	public static final String MOB_INDEX_PREFIX = "mobmessage";
	public static final String WEB_INDEX_PREFIX = "webmessage";
	public static final String SMS_INDEX_PREFIX = "smsm";
	public static final String MARVEL_INDEX_PREFIX = ".marvel";

	public static final String PRE_MOB_INDEX_PREFIX = "yfb-mob";
	public static final String PRE_WEB_INDEX_PREFIX = "yfb-web";

	public static final String PRE_SMS_INDEX_PREFIX = "yfb-smsm";
	public static final String TEST_WEB_INDEX_PREFIX = "test-web";

	public static final String TEST_MOB_INDEX_PREFIX = "test-mob";

	//查询所有channel
	public static final String SMS_CHANNEL = "sms";
	public static final String WEB_CHANNEL = "web";
	public static final String MOB_CHANNEL = "mob";
	public static final String MOBWAP_CHANNEL = "mobwap";
	public static final String MOBSTD_CHANNEL = "mobstandard";
	//报文后缀日志格式
	public static final String INDEX_SUFFIX_DATE_FORMAT = "yyyy.MM.dd";
	//默认查询字段
	//public static final String PRE_MESSAGE_MARK = "prepare";
	public static final String MESSAGE_MARK = "message";
	public static final String MESSAGE_TRANSID = "transid";

	// 接口名称e.g.  cu.doQuery.accountbalance
	public static final String INTER_REGEX = "cu(\\.\\w+){2}";
}
