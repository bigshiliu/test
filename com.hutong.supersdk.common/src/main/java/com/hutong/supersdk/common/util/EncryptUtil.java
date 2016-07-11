package com.hutong.supersdk.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hutong.supersdk.common.constant.DataKeys;

/**
 * 签名工具类
 * @author QINZH
 *
 */
public class EncryptUtil {
	
	/**
	 * 生成签名值
	 * @param params 请求参数
	 * @param signKey 签名Key
	 * @return
	 */
	public static String generateSign(Map<String, String> params, String signKey) {
		String sigString = generateNormalizedString(params, DataKeys.Common.SIGN);
		String sign = MD5Util.MD5(signKey + sigString + signKey);
		return sign;
	}
	
	/**
	 * 对http请求参数作字典排序，拼接字符串,不包括签名
	 */
	public static final String generateNormalizedString(Map<String, String> paramMap, String sigParamKey) {
		Set<String> params = paramMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String paramKey : sortedParams) {
			if (paramKey.equals(sigParamKey)) {
				continue;
			}
			String valueStr = paramMap.get(paramKey);
			sb.append(paramKey).append('=').append(null == valueStr ? "" : Base64Util.encode(valueStr.getBytes()));
		}
		return sb.toString();
	}
	
	public static boolean checkSign(Map<String, String> params, String signKey) {
		String sign = null;
		if (params.containsKey(DataKeys.Common.SIGN)) {
			sign = params.get(DataKeys.Common.SIGN);
		}
		else {
			return false;
		}
		
		String checkSign = generateSign(params, signKey);
		
		if (StringUtil.equalsStringIgnoreCase(sign, checkSign)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public static void main(String[] args) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(DataKeys.Platform.PLATFORM_USER_TOKEN, "token");
		paramMap.put(DataKeys.Platform.PLATFORM_USER_ID, "userid");
		paramMap.put(DataKeys.Platform.APP_ID, "appid");
		System.out.println(EncryptUtil.generateSign(paramMap, "***"));
	}
}
