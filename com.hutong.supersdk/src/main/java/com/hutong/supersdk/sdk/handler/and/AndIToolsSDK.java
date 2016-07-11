package com.hutong.supersdk.sdk.handler.and;

import com.hutong.supersdk.sdk.handler.iosyy.IOSYYIToolsSDK;
import org.springframework.stereotype.Component;

@Component("andIToolsSDK")
public class AndIToolsSDK extends IOSYYIToolsSDK{
	
	private static final String SDK_ID = "AndITools";
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

}
