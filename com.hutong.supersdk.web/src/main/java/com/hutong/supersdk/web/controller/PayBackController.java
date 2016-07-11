package com.hutong.supersdk.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.iservice.server.IPayServerService;
import com.hutong.supersdk.web.WebUtil;

@Controller
public class PayBackController {
	private static final Log logger = LogFactory.getLog(PayBackController.class);
	
	public PayBackController(){
		logger.info("new PayBackController");
	}

	/**
	 * SuperSDK Web组件与PayBack组件的调用入
	 * @param shortId AppShortId
	 * @param channelId App渠道Id
	 * @param request
	 */
	@RequestMapping("/payback/{shortId}/{channelId}")
	@ResponseBody
	public Object doService(@PathVariable final String shortId, @PathVariable final String channelId, 
			final HttpServletRequest request) {
		logger.info("ShortId:" + shortId + " channelId:" + channelId);
		
		long startTime = System.currentTimeMillis();
		
		Object result = "";
		Map<String, String> paramMap = null;
		
		try {
			request.setCharacterEncoding("UTF-8");
			
			paramMap = WebUtil.getParamMapByRequest(request);
			
			//获取SuperSDKCenter Service管理器中的IAppService的具体实现类
			IPayServerService payServerService = SuperSDK.getInstance().getService(IPayServerService.class);
			
			result = payServerService.payBack(shortId, channelId, paramMap, request.getInputStream(), request.getMethod());
		} catch (SuperSDKException e) {
			logger.error("SuperSDKException in PayBack shortId=" + shortId + " channelId=" + channelId, e);
		} catch (Exception e) {
			logger.error("Exception in PayBack shortId=" + shortId + " channelId=" + channelId, e);
		}
		
		if(logger.isDebugEnabled()){
			StringBuffer sb = new StringBuffer();
			try {
				
				sb.append("\t" + WebUtil.getUri(request));
				
				sb.append("\t@Request[");
				sb.append(System.currentTimeMillis() - startTime);
				sb.append("ms]: channelId:");
				sb.append(channelId);
				sb.append(" request IP : " + WebUtil.getIpAddr(request) + " ");
				sb.append(ParseJson.encodeJson(paramMap));
				sb.append("\t@Response: ");
				sb.append(ParseJson.encodeJson(result));
			} catch (Exception e) {
				logger.warn("Log Error", e);
			}
			logger.debug(sb.toString());
		}
		return result;
	}
}
