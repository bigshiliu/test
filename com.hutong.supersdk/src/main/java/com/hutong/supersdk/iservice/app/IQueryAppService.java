package com.hutong.supersdk.iservice.app;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

@ServiceName("queryAppService")
public interface IQueryAppService extends IAppService {
	
	@ServiceParam({"jsonData"})
	public JsonResObj queryConfig(JsonReqObj jsonData) throws SuperSDKException;
	
	@ServiceParam({"jsonData"})
	public JsonResObj queryOrder(JsonReqObj jsonData) throws SuperSDKException;
	
	@ServiceParam({"jsonData"})
	public JsonResObj queryCallBackUrl(JsonReqObj jsonData) throws SuperSDKException;
}
