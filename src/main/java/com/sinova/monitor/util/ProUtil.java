package com.sinova.monitor.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

/**
 * Properties工具类，
 * 并封装保存配置文件common.properties对应的Properties
 * 可以通过封装后的get获取配置
 */
public class ProUtil {

	private static Properties common;

	static {
		Resource defaultRes = new ClassPathResource("common.properties");
		try {
			common = PropertiesLoaderUtils.loadProperties(defaultRes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return common.getProperty(key);
	}

	public static String get(String key, String defaultValue) {
		return common.getProperty(key, defaultValue);
	}



	/**
	 * @param proFile properties文件的path
	 * @return 返回properties文件对应的Properties对象
	 */
	public static Properties load(String proFile) {
		Resource defaultRes = new ClassPathResource(proFile);
		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(defaultRes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}
	/**
	 * @param proFile properties文件的path
	 * @return 返回properties文件对应的Map对象
	 */
	public static Map<String, String> Map(String proFile) {
		Properties props = load(proFile);
		Map<String, String> map = new HashMap<>();
		String key;
		assert props != null;
		for (Object o : props.keySet()) {
			key = (String) o;
			map.put(key, props.getProperty(key));
		}
		return map;
	}

	/**
	 * 如：cu.doQuery.currentfee||当月话费查询
	 */
	public static List<String> toList(Properties properties) {
		String interSeparator = "||";
		List<String> list = new ArrayList<>();
		String key;
		for (Object o : properties.keySet()) {
			key = (String) o;
			list.add(key + interSeparator + properties.get(key));
		}
		return list;
	}

	/**
	 * 类型转换Properties to Map
	 *
	 * @param properties 待转换的Properties
	 * @return 返回一个Map类型
	 */
	public Map<String, String> toMap(Properties properties) {
		Map<String, String> map = new HashMap<>();
		String key;
		for (Object o : properties.keySet()) {
			key = (String) o;
			map.put(key, properties.getProperty(key));
		}
		return map;
	}
}

