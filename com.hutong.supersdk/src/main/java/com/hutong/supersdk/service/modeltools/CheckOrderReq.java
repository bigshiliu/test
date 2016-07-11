package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public class CheckOrderReq {
	
	private JsonReqObj req;
	
	public JsonReqObj getReq() {
		return req;
	}

	public void setReq(JsonReqObj req) {
		this.req = req;
	}

	public CheckOrderReq(JsonReqObj req) {
		this.req = req;
	}

	public boolean checkData() {
		if (!this.req.containsDataKey(DataKeys.Common.APP_ID) || !this.req.containsDataKey(DataKeys.Common.SUPERSDK_UID)
				|| !this.req.containsDataKey(DataKeys.Common.CHANNEL_ID)
				|| !this.req.containsDataKey(DataKeys.Common.SIGN)) {
			return false;
		}
		return true;
	}

	public void setPaymentOrder(PaymentOrder pOrder) {
		pOrder.setAppId(this.getAppId());
		pOrder.setSupersdkUid(this.getSuperSDKUid());
		pOrder.setAppChannelId(this.getAppChannelId());
		pOrder.setOrderId(this.getOrderId());
	}

	public String getOrderId() {
		return this.req.getDataKey(DataKeys.Payment.ORDER_ID);
	}
	
	public String getSDKOrderId() {
		return this.req.getExtraKey(DataKeys.Payment.SDK_ORDER_ID);
	}

	public String getAppId() {
		return this.req.getAppId();
	}
	
	public String getSuperSDKUid() {
		return this.req.getDataKey(DataKeys.Common.SUPERSDK_UID);
	}

	public String getAppChannelId() {
		return this.req.getDataKey(DataKeys.Common.CHANNEL_ID);
	}

	public String getSign() {
		return this.req.getDataKey(DataKeys.Common.SIGN);
	}

	public String getExtraKey(String key) {
		return this.req.getExtraKey(key);
	}

}
