package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

public class LoginCheckReq {
	
	private JsonReqObj req;
	
	public LoginCheckReq(JsonReqObj req) {
		this.req = req;
	}
	
	public String getSdkId() {
		return this.req.getData().get(DataKeys.Common.SDK_ID);
	}
	
	public String getAppId() {
		return this.req.getData().get(DataKeys.Common.APP_ID);
	}
	
	public String getAppChannelId() {
		return this.req.getData().get(DataKeys.Common.CHANNEL_ID);
	}
	
	public String getSdkUid() {
		return this.req.getData().get(DataKeys.LoginCheck.SDK_UID);
	}
	
	public String getSdkAccessToken() {
		return this.req.getData().get(DataKeys.LoginCheck.ACCESS_TOKEN);
	}
	
	public String getSdkRefreshToken() {
		return this.req.getData().get(DataKeys.LoginCheck.REFRESH_TOKEN);
	}
	
	public String getSign() {
		return this.req.getData().get(DataKeys.Common.SIGN);
	}
}
