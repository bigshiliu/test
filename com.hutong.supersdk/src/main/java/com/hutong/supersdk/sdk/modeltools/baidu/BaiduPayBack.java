package com.hutong.supersdk.sdk.modeltools.baidu;

import com.hutong.supersdk.common.util.ParseJson;

public class BaiduPayBack {
	
	private String ErrorCode;
	private String ErrorDesc;
	public String getErrorCode() {
		return ErrorCode;
	}
	public void setErrorCode(String errorCode) {
		ErrorCode = errorCode;
	}
	public String getErrorDesc() {
		return ErrorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		ErrorDesc = errorDesc;
	}

	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}
}
