package com.hutong.supersdk.sdk.modeltools.pptv;

import com.hutong.supersdk.common.util.ParseJson;

public class PPTVPayBackRet {
	
	private String code;
	private String message;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}
}
