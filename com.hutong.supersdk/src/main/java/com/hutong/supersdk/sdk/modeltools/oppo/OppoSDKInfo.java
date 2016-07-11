package com.hutong.supersdk.sdk.modeltools.oppo;

public class OppoSDKInfo {
	
	private String pubKey;
	
	/**
	 * opposdk 2.0.0版本参数
	 * 登陆验证地址 http://i.open.game.oppomobile.com/gameopen/user/fileIdInfo
	 */
	private String requestUrl;
	
	private String appKey;
	
	private String appSecret;

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

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
}
