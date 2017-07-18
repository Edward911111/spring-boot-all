package com.lance.shiro.constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CASConstant {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CASConstant.class);
	
	public static String CAS_SERVER = "";
	
	static {
		InputStream inputStream = null;
		try {
			Properties properties = new Properties();
			inputStream = CASConstant.class.getClassLoader().getResourceAsStream("conf/cas.properties");
			properties.load(inputStream);
			CAS_SERVER = properties.getProperty("cas.server.address");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("配置CAS服务器参数失败{}", e.getMessage());
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
