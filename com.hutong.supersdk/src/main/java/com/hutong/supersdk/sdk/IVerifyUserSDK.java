package com.hutong.supersdk.sdk;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;

/**
 * 效验用户接口
 * @author QINZH
 *
 */
public interface IVerifyUserSDK extends ISDK {
	
	/**
	 * 验证玩家账号信息
	 * @param input
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config);

}
