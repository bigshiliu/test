package com.hutong.supersdk.mysql.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_COMMON_SDK_CONFIG")
public class CommonSDKConfig extends ABaseDomain {

	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "App_Channel_Id")
	private String appChannelId;
	
	@Column(name = "App_Channel_Name")
	private String appChannelName;

	@Column(name = "Common_Config_Info")
	private String commonConfigInfo;

	public String getAppChannelId() {
		return appChannelId;
	}

	public void setAppChannelId(String appChannelId) {
		this.appChannelId = appChannelId;
	}

	public String getAppChannelName() {
		return appChannelName;
	}

	public void setAppChannelName(String appChannelName) {
		this.appChannelName = appChannelName;
	}

	public String getCommonConfigInfo() {
		return commonConfigInfo;
	}

	public void setCommonConfigInfo(String commonConfigInfo) {
		this.commonConfigInfo = commonConfigInfo;
	}
	
	
}
