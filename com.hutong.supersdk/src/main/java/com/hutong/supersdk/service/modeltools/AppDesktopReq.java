package com.hutong.supersdk.service.modeltools;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * ConfigDesktop请求对象
 * @author QINZH
 *
 */
public class AppDesktopReq extends DesktopReq {
	
	public AppDesktopReq(JsonReqObj req) {
		super(req);
	}
	
	@JsonIgnore
	public String getAppId() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.APP_ID)) {
			return this.getReq().getDataKey(DataKeys.Desktop.APP_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getAppName() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.APP_NAME)) {
			return this.getReq().getDataKey(DataKeys.Desktop.APP_NAME);
		}
		return null;
	}
	
	@JsonIgnore
	public String getNoticeUrl() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.NOTICE_URL)) {
			return this.getReq().getDataKey(DataKeys.Desktop.NOTICE_URL);
		}
		return null;
	}
	
	@JsonIgnore
	public String getSdkId() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SDK_ID)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SDK_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getAppSecret() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.APP_SECRET)) {
			return this.getReq().getDataKey(DataKeys.Desktop.APP_SECRET);
		}
		return null;
	}
	
	@JsonIgnore
	public String getPrivateKey() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.PRIVATE_KEY)) {
			return this.getReq().getDataKey(DataKeys.Desktop.PRIVATE_KEY);
		}
		return null;
	}
	
	@JsonIgnore
	public String getEncryptKey() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.ENCRYPT_KEY)) {
			return this.getReq().getDataKey(DataKeys.Desktop.ENCRYPT_KEY);
		}
		return null;
	}
	
	@JsonIgnore
	public String getPaymentSecret() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.PAYMENT_SECRET)) {
			return this.getReq().getDataKey(DataKeys.Desktop.PAYMENT_SECRET);
		}
		return null;
	}
	
	@JsonIgnore
	public String getServerConfig() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.SERVER_CONFIG)) {
			return this.getReq().getDataKey(DataKeys.Desktop.SERVER_CONFIG);
		}
		return null;
	}
	
	@JsonIgnore
	public String getChannelIds() {
		if (this.getReq().containsDataKey(DataKeys.Desktop.CHANNEL_IDS)) {
			return this.getReq().getDataKey(DataKeys.Desktop.CHANNEL_IDS);
		}
		return null;
	}
}
