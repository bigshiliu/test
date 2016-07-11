package com.hutong.supersdk.sdk.modeltools.vivo;

public class VivoSDKInfo {
	
	private String requestUrl;
	private String cpId;
	private String appId;
	private String appSecret; 
	private String version;
	private String signMethod;
	private String createOrderUrl;
	/**
	 * https://pay.vivo.com.cn/vcoin/query
	 */
	private String checkOrderUrl;
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getCpId() {
		return cpId;
	}
	public void setCpId(String cpId) {
		this.cpId = cpId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getAppSecret() {
		return appSecret;
	}
	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getSignMethod() {
		return signMethod;
	}
	public void setSignMethod(String signMethod) {
		this.signMethod = signMethod;
	}
	public String getCreateOrderUrl() {
		return createOrderUrl;
	}
	public void setCreateOrderUrl(String createOrderUrl) {
		this.createOrderUrl = createOrderUrl;
	}
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}
	
}
