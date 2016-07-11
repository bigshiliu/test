package com.hutong.supersdk.sdk.modeltools.opensdk;

public class OpenSDKInfo {
	
	private String grant_app;
	private String pub_key;
	private String requestUrl;
	private String checkOrderUrl;
	
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getGrant_app() {
		return grant_app;
	}
	public void setGrant_app(String grant_app) {
		this.grant_app = grant_app;
	}
	public String getPub_key() {
		return pub_key;
	}
	public void setPub_key(String pub_key) {
		this.pub_key = pub_key;
	}
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}
}
