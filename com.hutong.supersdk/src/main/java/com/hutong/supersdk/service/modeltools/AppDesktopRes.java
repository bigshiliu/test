package com.hutong.supersdk.service.modeltools;

import java.util.Map;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.util.ParseJson;

public class AppDesktopRes extends DesktopRes {
	
	public void setAppId(String appId) {
		this.getRes().setKeyData(DataKeys.Desktop.APP_ID, appId);
	}
	
	public void setAppName(String appName) {
		this.getRes().setKeyData(DataKeys.Desktop.APP_NAME, appName);
	}
	
	public void setAppSecret(String appSecret) {
		this.getRes().setKeyData(DataKeys.Desktop.APP_SECRET, appSecret);
	}
	
	public void setPrivateKey(String privateKey) {
		this.getRes().setKeyData(DataKeys.Desktop.PRIVATE_KEY, privateKey);
	}
	
	public void setEncryptKey(String encryptKey) {
		this.getRes().setKeyData(DataKeys.Desktop.ENCRYPT_KEY, encryptKey);
	}
	
	public void setPaymentSecret(String paymentSecret) {
		this.getRes().setKeyData(DataKeys.Desktop.PAYMENT_SECRET, paymentSecret);
	}
	
	public void setShortId(String short_id) {
		this.getRes().setKeyData(DataKeys.Desktop.SHORT_ID, short_id);
	}
	
	public void setNoticeUrl(String notice_url) {
		this.getRes().setKeyData(DataKeys.Desktop.NOTICE_URL, notice_url);
	}
	
	public void setSdkId(String sdkId) {
		this.getRes().setKeyData(DataKeys.Desktop.SDK_ID, sdkId);
	}
	
	public void setChannelId(String channelId) {
		this.getRes().setKeyData(DataKeys.Desktop.CHANNEL_ID, channelId);
	}
	
	public void setConfigInfo(String channelId, Map<String, String> configInfoMap) {
		this.getRes().setKeyData(channelId, ParseJson.encodeJson(configInfoMap));
	}

	public void setClientConfig(String clientConfig) {
		this.getRes().setKeyData(DataKeys.Desktop.CLIENT_CONFIG, clientConfig);
	}
	
	public void setServerConfig(String serverConfig) {
		this.getRes().setKeyData(DataKeys.Desktop.SERVER_CONFIG, serverConfig);
	}
}
