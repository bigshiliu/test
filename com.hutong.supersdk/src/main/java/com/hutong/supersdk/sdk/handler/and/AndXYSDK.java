package com.hutong.supersdk.sdk.handler.and;

import org.springframework.stereotype.Component;

import com.hutong.supersdk.sdk.handler.iosyy.IOSYYXYSDK;

@Component("andXYSDK")
public class AndXYSDK extends IOSYYXYSDK {
	
	private static final String SDK_ID = "AndXYTools";
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

}
