package com.hutong.supersdk.sdk.modeltools.meizu;

public class MeiZuSDKInfo {
	private String requestUrl;
	private String appSecret;
	private String appID;
	/**
	 * https://api.game.meizu.com/game/order/query
	 */
	private String checkOrderUrl;

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
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
