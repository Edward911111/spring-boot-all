package com.lance.shiro.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CASSercice {

	public abstract String getTicketGrantingTicket(HttpServletResponse response, String username, String password);

	public abstract String getServiceTicket(HttpServletRequest request, HttpServletResponse response,
			String ticketGrantingTicket, String service);

	public abstract void removeTicketGrantingTicket(HttpServletRequest request);

	public abstract boolean validateTicketGrantingTicket(HttpServletRequest request);

	public abstract boolean validateServiceTicket(HttpServletRequest request, String serviceTicket);
}