package com.hutong.supersdk.sdk.modeltools.pptv;

public class PPTVSDKInfo {

	/**
	 * http://api.user.vas.pptv.com/c/v2/cksession.php
	 */
	private String requestUrl;

	/**
	 * 回调密钥
	 */
	private String key;
	
	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
