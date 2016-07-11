package com.hutong.supersdk.sdk.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.util.PropertiesUtil;

/**
 * URL工具类
 * @author QINZH
 *
 */
public class UrlUtil {
	
	private final static Log logger = LogFactory.getLog(UrlUtil.class);
	
	private final static String SUPERSDK_CONFIG_NAME = "superSDKConfig";
	
	/**
	 * 获取SDK支付回调地址
	 * @param appId
	 * @param channelId
	 * @return
	 */
	public static String getSDKPayBackUrl(String shortAppId, String channelId){
		if(StringUtils.isEmpty(shortAppId) || StringUtils.isEmpty(channelId))
			return "";
		StringBuffer sb = new StringBuffer();
		//http://ssdk.sail2world.com/
		sb.append(PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "superSDK_url"));
		//superSDK应用名
		sb.append("supersdk-web/payback/");
		//短AppId,shortAppId
		sb.append(shortAppId);
		sb.append("/");
		//渠道ID
		sb.append(channelId);
		logger.debug("UrlUtils SDKPayBackUrl:" + sb.toString());
		return sb.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(UrlUtil.getSDKPayBackUrl("test", "10001"));
	}
}
