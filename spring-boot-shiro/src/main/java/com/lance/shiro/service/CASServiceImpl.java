package com.lance.shiro.service;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.lance.shiro.constant.CASConstant;

@Service
public class CASServiceImpl implements CASSercice {

	private static final Logger LOGGER = LoggerFactory.getLogger(CASServiceImpl.class);

	@Override
	public String getTicketGrantingTicket(HttpServletResponse response, String username, String password) {
		HttpClient client = new HttpClient();
		String url = CASConstant.CAS_SERVER + "/v1/tickets";
		PostMethod post = new PostMethod(url);
		post.setRequestBody(new NameValuePair[] { new NameValuePair("username", username),
				new NameValuePair("password", password) });
		String ticketGrantingTicket = null;
		try {
			client.executeMethod(post);
			String responseResult = post.getResponseBodyAsString();
			switch (post.getStatusCode()) {
			case 201: {
				final Matcher matcher = Pattern.compile(".*action=\".*/(.*?)\".*").matcher(responseResult);
				if (matcher.matches()) {
					ticketGrantingTicket = matcher.group(1);
					// 创建一个Cookie存储TGT
					Cookie cookie = new Cookie("CASTGC", ticketGrantingTicket);
					response.addCookie(cookie);
					return ticketGrantingTicket;
				}
				LOGGER.warn("Successful ticket granting request, but no ticket found!");
				LOGGER.info("responseResult {}", responseResult.substring(0, Math.min(1024, responseResult.length())));
				break;
			}
			default:
				LOGGER.warn("Invalid response code ({}) from CAS server!", post.getStatusCode());
				LOGGER.info(
						"responseResult {}: " + responseResult.substring(0, Math.min(1024, responseResult.length())));
				break;
			}
		} catch (final IOException e) {
			LOGGER.warn("获取TGT发生异常{}", e.getMessage());
		} finally {
			post.releaseConnection();
		}
		return null;
	}

	@Override
	public void removeTicketGrantingTicket(HttpServletRequest request) {
		// DELETE /cas/v1/tickets/TGT-fdsjfsdfjkalfewrihfdhfaie HTTP/1.0
		Cookie[] cookies = request.getCookies();
		String ticketGrantingTicket = null;
		for (Cookie cookie : cookies) {
			if ("CASTGC".equals(cookie.getName())) {
				ticketGrantingTicket = cookie.getValue();
				cookie.setMaxAge(0);
				break;
			}
		}
		if (StringUtils.isNotEmpty(ticketGrantingTicket)) {
			HttpClient client = new HttpClient();
			StringBuilder url = new StringBuilder(CASConstant.CAS_SERVER);
			url.append("/v1/tickets/").append(ticketGrantingTicket);
			DeleteMethod delete = new DeleteMethod(url.toString());
			try {
				client.executeMethod(delete);
				int code = delete.getStatusCode();
				if (200 != code) {
					LOGGER.error("删除TGT失败，返回码为{}", code);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("移除TGT时发生异常{}", e.getMessage());
			} finally {
				delete.releaseConnection();
			}
		}
	}

	@Override
	public boolean validateTicketGrantingTicket(HttpServletRequest request) {
		// GET /cas/v1/tickets/TGT-fdsjfsdfjkalfewrihfdhfaie HTTP/1.0
		Cookie[] cookies = request.getCookies();
		String ticketGrantingTicket = null;
		for (Cookie cookie : cookies) {
			if ("CASTGC".equals(cookie.getName())) {
				ticketGrantingTicket = cookie.getValue();
				break;
			}
		}
		if (StringUtils.isNotEmpty(ticketGrantingTicket)) {
			HttpClient client = new HttpClient();
			StringBuilder url = new StringBuilder(CASConstant.CAS_SERVER);
			url.append("/v1/tickets/").append(ticketGrantingTicket);
			GetMethod get = new GetMethod(url.toString());
			try {
				client.executeMethod(get);
				int code = get.getStatusCode();
				if (200 != code) {
					LOGGER.info("TGT不存在或已失效，原因为{}，返回码为{}", get.getResponseBodyAsString(), code);
				}
			} catch (Exception e) {
				e.printStackTrace();
				LOGGER.error("验证TGT时发生异常{}", e.getMessage());
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean validateServiceTicket(HttpServletRequest request, String serviceTicket) {
		/// cas/p3/serviceValidate?service={service url}&ticket={service ticket}
		return false;
	}

	@Override
	public String getServiceTicket(HttpServletRequest request, HttpServletResponse res, String ticketGrantingTicket,
			String casService) {
		// POST /cas/v1/tickets/{TGT id} HTTP/1.0
		// service={form encoded parameter for the service url}
		return null;
	}

}
