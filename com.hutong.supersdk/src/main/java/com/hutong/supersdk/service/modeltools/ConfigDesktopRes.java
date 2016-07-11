package com.hutong.supersdk.service.modeltools;

import java.util.Map;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.util.ParseJson;

public class ConfigDesktopRes extends DesktopRes {
	
	public void setAppId(String appId) {
		this.getRes().setKeyData(DataKeys.Desktop.APP_ID, appId);
	}
	
	public void setSdkId(String sdkId) {
		this.getRes().setKeyData(DataKeys.Desktop.SDK_ID, sdkId);
	}
	
	public void setSdkName(String sdkName) {
		this.getRes().setKeyData(DataKeys.Desktop.SDK_NAME, sdkName);
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
	
	public void setConfigCount(String configCount) {
		this.getRes().setKeyData(DataKeys.Desktop.CONFIG_COUNT, configCount);
	}
}
