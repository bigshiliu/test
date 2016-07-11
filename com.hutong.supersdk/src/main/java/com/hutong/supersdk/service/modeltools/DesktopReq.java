package com.hutong.supersdk.service.modeltools;


import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * Desktop请求对象
 * 
 * @author QINZH
 *
 */
public class DesktopReq {

	private JsonReqObj req;

	public JsonReqObj getReq() {
		return req;
	}

	public void setReq(JsonReqObj req) {
		this.req = req;
	}

	public DesktopReq(JsonReqObj req) {
		this.req = req;
	}
	
	@JsonIgnore
	public String getUserId() {
		if (this.getReq().containsDataKey(DataKeys.Platform.PLATFORM_USER_ID)) {
			return this.getReq().getDataKey(DataKeys.Platform.PLATFORM_USER_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getToken() {
		if (this.getReq().containsDataKey(DataKeys.Platform.PLATFORM_USER_TOKEN)) {
			return this.getReq().getDataKey(DataKeys.Platform.PLATFORM_USER_TOKEN);
		}
		return null;
	}

	@JsonIgnore
	public String getUserName() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.USER_NAME)) {
			return this.getReq().getDataKey(DataKeys.Desktop.USER_NAME);
		}
		return null;
	}

	@JsonIgnore
	public String getPassword() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.PASSWORD)) {
			return this.getReq().getDataKey(DataKeys.Desktop.PASSWORD);
		}
		return null;
	}
	
	@JsonIgnore
	public String getNewPassword() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.NEW_PASSWORD)) {
			return this.getReq().getDataKey(DataKeys.Desktop.NEW_PASSWORD);
		}
		return null;
	}

	@JsonIgnore
	public String getEmail() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.EMAIL)) {
			return this.getReq().getDataKey(DataKeys.Desktop.EMAIL);
		}
		return null;
	}

	@JsonIgnore
	public String getSign() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SIGN)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SIGN);
		}
		return null;
	}
	
	@JsonIgnore
	public String getAppIdArray() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.APP_ID_ARRAY)) {
			return this.getReq().getDataKey(DataKeys.Desktop.APP_ID_ARRAY);
		}
		return null;
	}
}
