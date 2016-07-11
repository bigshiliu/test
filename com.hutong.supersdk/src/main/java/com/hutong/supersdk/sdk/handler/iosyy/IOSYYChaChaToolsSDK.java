package com.hutong.supersdk.sdk.handler.iosyy;

import org.springframework.stereotype.Component;

import com.hutong.supersdk.sdk.handler.and.AndChaChaSDK;

@Component("iOSChaChaToolsSDK")
public class IOSYYChaChaToolsSDK extends AndChaChaSDK {
	
	private static final String SDK_ID = "IOSYYChaChaTools";
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}
}
