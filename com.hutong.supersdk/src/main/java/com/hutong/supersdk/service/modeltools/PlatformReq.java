package com.hutong.supersdk.service.modeltools;


import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * Platform系统请求对象
 * 
 * @author QINZH
 *
 */
public class PlatformReq {

	private JsonReqObj req;

	public JsonReqObj getReq() {
		return req;
	}

	public void setReq(JsonReqObj req) {
		this.req = req;
	}

	public PlatformReq(JsonReqObj req) {
		this.req = req;
	}
	
	public void setToken(String token) {
		this.getReq().setKeyData(DataKeys.Platform.PLATFORM_USER_TOKEN, token);
	}
	
	public void setSign(String sign) {
		this.getReq().setKeyData(DataKeys.Platform.SIGN, sign);
	}
	
	public void setAppId(String appId) {
		this.getReq().setKeyData(DataKeys.Platform.APP_ID, appId);
	}

	/**
	 * 参数检查
	 * @param 参数检查数组 checkList
	 * @return 参数数组中的参数全部存在并且不为空返回true,否则返回false
	 */
	public boolean checkParmas(String[] checkList) {
		boolean checkResult = false;
		for (String param : checkList) {
			if (!this.req.containsDataKey(param) || StringUtils.isEmpty(this.req.getDataKey(param))) {
				checkResult = false;
				return checkResult;
			}
			checkResult = true;
		}
		return checkResult;
	}
}
