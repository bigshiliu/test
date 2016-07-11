package com.hutong.supersdk.common.iservice;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * SuperSDK Client组件使用的用户接口
 * @author QINZH
 *
 */
@ServiceName("UserService")
public interface IUserService extends IClientService{
	/**
	 * 用户认证,以post方式进行提交
	 * @param jsonObject
	 * @return 输出格式为json
	 * @throws SuperSDKException
	 */
	@ServiceParam({"jsonData"})
	public JsonResObj loginCheck(JsonReqObj jsonData) throws SuperSDKException;
	
}
