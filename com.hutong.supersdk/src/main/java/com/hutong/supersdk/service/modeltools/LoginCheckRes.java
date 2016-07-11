package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;

public class LoginCheckRes {
	
	private JsonResObj res;
	
	public LoginCheckRes() {
		this.res = new JsonResObj();
	}
	
	public void ok() {
		this.res.ok();
	}
	
	public void setSuperSDKUid(String superSdkUid) {
		this.res.setKeyData(DataKeys.Common.SUPERSDK_UID, superSdkUid);
	}
	
	public void setAppId(String appId) {
		this.res.setKeyData(DataKeys.Common.APP_ID, appId);
	}
	
	public void setSdkId(String sdkId) {
		this.res.setKeyData(DataKeys.Common.SDK_ID, sdkId);
	}
	
	public void setAppChannelId(String appChannelId) {
		this.res.setKeyData(DataKeys.Common.CHANNEL_ID, appChannelId);
	}
	
	public void setWithSDKVerifyRet(SDKVerifyRet ret) {
		this.res.setKeyData(DataKeys.LoginCheck.SDK_UID, ret.getSdkUid());
		this.res.setKeyData(DataKeys.LoginCheck.ACCESS_TOKEN, ret.getSdkAccessToken());
		this.res.setKeyData(DataKeys.LoginCheck.REFRESH_TOKEN, ret.getSdkRefreshToken());
		
		this.res.setExtra(ret.getExtra());
	}

	public void setSign(String sign) {
		this.res.setKeyData(DataKeys.Common.SIGN, sign);
	}

	public JsonResObj getRes() {
		return res;
	}
}
