package com.hutong.supersdk.sdk.modeltools.pengyouwan;

public class PengYouWanSDKInfo {
	
	/**
	 * http://pywsdk.pengyouwan.com/Cpapi/check
	 */
	private String requestUrl;
	
	private String apiSecret;

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getApiSecret() {
		return apiSecret;
	}

	public void setApiSecret(String apiSecret) {
		this.apiSecret = apiSecret;
	}
}
