package com.lance.shiro.config;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lance.shiro.service.CASSercice;

@Component("customCASAuthenticationFilter")
public class CustomCASAuthenticationFilter extends FormAuthenticationFilter {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomCASAuthenticationFilter.class);
	
	@Resource
	private CASSercice casService;

	/**
	 * 所有请求都会经过的方法。
	 */
	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpservletrequest = (HttpServletRequest) request;
		Subject subject = SecurityUtils.getSubject();
		// 是登录的请求
		if (isLoginRequest(request, response)) 
		{
			JSONObject reqJson = JSON.parseObject(IOUtils.toString(httpservletrequest.getInputStream()));
			String username = reqJson.getString("userName");
			String password = reqJson.getString("pwd");
			LOGGER.info("===========================login parmas is:登录名:{},密码{}", username, password);
			UsernamePasswordToken token = new UsernamePasswordToken(username,password);
			casService.getTicketGrantingTicket((HttpServletResponse) response, username, password);
			try 
			{
				subject.login(token);  // 凭证不匹配的时候要捕获异常，这样子才能确认是什么样的错误。
				return true ;
			} 
			catch (Exception e) 
			{
				LOGGER.error("请求认证CAS时发生异常{}", e);
				return false;
			}
		} else if(!isAllowedUrl(httpservletrequest)) {
			return casService.validateTicketGrantingTicket(httpservletrequest);
		} else {
			return true;
		}
	}

	private String getRequestUrl(ServletRequest request) {
		HttpServletRequest req = (HttpServletRequest)request;
		String queryString = req.getQueryString();
		queryString = StringUtils.isBlank(queryString)?"": "?"+queryString;
		return req.getRequestURI()+queryString;
	}
	
	private boolean isAllowedUrl(ServletRequest request) {
		String url = getRequestUrl(request);
		return StringUtils.endsWithAny(url, ".js",".css",".html") || StringUtils.endsWithAny(url, ".jpg",".png",".gif", ".jpeg") || StringUtils.equals(url, "/unauthor");
	}
	
//	private void whriteJsonToResponse(ServletResponse response, Map<String, Object> result) throws IOException {
//		HttpServletResponse resp = (HttpServletResponse)response;
//		if(200 != resp.getStatus())
//		{
//			LOGGER.info("================result={}",result);
//		}
//		response.setContentType("application/json;charset=UTF-8");
//		PrintWriter out = response.getWriter();
//		out.print(JSON.toJSONString(result));
//		out.flush();
//		out.close();
//	}
}
