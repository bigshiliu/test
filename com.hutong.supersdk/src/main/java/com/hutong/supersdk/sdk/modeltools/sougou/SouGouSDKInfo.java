package com.hutong.supersdk.sdk.modeltools.sougou;

public class SouGouSDKInfo {
	
	private String requestUrl;
	private String gid;
	private String secret;
	private String payKey;
	private String debugRequestUrl;
	
	public String getDebugRequestUrl() {
		return debugRequestUrl;
	}
	public void setDebugRequestUrl(String debugRequestUrl) {
		this.debugRequestUrl = debugRequestUrl;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getGid() {
		return gid;
	}
	public void setGid(String gid) {
		this.gid = gid;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
	public String getPayKey() {
		return payKey;
	}
	public void setPayKey(String payKey) {
		this.payKey = payKey;
	}
}
