package com.hutong.supersdk.sdk.modeltools.sinatools;

import java.util.Map;

public class SinaToolsLoginRet {
	
	//1成功 0失败
	private int code;
	private String msg;
	private String sign;
	private int r;
	private Map<String, String> data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public int getR() {
		return r;
	}
	public void setR(int r) {
		this.r = r;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	
}
