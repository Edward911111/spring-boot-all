package com.lance.shiro.util;

import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigUtils.class);

	public static void initConfig(String configFileName, Class<?> mappingClass) {
		Map<String, String> map = convertToMap(configFileName);
		mapping(map, mappingClass);
		LOGGER.info("loading config file successfully");
	}

	public static Map<String, String> convertToMap(String configFileName) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			Properties prop = new Properties();
			prop.load(new InputStreamReader(ConfigUtils.class.getClassLoader().getResourceAsStream(configFileName),
					"utf-8"));
			for (Object k : prop.keySet()) {
				Object v = prop.get(k);
				String key = String.valueOf(k), value = String.valueOf(v);
				map.put(key, value);
			}

		} catch (Exception e) {
			LOGGER.error("Occuring an exception when to execute the method 'convertToMap' for {}", e);
		}
		return map;
	}

	public static void mapping(Map<String, String> map, Class<?> mappingClass) {
		try {
			Field[] fields = mappingClass.getFields();
			for (Field f : fields) {
				String val = map.get(f.getName());
				if (StringUtils.isEmpty(val)) {
					continue;
				}
				if (f.getType() == Integer.class || f.getType() == int.class) {
					f.set(mappingClass, Integer.valueOf(val));
				} else if (f.getType() == Byte.class || f.getType() == byte.class) {
					f.set(mappingClass, Byte.valueOf(val));
				} else if (f.getType() == Long.class || f.getType() == long.class) {
					f.set(mappingClass, Long.valueOf(val));
				} else if (f.getType() == Short.class || f.getType() == short.class) {
					f.set(mappingClass, Short.valueOf(val));
				} else if (f.getType() == String.class) {
					f.set(mappingClass, String.valueOf(val));
				} else if (f.getType() == Double.class || f.getType() == double.class) {
					f.set(mappingClass, Double.valueOf(val));
				} else if (f.getType() == Float.class || f.getType() == float.class) {
					f.set(mappingClass, Float.valueOf(val));
				} else if (f.getType() == Boolean.class || f.getType() == boolean.class) {
					f.set(mappingClass, Boolean.valueOf(val));
				} else if (f.getType() == List.class || f.getType() == ArrayList.class
						|| f.getType() == LinkedList.class) {
					List<String> list = new ArrayList<String>();
					Collections.addAll(list, val.split(","));
					f.set(mappingClass, list);
				} else {
					throw new Exception("unsupported data type");
				}
			}

		} catch (Exception e) {
			LOGGER.error("Occuring an exception when to execute the method 'mapping' for {}", e);
		}

	}

}
