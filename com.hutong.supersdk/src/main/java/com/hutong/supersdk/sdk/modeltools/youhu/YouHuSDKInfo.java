package com.hutong.supersdk.sdk.modeltools.youhu;

public class YouHuSDKInfo {
	
	private String requestUrl;
	private String appId;
	private String merchantId;
	private String merchantAppId;
	private String keys;

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String keys) {
		this.keys = keys;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantAppId() {
		return merchantAppId;
	}

	public void setMerchantAppId(String merchantAppId) {
		this.merchantAppId = merchantAppId;
	}
	
}
