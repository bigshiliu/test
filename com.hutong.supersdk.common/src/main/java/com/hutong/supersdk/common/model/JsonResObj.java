package com.hutong.supersdk.common.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.util.ParseJson;

/**
 * 返回对象
 * @author QINZH
 * status 状态标示 ok or fail
 * data 业务参数
 * common 公共参数
 * extra额外参数
 */
public class JsonResObj {
	
	public static final String STATUS_OK = "ok";
	public static final String STATUS_FAIL = "fail";

	private String status;
	private Map<String, String> data;
	private Map<String, String> common;
	private Map<String, String> extra;
	
	public JsonResObj() {
		this.data = new HashMap<String, String>();
		this.common = new HashMap<String, String>();
		this.extra = new HashMap<String, String>();
	}
	
	@JsonIgnore
	public boolean isStatusOk() {
		return this.status.equals(STATUS_OK);
	}
	
	@JsonIgnore
	public boolean isOk() {
		return this.status.equals(STATUS_OK);
	}
	
	public JsonResObj fail() {
		this.status = STATUS_FAIL;
		return this;
	}
	
	public JsonResObj ok() {
		this.status = STATUS_OK;
		return this;
	}
	
	public void setKeyData(String key, String value) {
		this.data.put(key, value);
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public Map<String, String> getCommon() {
		return common;
	}
	public void setCommon(Map<String, String> common) {
		this.common = common;
	}
	public Map<String, String> getExtra() {
		return extra;
	}
	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
	
	public void setException(SuperSDKException e) {
		this.data.put(DataKeys.Error.ERROR, e.getMessage());
		this.data.put(DataKeys.Error.ERROR_NO, String.valueOf(e.getErrorCode()));
	}
	
	public void setException(Exception e) {
		this.data.put(DataKeys.Error.ERROR, ErrorEnum.SERVER_ERROR.toString());
		this.data.put(DataKeys.Error.ERROR_NO, String.valueOf(ErrorEnum.SERVER_ERROR.errorCode));
		this.data.put(DataKeys.Error.ERROR_MSG, e.getMessage());
	}

	public JsonResObj setError(ErrorEnum error) {
		this.setKeyData(DataKeys.Error.ERROR, error.toString());
		this.setKeyData(DataKeys.Error.ERROR_NO, String.valueOf(error.errorCode));

		return this;
	}

	public JsonResObj setError(ErrorEnum error, String msg) {
		this.setKeyData(DataKeys.Error.ERROR, error.toString());
		this.setKeyData(DataKeys.Error.ERROR_NO, String.valueOf(error.errorCode));
		this.setKeyData(DataKeys.Error.ERROR_MSG, msg);

		return this;
	}

	public JsonResObj ready() {
		this.common.put(DataKeys.Common.TIME, String.valueOf(System.currentTimeMillis()));
		return this;
	}
	
	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}

}
