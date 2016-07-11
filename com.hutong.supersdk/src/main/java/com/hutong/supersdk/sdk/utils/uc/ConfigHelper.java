package com.hutong.supersdk.sdk.utils.uc;

/**
 * 
 * 配置文件辅助类。
 *
 */
public class ConfigHelper {

	/**
	 * 根据接口名称获取访问路径
	 * @param 接口名称
	 * @return 访问路径
	 */
	public static String getServiceUrl(String service){
		return "ss";
	}
	
	/**
	 * 
	 */
	public static Long getCheckTime(){
		return 120L;
	}
	/**
	 * 
	 */
	public static Integer getConnectTimeOut(){
		return 5000;
	}
	/**
	 * 
	 */
	public static Integer getSocketTimeOut(){
		return 5000 ;
	}
	
	/**
	 * 
	 */
	public static Integer getIntervalTime(){
		return 24;
	}

	public static String getServerHost() {
		return "http://sdk.test4.g.uc.cn/";
	}
	
}
