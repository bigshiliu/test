package com.hutong.supersdk.sdk.modeltools.kaopu;

import com.hutong.supersdk.sdk.utils.MD5Util;

public class PayCallbackRet {
	
	private String code;
	private String msg;
	private String sign;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
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
	
	public PayCallbackRet generateSign(String signKey) {
		this.sign = MD5Util.MD5(this.code + "|" + signKey);
		return this;
	}
}
