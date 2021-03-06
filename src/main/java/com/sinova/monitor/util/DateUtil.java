package com.sinova.monitor.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期解析转换的工具类
 */
public class DateUtil {
	// 日期格式yyyy-MM-dd字符串常量
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	// 日期格式HH:mm:ss字符串常量
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String DATETIME_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	public static SimpleDateFormat defatulFormat = new SimpleDateFormat(DATETIME_FORMAT);
	public static SimpleDateFormat UTC_Format = new SimpleDateFormat(DATETIME_UTC_FORMAT);
	public static SimpleDateFormat msgFormat = new SimpleDateFormat("yyyy.MM.dd");
	public static SimpleDateFormat dayFormat = new SimpleDateFormat(DATE_FORMAT);

	/**
	 * 默认解析为yyyy-MM-dd HH:mm:ss的时间格式
	 */
	public static Date parse(String strDate) {
		return parse(strDate, defatulFormat);
	}

	/**
	 * 指定特定的格式解析日期
	 *
	 * @param strDate 格式错误或者为空，返回null
	 */
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
