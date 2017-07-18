package com.lance.shiro.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.lance.shiro.domain.UserInfo;

@Controller
@RequestMapping("/user")
public class UserController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
	
	@ResponseBody
	@RequestMapping("getUserInfo")
	public Map<String, Object> getUserInfo(HttpServletRequest request) {
		try {
			String username = (String) SecurityUtils.getSubject().getPrincipal();
			LOGGER.info("用户信息{}", username);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", username);
			return map;
		} catch (Exception e) {
			LOGGER.info("获取用户失败！");
			return null;
		}
	}

}
