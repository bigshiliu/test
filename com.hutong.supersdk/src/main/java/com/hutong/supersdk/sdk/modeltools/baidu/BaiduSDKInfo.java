package com.hutong.supersdk.sdk.modeltools.baidu;

public class BaiduSDKInfo {
	private String appId;
	private String appKey;
	private String secretKey;
	/**
	 * 越狱url
	 * http://service.sj.91.com/usercenter/AP.aspx
	 */
	private String requestUrl; 
	/**
	 * 安卓,checkOrder url
	 * http://querysdkapi.91.com/CpOrderQuery.ashx
	 */
	private String checkOrderUrl;
	
	/**
	 * 3.6.0以后版本的requestUrl360
	 * http://querysdkapi.baidu.com/query/cploginstatequery
	 */
	private String requestUrl360;
	
	/**
	 * 3.6.0以后版本的checkOrderUrl
	 * http://querysdkapi.baidu.com/query/cploginstatequery
	 */
	private String checkOrderUrl360;
	
	public String getRequestUrl360() {
		return requestUrl360;
	}
	public void setRequestUrl360(String requestUrl360) {
		this.requestUrl360 = requestUrl360;
	}
	public String getCheckOrderUrl360() {
		return checkOrderUrl360;
	}
	public void setCheckOrderUrl360(String checkOrderUrl360) {
		this.checkOrderUrl360 = checkOrderUrl360;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
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
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}
}
