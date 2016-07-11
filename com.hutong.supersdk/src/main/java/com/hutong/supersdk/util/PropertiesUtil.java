package com.hutong.supersdk.util;


import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@SuppressWarnings("unused")
public class PropertiesUtil {
	
	private static final Log logger = LogFactory.getLog(PropertiesUtil.class);
	
	private PropertiesUtil(){}
	
	/**
	 * 查询value根据key
	 * @param fileName 名称
	 * @param keyName 查询key
	 * @return 有 返回对应value 无 返回空字符串
	 */
	public static String findValueByKey(String fileName, String keyName){
		ResourceBundle rb = ResourceBundle.getBundle(fileName);
		if(null != rb){
			return rb.getString(keyName);
		}
		return "";
	}
	
	public static void main(String[] args) {
	}
}
