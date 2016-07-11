package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * 查询订单请求对象
 * @author QINZH
 *
 */
public class QueryOrdersReq {
	
	private JsonReqObj req;
	
	public JsonReqObj getReq() {
		return req;
	}

	public void setReq(JsonReqObj req) {
		this.req = req;
	}

	public QueryOrdersReq(JsonReqObj req) {
		this.req = req;
	}

	public boolean checkData() {
		if (!this.req.containsDataKey(DataKeys.Common.APP_ID) || !this.req.containsDataKey(DataKeys.Common.SUPERSDK_UID)) {
			return false;
		}
		return true;
	}

	public String getAppId() {
		return this.req.getAppId();
	}
	
	public String getSuperSDKUid() {
		return this.req.getDataKey(DataKeys.Common.SUPERSDK_UID);
	}

	public String getOrderNum() {
		return this.req.getDataKey(DataKeys.QueryOrder.QUERY_MAX_ORDER_NUM);
	}

	public String getStartTime() {
		return this.req.getDataKey(DataKeys.QueryOrder.ORDER_START_TIME);
	}

	public String getEndTime() {
		return this.req.getDataKey(DataKeys.QueryOrder.ORDER_END_TIME);
	}

}
