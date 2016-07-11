package com.hutong.supersdk.sdk.modeltools.pp;

import java.util.HashMap;
import java.util.Map;

public class PPRes {
	
	private Integer id;
	private String service;
	private Map<String, String> data = new HashMap<String, String>();
	private Map<String, Integer> game = new HashMap<String, Integer>();
	private String encrypt;
	private String sign;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public Map<String, Integer> getGame() {
		return game;
	}
	public void setGame(Map<String, Integer> game) {
		this.game = game;
	}
	public String getEncrypt() {
		return encrypt;
	}
	public void setEncrypt(String encrypt) {
		this.encrypt = encrypt;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
}
