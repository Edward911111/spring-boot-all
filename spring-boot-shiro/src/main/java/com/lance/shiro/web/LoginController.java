package com.lance.shiro.web;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.lance.shiro.service.CASSercice;

@Controller
public class LoginController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class); 
	
	@Resource
	private CASSercice casSercice;

	/**
	 * Go login.jsp
	 * @return
	 */
	@RequestMapping(value="login", method=RequestMethod.GET)
	public String login() {
		return "login.jsp";
	}
	
	/**
	 * Go login
	 * @param request
	 * @return
	 */
	@RequestMapping(value="login", method=RequestMethod.POST)
	public String login(HttpServletRequest request, HttpServletResponse response, RedirectAttributes rediect) {
		String account = request.getParameter("account");
		String password = request.getParameter("password");
		String TGT = casSercice.getTicketGrantingTicket(response, account, password);
		LOGGER.info("获取到的TGT为{}", TGT);
		UsernamePasswordToken upt = new UsernamePasswordToken(account, password);
		Subject subject = SecurityUtils.getSubject();
		try {
			subject.login(upt);
		} catch (AuthenticationException e) {
			e.printStackTrace();
			rediect.addFlashAttribute("errorText", "您的账号或密码输入错误!");
			return "redirect:/login";
		}
		return "redirect:/index";
	}
	
	/**
	 * Exit
	 * @return
	 */
//	@RequestMapping("logout")
//	public String logout(HttpServletRequest request) {
//		casSercice.removeTicketGrantingTicket(request);
//		Subject subject = SecurityUtils.getSubject();
//		subject.logout();
//		return "redirect:/index";
//	}
	
	@ResponseBody
	@RequestMapping("logouting")
	public Map<String, Object> logout(HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		casSercice.removeTicketGrantingTicket(request);
		Subject subject = SecurityUtils.getSubject();
		subject.logout();
		resultMap.put("是否登出", true);
		return resultMap;
	}
}
