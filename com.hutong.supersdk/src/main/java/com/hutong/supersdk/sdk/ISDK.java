package com.hutong.supersdk.sdk;

/**
 * SDK基类
 * @author QINZH
 *
 */
public interface ISDK {
	public String getSDKId();
	
	public Class<?> getConfigClazz();
	
}
