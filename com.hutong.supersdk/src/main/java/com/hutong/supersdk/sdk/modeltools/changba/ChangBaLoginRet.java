package com.hutong.supersdk.sdk.modeltools.changba;

import java.util.Map;

public class ChangBaLoginRet {
	
	private Map<String, String> data;
	private int errno;
	private String errmsg;
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public int getErrno() {
		return errno;
	}
	public void setErrno(int errno) {
		this.errno = errno;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}
