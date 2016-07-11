package com.hutong.supersdk.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class TestController {
	
	private static final Log logger = LogFactory.getLog(TestController.class);
	
	public TestController() {
		logger.info("new TestController");
	}

	@RequestMapping("/test")
	@ResponseBody
	public Object test(HttpServletResponse response) {
        response.setContentType("text/plain;charset=UTF-8");
		response.setHeader("Connection", "close");
		return "ok";
	}

}
