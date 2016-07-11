package com.hutong.supersdk.sdk.modeltools;

import java.util.HashMap;
import java.util.Map;

public class PostOrderRet {
	
	public static final String STATUS_OK = "ok";
	public static final String STATUS_FAIL = "fail";
	
	private String status;
	private Map<String, String> extra = new HashMap<String, String>();
	
	public PostOrderRet ok() {
		this.status = STATUS_OK;
		return this;
	}
	
	public PostOrderRet fail() {
		this.status = STATUS_FAIL;
		return this;
	}
	
	public boolean isOk() {
		return this.status.equals(STATUS_OK);
	}
	
	public PostOrderRet setExtra(String key, String value) {
		this.extra.put(key, value);
		return this;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, String> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}

}
