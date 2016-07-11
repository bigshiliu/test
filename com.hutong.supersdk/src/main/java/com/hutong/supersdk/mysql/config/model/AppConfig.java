package com.hutong.supersdk.mysql.config.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_APP_CONFIG" )
public class AppConfig extends ABaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "App_Id")
	private String appId;
	
	@Column(name = "App_Name")
	private String appName;
	
	@Column(name = "App_Secret")
	private String appSecret;
	
	@Column(name = "Private_Key")
	private String privateKey;
	
	@Column(name = "Encrypt_Key")
	private String encryptKey;
	
	@Column(name = "Payment_Secret")
	private String paymentSecret;
	
	@Column(name = "Short_App_Id")
	private String shortAppId;
	
	@Column(name = "Notice_Url")
	private String noticeUrl;
	
	@Column(name = "DB_Id")
	private String dbId;
	
	@Column(name = "Enable_Flag")
	private int enableFlag;
	
	@Column(name = "Create_Time")
	private Timestamp createTime;
	
	@Column(name = "Extra")
	private String extra;

	public AppConfig() {
		//设置初始化数据源
		this.dbId = DataKeys.Desktop.DEFAULT_DB_ID;
		this.createTime = new Timestamp(System.currentTimeMillis());
		this.enableFlag = 1;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public String getEncryptKey() {
		return encryptKey;
	}

	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}

	public String getPaymentSecret() {
		return paymentSecret;
	}

	public void setPaymentSecret(String paymentSecret) {
		this.paymentSecret = paymentSecret;
	}

	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
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

	public String getShortAppId() {
		return shortAppId;
	}

	public void setShortAppId(String shortAppId) {
		this.shortAppId = shortAppId;
	}

	public String getNoticeUrl() {
		return noticeUrl;
	}

	public void setNoticeUrl(String noticeUrl) {
		this.noticeUrl = noticeUrl;
	}
}
