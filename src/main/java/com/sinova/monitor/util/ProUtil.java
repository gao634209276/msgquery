package com.sinova.monitor.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

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


	public static Properties load(String properties) {
		Resource defaultRes = new ClassPathResource(properties);
		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(defaultRes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
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
	 *
	 */
	public static Map<String, String> getMap(String properties) {
		Properties props = load(properties);
		Map<String, String> map = new HashMap<>();
		String key;
		assert props != null;
		for (Object o : props.keySet()) {
			key = (String) o;
			map.put(key, props.getProperty(key));
		}
		return map;
	}

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

