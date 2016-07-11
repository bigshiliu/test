package com.hutong.supersdk.sdk.modeltools.anzhi;

public class AnZhiSDKInfo {
	private String requestUrl;
	private String appKey;
	private String appSecret;
	/**
	 * 安卓 checkOrder url
	 * http://pay.anzhi.com/web/api/third/1/queryorder
	 */
	private String checkOrderUrl;
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	} 
}
