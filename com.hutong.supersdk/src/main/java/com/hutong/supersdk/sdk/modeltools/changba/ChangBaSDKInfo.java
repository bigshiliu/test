package com.hutong.supersdk.sdk.modeltools.changba;

public class ChangBaSDKInfo {
	//   
	private String requestUrl;
	// appId
	private String id;
	// 应用密钥
	private String secret;
	// RSA私钥
	private String priKey;
	// RSA公钥
	private String pubKey;

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getPriKey() {
		return priKey;
	}

	public void setPriKey(String priKey) {
		this.priKey = priKey;
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}
}
