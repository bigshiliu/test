package com.hutong.supersdk.service.modeltools;


import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * Desktop返回对象
 * @author QINZH
 *
 */
public class DesktopRes {

	private JsonResObj res;

	public DesktopRes() {
		this.res = new JsonResObj();
	}

	public DesktopRes ok() {
		this.res.ok();
		return this;
	}
	
	public DesktopRes fail() {
		this.res.fail();
		return this;
	}
	
	public void setGameOwnerId(String id) {
		this.res.setKeyData(DataKeys.Platform.PLATFORM_USER_ID, id);
	}
	
	public void setUserName(String username) {
		this.res.setKeyData(DataKeys.Desktop.USER_NAME, username);
	}
	
	public void setPassword(String password) {
		this.res.setKeyData(DataKeys.Desktop.PASSWORD, password);
	}
	
	public void setEmail(String email) {
		this.res.setKeyData(DataKeys.Desktop.EMAIL, email);
	}
	
	public void setAppIdArray(String appIdArray) {
		this.res.setKeyData(DataKeys.Desktop.APP_ID_ARRAY, appIdArray);
	}
	
	public void setToken(String token) {
		this.res.setKeyData(DataKeys.Desktop.TOKEN, token);
	}
	
	public JsonResObj getRes() {
		return res;
	}

}
