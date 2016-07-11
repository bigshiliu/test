package com.hutong.supersdk.sdk.modeltools.leshitv;

import java.util.Map;

public class LeShiTVLoginRet {
	/**
	 * 请求API接口
	 */
	private String request;
	/**
	 * 返回结果
	 * letv_uid   乐视uid 
	 * nickname   乐视昵称 
	 * file_300*300 头像文件,尺寸：298*298 
	 * file_200*200 头像文件,尺寸：200*200 
	 * file_70*70 头像文件,尺寸：70*70 
	 * ile_50*50 头像文件,尺寸：50*50 
	 */
	private Map<String, String> result;
	/**
	 * 状态,1标示请求成功
	 */
	private String status;
	/**
	 * 失败时返回参数包含errorCode和error
	 */
	private String error_code;
	
	private String error;
	
	
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public Map<String, String> getResult() {
		return result;
	}
	public void setResult(Map<String, String> result) {
		this.result = result;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
}
