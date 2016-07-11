package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonResObj;

public class ErrorRes {
	
	private JsonResObj res;
	
	public ErrorRes() {
		res = new JsonResObj();
	}
	
	public void fail() {
		this.res.fail();
	}
	
	public void setError(String error) {
		this.res.setKeyData(DataKeys.Error.ERROR, error);
	}
	
	public void setErrorNo(Integer errorNo) {
		this.res.setKeyData(DataKeys.Error.ERROR_NO, errorNo.toString());
	}
	
	public void setErrorMsg(String errorMsg) {
		this.res.setKeyData(DataKeys.Error.ERROR_MSG, errorMsg);
	}

	public JsonResObj getRes() {
		return res;
	}

	public void setRes(JsonResObj res) {
		this.res = res;
	}
	
	
}
