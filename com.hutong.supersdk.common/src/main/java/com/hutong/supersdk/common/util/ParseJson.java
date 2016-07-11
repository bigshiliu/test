package com.hutong.supersdk.common.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

public class ParseJson {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	 
	 static{
		 objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
	 }

	public static String encodeJson(Object o) {
		try{
			return objectMapper.writeValueAsString(o);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从指定的url获取信息并包装成map返回
	 * @param strUrl
	 * @return
	 * @throws Exception
	 */
	public static <T>  T getJsonContentByUrl(String strUrl,Class<T> T) {
		try{
			String result = HttpUtil.get(strUrl);
			return objectMapper.readValue(result, T);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从指定json字符串获取信息
	 * @param strInfo
	 * @param T
	 * @return
	 * @throws Exception
	 */
	public static <T> T  getJsonContentByStr(String strInfo,Class<T> T) {
		try{
			return objectMapper.readValue(strInfo, T);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
