package com.hutong.supersdk.sdk.modeltools.aibeipay;

public class AiBeiPaySDKInfo {
	
	/**
	 * http://ipay.iapppay.com:9999/payapi/order
	 */
	private String checkOrderUrl;
	
	private String appId;
	
	private String rsaPrivateKey;
	
	private String rsaPublicKey;

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getRsaPrivateKey() {
		return rsaPrivateKey;
	}

	public void setRsaPrivateKey(String rsaPrivateKey) {
		this.rsaPrivateKey = rsaPrivateKey;
	}

	public String getRsaPublicKey() {
		return rsaPublicKey;
	}

	public void setRsaPublicKey(String rsaPublicKey) {
		this.rsaPublicKey = rsaPublicKey;
	}

	public String getCheckOrderUrl() {
		return checkOrderUrl;
	}

	public void setCheckOrderUrl(String checkOrderUrl) {
		this.checkOrderUrl = checkOrderUrl;
	}
	
}
