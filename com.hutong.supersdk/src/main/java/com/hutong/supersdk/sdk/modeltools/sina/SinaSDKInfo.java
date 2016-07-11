package com.hutong.supersdk.sdk.modeltools.sina;

public class SinaSDKInfo {
	
	private String signaturekey;
	private String requestUrl;
	private String appKey;
	private String appSecret;
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}
	public String getSignaturekey() {
		return signaturekey;
	}
	public void setSignaturekey(String signaturekey) {
		this.signaturekey = signaturekey;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
}
