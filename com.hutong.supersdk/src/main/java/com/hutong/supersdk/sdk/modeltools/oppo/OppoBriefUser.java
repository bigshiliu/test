package com.hutong.supersdk.sdk.modeltools.oppo;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class OppoBriefUser implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("constellation")
	private Integer constellation;
	
	@JsonProperty("sex")
	private Boolean sex;
	
	@JsonProperty("profilePictureUrl")
	private String profilePictureUrl;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("emailStatus")
	private Boolean emailStatus;
	
	@JsonProperty("mobileStatus")
	private Boolean mobileStatus;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("mobile")
	private String mobile;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("gameBalance")
	private String gameBalance;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getConstellation() {
		return constellation;
	}

	public void setConstellation(Integer constellation) {
		this.constellation = constellation;
	}

	public Boolean getSex() {
		return sex;
	}

	public void setSex(Boolean sex) {
		this.sex = sex;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Boolean getEmailStatus() {
		return emailStatus;
	}

	public void setEmailStatus(Boolean emailStatus) {
		this.emailStatus = emailStatus;
	}

	public Boolean getMobileStatus() {
		return mobileStatus;
	}

	public void setMobileStatus(Boolean mobileStatus) {
		this.mobileStatus = mobileStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGameBalance() {
		return gameBalance;
	}

	public void setGameBalance(String gameBalance) {
		this.gameBalance = gameBalance;
	}

}
