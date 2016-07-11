package com.hutong.supersdk.sdk.modeltools.chacha;

public class ChaChaSDKInfo {
	
	private String appid;
	private String serverKey;
	private String requestUrl;
	/**
	 * http://open.guopan.cn/api2/gp_sdk_order_status.php
	 */
	private String checkOrderUrl;
	public String getServerKey() {
		return serverKey;
	}
	public void setServerKey(String serverKey) {
		this.serverKey = serverKey;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getRequestUrl() {
		return requestUrl;
	}
	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}
	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}
	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}

}
