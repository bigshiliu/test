package com.hutong.supersdk.sdk.modeltools.momo;

public class MoMoSDKInfo {
	
	private String appId;
	private String appKey;
	private String requestUrl;
	private String paymentCheckUrl;
	public String getPaymentCheckUrl() {
		return paymentCheckUrl;
	}
	public void setPaymentCheckUrl(String paymentCheckUrl) {
		this.paymentCheckUrl = paymentCheckUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
}
