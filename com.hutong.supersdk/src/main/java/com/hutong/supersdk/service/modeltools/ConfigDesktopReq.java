package com.hutong.supersdk.service.modeltools;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * ConfigDesktop请求对象
 * @author QINZH
 *
 */
public class ConfigDesktopReq extends DesktopReq {
	
	public ConfigDesktopReq(JsonReqObj req) {
		super(req);
	}
	
	@JsonIgnore
	public String getAppId() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.APP_ID)) {
			return this.getReq().getDataKey(DataKeys.Desktop.APP_ID);
		}
		return "";
	}
	
	@JsonIgnore
	public String getSdkId() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SDK_ID)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SDK_ID);
		}
		return "";
	}
	
	@JsonIgnore
	public String getSdkName() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SDK_NAME)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SDK_NAME);
		}
		return "";
	}
	
	@JsonIgnore
	public String getClientConfig() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.CLIENT_CONFIG)) {
			return this.getReq().getDataKey(DataKeys.Desktop.CLIENT_CONFIG);
		}
		return "";
	}
	
	@JsonIgnore
	public String getServerConfig() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SERVER_CONFIG)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SERVER_CONFIG);
		}
		return "";
	}
	
	@JsonIgnore
	public String getChannelId() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.CHANNEL_ID)) {
			return this.getReq().getDataKey(DataKeys.Desktop.CHANNEL_ID);
		}
		return "";
	}
	
	@JsonIgnore
	public String getChannelIds() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.CHANNEL_IDS)) {
			return this.getReq().getDataKey(DataKeys.Desktop.CHANNEL_IDS);
		}
		return "";
	}
}
