package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public class PayNoticeData {
	
	private JsonReqObj notice;
	
	public PayNoticeData() {
		this.notice = new JsonReqObj();
	}

	public JsonReqObj getNotice() {
		return notice;
	}

	public void setNotice(JsonReqObj notice) {
		this.notice = notice;
	}
	
	public void setWithPOrder(PaymentOrder pOrder) {
		this.notice.setKeyData(DataKeys.Payment.ORDER_ID, pOrder.getOrderId());
		this.notice.setKeyData(DataKeys.Payment.SDK_ORDER_ID, pOrder.getSdkOrderId());
		this.notice.setKeyData(DataKeys.Payment.PAY_AMOUNT, String.valueOf(pOrder.getPayAmount()));
		this.notice.setKeyData(DataKeys.Payment.CURRENCY_TYPE, pOrder.getCurrencyType());
		this.notice.setKeyData(DataKeys.Payment.PAY_STATUS, pOrder.getPayStatus());
		this.notice.setKeyData(DataKeys.Payment.PAY_TIME, String.valueOf(pOrder.getPayTime().getTime()));
		this.notice.setKeyData(DataKeys.Common.SUPERSDK_UID, pOrder.getSupersdkUid());
		this.notice.setKeyData(DataKeys.Payment.GAME_UID, pOrder.getAppGameUid());
		this.notice.setKeyData(DataKeys.Payment.SERVER_ID, pOrder.getAppServerId());
		this.notice.setKeyData(DataKeys.Payment.PRODUCT_ID, pOrder.getAppProductId());
		this.notice.setKeyData(DataKeys.Payment.PRODUCT_COUNT, pOrder.getAppProductCount());
		this.notice.setKeyData(DataKeys.Payment.PRODUCT_NAME, pOrder.getAppProductName());
		this.notice.setKeyData(DataKeys.Payment.ORDER_AMOUNT, String.valueOf(pOrder.getOrderAmount()));
		this.notice.setKeyData(DataKeys.Common.APP_ID, pOrder.getAppId());
		this.notice.setKeyData(DataKeys.Common.CHANNEL_ID, pOrder.getAppChannelId());
		this.notice.setKeyData(DataKeys.Payment.APP_DATA, pOrder.getAppData());
		
		this.notice.setKeyData(DataKeys.Payment.ROLE_ID, pOrder.getAppRoleId());
		this.notice.setKeyData(DataKeys.Payment.ROLE_NAME, pOrder.getAppRoleName());
		this.notice.setKeyData(DataKeys.Payment.ROLE_GRADE, pOrder.getAppRoleGrade());
		this.notice.setKeyData(DataKeys.Payment.ROLE_BALANCE, pOrder.getAppRoleBalance());
		
		this.notice.setKeyExtra(DataKeys.Payment.SOURCE, pOrder.getSource());
	}
	
	public void ready() {
		this.notice.setTime(String.valueOf(System.currentTimeMillis()));
	}
}
