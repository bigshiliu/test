package com.hutong.supersdk.sdk.modeltools.kupai;

public class KuPaiSDKInfo {
	
	private String grant_type;
	private String appId;
	private String client_id;
	private String client_secret;
	private String redirect_uri;
	private String request_url;
	private String pub_key;
	//加密私钥
	private String pri_key;
	//创建订单地址http://pay.coolyun.com:6988/payapi/order
	private String postOrdetUrl;
	
	public String getPri_key() {
		return pri_key;
	}
	public void setPri_key(String pri_key) {
		this.pri_key = pri_key;
	}
	public String getPostOrdetUrl() {
		return postOrdetUrl;
	}
	public void setPostOrdetUrl(String postOrdetUrl) {
		this.postOrdetUrl = postOrdetUrl;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getPub_key() {
		return pub_key;
	}
	public void setPub_key(String pub_key) {
		this.pub_key = pub_key;
	}
	public String getGrant_type() {
		return grant_type;
	}
	public void setGrant_type(String grant_type) {
		this.grant_type = grant_type;
	}
	public String getClient_id() {
		return client_id;
	}
	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}
	public String getClient_secret() {
		return client_secret;
	}
	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}
	public String getRedirect_uri() {
		return redirect_uri;
	}
	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}
	public String getRequest_url() {
		return request_url;
	}
	public void setRequest_url(String request_url) {
		this.request_url = request_url;
	}
}
