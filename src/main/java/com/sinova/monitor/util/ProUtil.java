package com.sinova.monitor.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

/**
 */
public class ProUtil {

	public static Properties getProperties(String properties) {
		Resource defaultRes = new ClassPathResource(properties);
		Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(defaultRes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return props;
	}

	public static Map<String, String> getMap(String properties) {
		Properties props = getProperties(properties);
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

	/**
	 * 如：cu.query.currentfee||当月话费查询
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
}

