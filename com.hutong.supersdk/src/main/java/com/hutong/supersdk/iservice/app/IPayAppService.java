package com.hutong.supersdk.iservice.app;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

@ServiceName("payAppService")
public interface IPayAppService extends IAppService {
	
	/**
	 * 请求订单,在SuperSDK创建订单,APP获取订单号时调用此方法
	 * @param jsonData
	 * @return 返回格式为json
	 * @throws SuperSDKException
	 */
	@ServiceParam({"jsonData"})
	public JsonResObj createOrder(JsonReqObj jsonData) throws SuperSDKException;
	
	@ServiceParam({"jsonData"})
	public JsonResObj checkOrder(JsonReqObj jsonData) throws SuperSDKException;
}
