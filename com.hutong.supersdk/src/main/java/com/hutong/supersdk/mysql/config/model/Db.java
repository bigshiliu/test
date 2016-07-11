package com.hutong.supersdk.mysql.config.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;
import com.hutong.supersdk.util.PropertiesUtil;

@Entity
@Table( name = "T_APP_DB" )
public class Db extends ABaseDomain{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "DB_Id")
	private String dbId;
	
	@Column(name = "DB_Drive")
	private String dbDrive;
	
	@Column(name = "DB_Url")
	private String dbUrl;
	
	@Column(name = "DB_Username")
	private String dbUsername;
	
	@Column(name = "DB_Password")
	private String dbPassword;
	
	@Column(name = "Enable_Flag")
	private int Enable_Flag;
	
	@Column(name = "Create_Time")
	private Timestamp createTime;
	
	@Column(name = "Extra")
	private String extra;

	public Db() {
		this.Enable_Flag = 1;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}
	
	public Db(String dbId, String dbName) {
		// superSDKConfig.properties名称
		String SUPERSDK_CONFIG_NAME = "superSDKConfig";
		this.dbId = dbId;
		this.dbDrive = PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "db_drive");
		StringBuilder dbUrlStr = new StringBuilder();
		dbUrlStr.append(PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "db_url"));
		dbUrlStr.append(dbName);
		dbUrlStr.append("?useUnicode=true&amp;characterEncoding=UTF-8");
		this.dbUrl = dbUrlStr.toString();
		this.dbUsername = PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "db_username");
		this.dbPassword = PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "db_password");
		this.Enable_Flag = 1;
		this.createTime = new Timestamp(System.currentTimeMillis());
	}
	
	public String getDbId() {
		return dbId;
	}

	public void setDbId(String dbId) {
		this.dbId = dbId;
	}

	public String getDbDrive() {
		return dbDrive;
	}

	public void setDbDrive(String dbDrive) {
		this.dbDrive = dbDrive;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbUsername() {
		return dbUsername;
	}

	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}

	public int getEnable_Flag() {
		return Enable_Flag;
	}

	public void setEnable_Flag(int enable_Flag) {
		Enable_Flag = enable_Flag;
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
