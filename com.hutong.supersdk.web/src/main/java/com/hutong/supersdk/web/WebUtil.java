package com.hutong.supersdk.web;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class WebUtil {

	/**
	 * push request params key-value to map, the values are all url decoded
	 */
	public static final Map<String, String> getParamMapByRequest(HttpServletRequest request) {
		return (Map<String, String>)request.getParameterMap();
	}

	public static String getSimpleExceptionInfo(Exception e) {
		StringBuffer logMsg = new StringBuffer(e.toString());
		StackTraceElement[] stes = e.getStackTrace();
		if(null != stes && stes.length > 0){
			logMsg.append("\n\t@ ").append(stes[0]);
		}			
		return logMsg.toString();
	}

	/**
	 * 获取IP,若获取失败返回null
	 * 
	 * @param request
	 * @return
	 */
	public static final String getIpAddr(HttpServletRequest request) {
		// Enumeration<String> xxx = request.getHeaderNames();
		// while (xxx.hasMoreElements()) {
		// String name = xxx.nextElement();
		// System.out.println(name + ":	" + request.getHeader(name));
		// }
		String ip = null;
		try {
			ip = request.getHeader("x-forwarded-for");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
		} catch (Exception e) {
		}
		return ip;
	}
	
	
	public static final String getUri(HttpServletRequest request) {
		String uri = request.getScheme() + "://" +
	             request.getServerName() + 
	             ("http".equals(request.getScheme()) && request.getServerPort() == 80 || "https".equals(request.getScheme()) && request.getServerPort() == 443 ? "" : ":" + request.getServerPort() ) +
	             request.getRequestURI() +
	            (request.getQueryString() != null ? "?" + request.getQueryString() : "");
		return uri;
	}
	
}
