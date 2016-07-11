package com.hutong.supersdk.sdk.modeltools.yingyonghu;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class YYHData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("user_id")
	private Long user_id;
	
	@JsonProperty("nick_name")
	private String nick_name;
	
	@JsonProperty("user_name")
	private String user_name;
	
	@JsonProperty("phone")
	private String phone;
	
	@JsonProperty("avatar_url")
	private String avatar_url;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("ticket")
	private String ticket;
	
	@JsonProperty("actived")
	private Boolean actived;

	public Long getUser_id() {
		return user_id;
	}

	public void setUser_id(Long user_id) {
		this.user_id = user_id;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAvatar_url() {
		return avatar_url;
	}

	public void setAvatar_url(String avatar_url) {
		this.avatar_url = avatar_url;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	public Boolean getActived() {
		return actived;
	}

	public void setActived(Boolean actived) {
		this.actived = actived;
	}
}
