package com.hutong.supersdk.sdk.handler.ios;

import org.springframework.stereotype.Component;

import com.hutong.supersdk.sdk.handler.and.AndOpenSDK;

@Component("iOSOpenSDK")
public class IOSOpenSDK extends AndOpenSDK {
	
	private static final String SDK_ID = "IOSOpenSDK";

	@Override
	public String getSDKId() {
		return SDK_ID;
	}
}	
