package com.hutong.supersdk.sdk.handler.and;

import org.springframework.stereotype.Component;

import com.hutong.supersdk.sdk.handler.iosyy.IOSYYHaiMaSDK;

@Component("andHaiMaSDK")
public class AndHaiMaSDK extends IOSYYHaiMaSDK {
	
	private static final String SDK_ID = "AndHaiMa";
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}
}
