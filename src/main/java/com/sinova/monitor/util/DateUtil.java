package com.sinova.monitor.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 */
public class DateUtil {
	public static String timePattern = "HH:mm";
	// 日期格式yyyy-MM字符串常量
	public static final String MONTH_FORMAT = "yyyy-MM";
	// 日期格式yyyy-MM-dd字符串常量
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	// 日期格式HH:mm:ss字符串常量
	public static final String HOUR_FORMAT = "HH:mm:ss";
	//日期格式yyyy-MM-dd HH:mm:ss字符串常量
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	// 某天开始时分秒字符串常量 00:00:00
	public static final String DAY_BEGIN_STRING_HHMMSS = " 00:00:00";
	// 某天结束时分秒字符串常量 23:59:59
	public static final String DAY_END_STRING_HHMMSS = " 23:59:59";

	public static SimpleDateFormat defatulFormat = new SimpleDateFormat(DATETIME_FORMAT);
	public static SimpleDateFormat UTC_Format = new SimpleDateFormat(DATETIME_UTC_FORMAT);

	public static Date parse(String strDate) {
		return parse(strDate, defatulFormat);
	}

	public static Date parse(String strDate, SimpleDateFormat df) {
		Date date = null;
		try {
			if (!StringUtils.isEmpty(strDate)) {
				date = df.parse(strDate);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
}