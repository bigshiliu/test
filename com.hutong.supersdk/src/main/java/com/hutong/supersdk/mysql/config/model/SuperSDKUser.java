package com.hutong.supersdk.mysql.config.model;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_SUPERSDK_USER" )
public class SuperSDKUser extends ABaseDomain{

	private static final long serialVersionUID = 1L;

	public static final Integer ENABLE_FLAG_VALID = 0;
	public static final Integer ENABEL_FLAG_INVALID = 1;
	
	@Id
	@Column(name = "SuperSDK_Uid")
	private String supersdkUid;
	
	@Column(name = "SDK_Id")
	private String sdkId;
	
	@Column(name = "SDK_Uid")
	private String sdkUid;
	
	@Column(name = "Enable_Flag")
	private int enableFlag;
	
	@Column(name = "Create_Time")
	private Timestamp createTime;
	
	@Column(name = "Extra")
	private String extra;
	
	public SuperSDKUser() {}

	public SuperSDKUser(String sdkId, String sdkUid) {
		this.supersdkUid = UUID.randomUUID().toString().replace("-", "");
		
		this.sdkId = sdkId;
		this.sdkUid = sdkUid;
		
		this.enableFlag = ENABLE_FLAG_VALID;
		this.createTime = new Timestamp(System.currentTimeMillis());
		this.extra = "";
	}

	public String getSupersdkUid() {
		return supersdkUid;
	}

	public void setSupersdkUid(String supersdkUid) {
		this.supersdkUid = supersdkUid;
	}

	public String getSdkId() {
		return sdkId;
	}

	public void setSdkId(String sdkId) {
		this.sdkId = sdkId;
	}

	public String getSdkUid() {
		return sdkUid;
	}

	public void setSdkUid(String sdkUid) {
		this.sdkUid = sdkUid;
	}

	public int getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(int enableFlag) {
		this.enableFlag = enableFlag;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}
}
