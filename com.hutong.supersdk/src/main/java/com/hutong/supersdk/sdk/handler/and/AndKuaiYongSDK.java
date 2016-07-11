package com.hutong.supersdk.sdk.handler.and;

import org.springframework.stereotype.Component;

import com.hutong.supersdk.sdk.handler.iosyy.IOSYYKuaiYongSDK;

@Component("andKuaiYongSDK")
public class AndKuaiYongSDK extends IOSYYKuaiYongSDK {
	
	private static final String SDK_ID = "AndKuaiYong";
	
	@Override	
	public String getSDKId() {
		return SDK_ID;
	}
}
