package com.hutong.supersdk.service.modeltools;


import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * Platform返回对象
 * @author QINZH
 *
 */
public class PlatformRes {

	private JsonResObj res;

	public PlatformRes() {
		this.res = new JsonResObj();
	}

	public boolean isOk(){
		return this.res.isStatusOk();
	}
	
	public PlatformRes ok() {
		this.res.ok();
		return this;
	}
	
	public PlatformRes fail() {
		this.res.fail();
		return this;
	}
	
	public PlatformRes setError(ErrorEnum error, String errorMsg) {
		this.fail();
		this.res.setError(error, errorMsg);
		return this;
	}
	
	public JsonResObj getRes() {
		return res;
	}

}
