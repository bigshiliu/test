package com.hutong.supersdk.common.iservice;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * TODO
 * 此类的接口需要重构
 */
@ServiceName("PushService")
public interface IPushService extends IClientService {
	

	/**
	 * 信息推送,以post方式进行提交由GameServer推送的消息
	 * @param message
	 * @return 输出格式为json
	 * @throws SuperSDKException
	 */
	@ServiceParam({"jsonData"})
	public JsonResObj pushMessage(JsonReqObj message) throws SuperSDKException; 
}
