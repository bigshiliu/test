package com.hutong.supersdk.mysql.inst.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_SDK_CONFIG" )
public class SdkConfig extends ABaseDomain {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 联合主键
	 */
	@EmbeddedId
	private PoId poId;
	
	@Column(name = "SDK_Name")
	private String sdkName;
	
	@Column(name = "Config_Info")
	private String configInfo;
	
	@Column(name = "Push_Notice_Info")
	private String pushNoticeInfo;

	@Column(name = "Handle_Bean")
	private String handleBean;
	
	@Column(name = "Notice_Url")
	private String noticeUrl;
	
	@Column(name = "Version")
	private String version;
	
	@Column(name = "Enable_Flag")
	private int enableFlag;
	
	@Column(name = "Create_Time")
	private Timestamp createTime;
	
	@Column(name = "Extra")
	private String extra;
	
	public SdkConfig() {
		this.createTime = new Timestamp(System.currentTimeMillis());
		this.enableFlag = 1;
	}

    public PoId getPoId() {
        return poId;
    }

    public void setPoId(PoId poId) {
        this.poId = poId;
    }

    public String getSdkName() {
		return sdkName;
	}
	public void setSdkName(String sdkName) {
		this.sdkName = sdkName;
	}
	public String getConfigInfo() {
		if(StringUtils.isEmpty(configInfo))
			return "";
		return configInfo;
	}
	public void setConfigInfo(String configInfo) {
		this.configInfo = configInfo;
	}

	public String getHandleBean() {
		return handleBean;
	}
	public void setHandleBean(String handleBean) {
		this.handleBean = handleBean;
	}
	public String getNoticeUrl() {
		return noticeUrl;
	}
	public void setNoticeUrl(String noticeUrl) {
		this.noticeUrl = noticeUrl;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getPushNoticeInfo() {
		return pushNoticeInfo;
	}
	public void setPushNoticeInfo(String pushNoticeInfo) {
		this.pushNoticeInfo = pushNoticeInfo;
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

	public static class PoId extends ABaseDomain{

		private static final long serialVersionUID = 1L;

		@Column(name = "App_Channel_Id")
		private String appChannelId;

		@Column(name = "App_Id")
		private String appId;

		public PoId() {
		}

		public PoId(String appId, String appChannelId) {
			this.appChannelId = appChannelId;
			this.appId = appId;
		}

		public String getAppChannelId() {
			return appChannelId;
		}

		public void setAppChannelId(String appChannelId) {
			this.appChannelId = appChannelId;
		}

		public String getAppId() {
			return appId;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}
	}

}
