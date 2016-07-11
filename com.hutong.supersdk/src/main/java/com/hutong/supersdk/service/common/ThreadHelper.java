package com.hutong.supersdk.service.common;


public class ThreadHelper {
	/**
	 * serverKey的线程局部变量(保存当前线程持有的serverKey)
	 */
	private static  ThreadLocal<String> appId = new ThreadLocal<String>();

	public static void setAppId(String id) {
		appId.set(id);
	}
	
	public static String getAppId() {
		return appId.get();
	}
}
