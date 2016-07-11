package com.hutong.supersdk.sdk.modeltools.baidu;

import java.io.Serializable;

public class BaiduResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer AppID;
	
	private Integer ResultCode;
	
	private String ResultMsg;
	
	private String Sign;
	
	private String Content;
	
	public BaiduResult() {
		this.Content = "";
	}
	
	public BaiduResult(int appId) {
		this.AppID = appId;
		this.Content = "";
	}

	public Integer getAppID() {
		return AppID;
	}

	public void setAppID(Integer appID) {
		AppID = appID;
	}

	public Integer getResultCode() {
		return ResultCode;
	}

	public void setResultCode(Integer resultCode) {
		ResultCode = resultCode;
	}

	public String getResultMsg() {
		return ResultMsg;
	}

	public void setResultMsg(String resultMsg) {
		ResultMsg = resultMsg;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getContent() {
		return Content;
	}

	public void setContent(String content) {
		Content = content;
	}

}
