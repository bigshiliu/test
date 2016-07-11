package com.hutong.supersdk.util;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.util.ParseJson;

public class SDKConfigUtil {
	
	private static final Log logger = LogFactory.getLog(SDKConfigUtil.class);
	
	/**
	 * 根据平台ID获得平台对应的配置信息
	 * @param configInfo
	 * @param clazz
	 * @return
	 */
	public static <T> T json2Config(String configInfo, Class<T> clazz){
		T obj = null;
		try {
			obj = ParseJson.getJsonContentByStr(configInfo, clazz);
		} catch (Exception e) {
			logger.error("SDK ConfigInfo Parse Error.", e);
		}
		return 	obj;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> configInfoToMap(String configInfo){
		Map<String, String> map = null;
		try {
			map = ParseJson.getJsonContentByStr(configInfo, Map.class);
		} catch (Exception e) {
			logger.error("SDK ConfigInfo Parse Error.", e);
		}
		return 	map;
	}
	
	/**
	 * 生成AppChannelId,生成规则为:sdkId + "_" + appId
	 * @param appId
	 * @param sdkId
	 * @return appChannelId
	 */
	public static String createAppChannelId(String appId, String sdkId){
		StringBuffer appChannelId = new StringBuffer();
		try {
			if(StringUtils.isEmpty(appId) || StringUtils.isEmpty(sdkId))
				throw new NullPointerException("Missing Parameter.");
			appChannelId.append(sdkId + "_" + appId);
		} catch (Exception e) {
			logger.error("Create AppChannelId Error.", e);
		}
		return appChannelId.toString();
	}
}
