package com.hutong.supersdk.sdk.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.util.MD5Util;
import com.hutong.supersdk.common.util.StringUtil;

/**
 * Sign签名工具类
 * @author QINZH
 *
 */
public class SignUtil {
	
	/**
	 * 将Map-String,String转换成Map-String,Object
	 * @param paramMap
	 * @return
	 */
	public static Map<String, Object> convert(Map<String, String> paramMap) {
		Map<String, Object> encMap = new HashMap<String, Object>();
		for (Entry<String, String> entry : paramMap.entrySet()) {
			encMap.put(entry.getKey(), entry.getValue());
		}
		return encMap;
	}
	
	/**
	 * 将Map数据组装成待签名字符串
	 * value1=key1&value2=key2&value3=key3...
	 * @param paramMap 待签名的参数列表
	 * @param notIn 不参与签名的参数名列表
	 * @param removeNull null值是否进行签名
	 * @param splitStr 连接符 
	 * @return 待签名字符串。如果参数paramMap为null，将返回null
	 */
	public static String createSignData(Map<String, Object> paramMap, String[] notIn, boolean removeNull, String splitStr) {
		if (null == paramMap) {
			return null;
		}

		StringBuilder content = new StringBuilder();

		// 按照key排序
		List<String> notInList = null;
		if (null != notIn) {
			notInList = Arrays.asList(notIn);
		}
		List<String> keys = new ArrayList<String>(paramMap.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			
			/**
			 * 如果是true  则表示null值进行签名,直接跳出if 
			 * 如果是false 则表示null值不进行签名,如果值为空字符串,
			 */
			if(!removeNull && ( null == keys.get(i) || "".equals(keys.get(i))))
				continue;
			
			if (notIn != null && notInList.contains(key))
				continue;

			String value = paramMap.get(key) == null ? "" : paramMap.get(key).toString();
			/**
			 * 如果有特殊连接符,则拼接在key=value后面
			 */
			content.append(key).append("=").append(value);
			if(!"".equals(splitStr))
				content.append(splitStr);
		}
		/**
		 * 删除最后一个特殊连接符
		 */
		String result = content.toString();
		if(!"".equals(splitStr))
			result = content.substring(0,content.lastIndexOf(splitStr));

		return result;
	}
	
	/**
	 * 将Map数据组装成待签名字符串
	 * value1value2value3...
	 * @param paramMap 待签名的参数列表
	 * @param notIn 不参与签名的参数名列表
	 * @param removeNull null值是否进行签名
	 * @param splitStr 连接符 
	 * @return 待签名字符串。如果参数paramMap为null，将返回null
	 */
	public static String createSignString(Map<String, String> paramMap, String[] notIn, boolean removeNull, String splitStr) {
		if (null == paramMap) {
			return null;
		}

		StringBuilder content = new StringBuilder();

		// 按照key排序
		List<String> notInList = null;
		if (null != notIn) {
			notInList = Arrays.asList(notIn);
		}
		List<String> keys = new ArrayList<String>(paramMap.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			
			/**
			 * 如果是true  则表示null值进行签名,直接跳出if 
			 * 如果是false 则表示null值不进行签名,如果值为空字符串,
			 */
			if(!removeNull && ( null == keys.get(i) || "".equals(keys.get(i))))
				continue;
			
			if (notIn != null && notInList.contains(key))
				continue;
			
			String value = paramMap.get(key) == null ? "" : paramMap.get(key).toString();
			content.append(value);
			if(!"".equals(splitStr))
				content.append(splitStr);
		}
		/**
		 * 删除最后一个特殊连接符
		 */
		String result = content.toString();
		if(!"".equals(splitStr))
			result = content.substring(0,content.lastIndexOf(splitStr));
		return result;
	}
	
	/**
	 * 通用延签方法,所有参数按照key自然排序,前后拼接secretKey,再进行MD5加密,结果和sign对比
	 * @param paramMap
	 * @param secretKey
	 * @return
	 */
	public static boolean chechSign(Map<String, String> paramMap, String secretKey) {
		String sign = paramMap.get(DataKeys.Common.SIGN);
		String mySignStr = createSignString(paramMap, new String[]{"sign"}, true, "");
		String mySign = MD5Util.MD5(secretKey + mySignStr + secretKey);
		return StringUtil.equalsStringIgnoreCase(sign, mySign);
	}
	
	public static void main(String[] args) {
		Map<String, String> testMap = new HashMap<String, String>();
		testMap.put("id", "33308fa950784b95a0fcb0499840b665");
		testMap.put("token", "3319a6335f5441acba584f0a5cbd466a");
		System.out.println(MD5Util.MD5("testsecretkey"+createSignString(testMap, null, true, "")+"testsecretkey"));
	}
	
}
