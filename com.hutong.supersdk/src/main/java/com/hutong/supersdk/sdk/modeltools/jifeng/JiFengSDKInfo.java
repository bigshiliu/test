package com.hutong.supersdk.sdk.modeltools.jifeng;

public class JiFengSDKInfo {
	
	private String requestUrl;
	private String jiFengID;
	/**
	 * http://api.gfan.com/sdk/pay/queryAppPayLog
	 */
	private String checkOrderUrl;
	
	private String secret_key;
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getJiFengID() {
		return jiFengID;
	}
	public void setJiFengID(String jiFengID) {
		this.jiFengID = jiFengID;
	}
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}
	public String getSecret_key() {
		return secret_key;
	}
	public void setSecret_key(String secret_key) {
		this.secret_key = secret_key;
	}
	
}
