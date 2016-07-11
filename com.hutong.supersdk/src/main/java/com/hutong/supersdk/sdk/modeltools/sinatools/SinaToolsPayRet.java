package com.hutong.supersdk.sdk.modeltools.sinatools;

import com.hutong.supersdk.common.util.ParseJson;

public class SinaToolsPayRet {
	
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

	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}
		
}
