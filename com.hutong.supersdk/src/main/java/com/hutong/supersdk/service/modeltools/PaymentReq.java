package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public class PaymentReq {
	private JsonReqObj req;

	public PaymentReq(JsonReqObj req) {
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

		pOrder.setOrderAmount(this.getOrderAmount());
		pOrder.setAppGameUid(this.getGameUid());
		pOrder.setAppServerId(this.getServerId());
		pOrder.setAppProductId(this.getProductId());
		pOrder.setAppProductCount(this.getProductCount());
		pOrder.setAppProductName(this.getProductName());
		pOrder.setAppRoleId(this.getAppRoleId());
		pOrder.setAppRoleName(this.getAppRoleName());
		pOrder.setAppRoleGrade(this.getAppRoleGrade());
		pOrder.setAppRoleBalance(this.getAppRoleBalance());
		
		pOrder.setAppData(this.req.getDataKey(DataKeys.Payment.APP_DATA));
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

	public String getGameUid() {
		return this.req.getDataKey(DataKeys.Payment.GAME_UID);
	}

	public Double getOrderAmount() {
		return Double.valueOf(this.req.getDataKey(DataKeys.Payment.ORDER_AMOUNT));
	}

	public String getServerId() {
		return this.req.getDataKey(DataKeys.Payment.SERVER_ID);
	}

	public String getProductId() {
		return this.req.getDataKey(DataKeys.Payment.PRODUCT_ID);
	}

	public String getProductCount() {
		return this.req.getDataKey(DataKeys.Payment.PRODUCT_COUNT);
	}

	public String getProductName() {
		return this.req.getDataKey(DataKeys.Payment.PRODUCT_NAME);
	}

	public String getAppRoleId() {
		return this.req.getDataKey(DataKeys.Payment.ROLE_ID);
	}

	public String getAppRoleName() {
		return this.req.getDataKey(DataKeys.Payment.ROLE_NAME);
	}
	
	public String getAppRoleGrade() {
		return this.req.getDataKey(DataKeys.Payment.ROLE_GRADE);
	}
	
	public String getAppRoleBalance() {
		return this.req.getDataKey(DataKeys.Payment.ROLE_BALANCE);
	}
}
