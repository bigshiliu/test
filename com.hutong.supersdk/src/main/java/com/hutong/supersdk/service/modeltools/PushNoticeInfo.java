package com.hutong.supersdk.service.modeltools;

/**
 * 推送配置信息,SdkConfig对应的pushInfo字段解析对象
 * @author QINZH
 *
 */
public class PushNoticeInfo {
	
	/**
	 * appKey
	 */
	private String appKey;
	/**
	 * masterSecret
	 */
	private String masterSecret;
	/**
	 * 最大重试次数
	 */
	private int maxRetryTimes = 0;
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getMasterSecret() {
		return masterSecret;
	}
	public void setMasterSecret(String masterSecret) {
		this.masterSecret = masterSecret;
	}
	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}
	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}
}
