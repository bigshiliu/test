package com.hutong.supersdk.sdk.modeltools.ruigao;

public class RuiGaoSDKInfo {
	
	/**
	 * http://sdk.yuwan8.com/index.php/user_center
	 */
	private String requestUrl;
	
	/**
	 * 商户密钥
	 */
	private String merchantKey;
	
	private String merchantId;

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public String getMerchantKey() {
		return merchantKey;
	}

	public void setMerchantKey(String merchantKey) {
		this.merchantKey = merchantKey;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}
}
