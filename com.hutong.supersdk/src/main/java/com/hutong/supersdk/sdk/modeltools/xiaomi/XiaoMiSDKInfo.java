package com.hutong.supersdk.sdk.modeltools.xiaomi;

public class XiaoMiSDKInfo {
	
	private String appId;
	private String appSecretKey;
	private String requestUrl;
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppSecretKey() {
		return appSecretKey;
	}
	public void setAppSecretKey(String appSecretKey) {
		this.appSecretKey = appSecretKey;
	}
}
