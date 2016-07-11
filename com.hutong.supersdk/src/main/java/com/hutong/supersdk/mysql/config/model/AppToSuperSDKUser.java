package com.hutong.supersdk.mysql.config.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_APP_TO_SUPERSDK_USER" )
public class AppToSuperSDKUser extends ABaseDomain{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AppToUid appToSUid;
	
	public AppToUid getAppToSUid() {
		return appToSUid;
	}

	public void setAppToSUid(AppToUid appToSUid) {
		this.appToSUid = appToSUid;
	}

	public static class AppToUid extends ABaseDomain {
		private static final long serialVersionUID = 1L;

		@Column(name = "SuperSDK_Uid")
		private String superSDKUid;
		
		@Column(name = "App_Id")
		private String appId;
		
		public AppToUid() {}
		
		public AppToUid(String sUid, String appId) {
			this.superSDKUid = sUid;
			this.appId = appId;
		}

		public String getSuperSDKUid() {
			return superSDKUid;
		}

		public void setSuperSDKUid(String superSDKUid) {
			this.superSDKUid = superSDKUid;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}
	}
}
