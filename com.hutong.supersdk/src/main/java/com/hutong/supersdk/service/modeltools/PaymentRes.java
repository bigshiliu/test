package com.hutong.supersdk.service.modeltools;

import java.util.Map;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public class PaymentRes {

	private JsonResObj res;

	public PaymentRes() {
		this.res = new JsonResObj();
	}

	public PaymentRes ok() {
		this.res.ok();
		return this;
	}
	
	public PaymentRes fail() {
		this.res.fail();
		return this;
	}
	
	public PaymentRes setError(SuperSDKException ex) {
		this.res.setException(ex);
		return this;
	}

	public void setOrder(PaymentOrder order) {
		this.res.setKeyData(DataKeys.Payment.ORDER_ID, StringUtil.isNull(order.getOrderId()));
		this.res.setKeyData(DataKeys.Payment.PAY_AMOUNT, StringUtil.isNull(String.valueOf(order.getPayAmount())));
		this.res.setKeyData(DataKeys.Payment.CURRENCY_TYPE, StringUtil.isNull(order.getCurrencyType()));
		this.res.setKeyData(DataKeys.Payment.PAY_STATUS, StringUtil.isNull(order.getPayStatus()));
		this.res.setKeyData(DataKeys.Payment.PAY_TIME, StringUtil.isNull(StringUtil.valueOf(order.getPayTime())));
		this.res.setKeyData(DataKeys.Payment.CREATE_TIME, StringUtil.isNull(StringUtil.valueOf(order.getCreateTime())));
		this.res.setKeyData(DataKeys.Common.SUPERSDK_UID, StringUtil.isNull(order.getSupersdkUid()));
		this.res.setKeyData(DataKeys.Payment.GAME_UID, StringUtil.isNull(order.getAppGameUid()));
		this.res.setKeyData(DataKeys.Payment.SERVER_ID, StringUtil.isNull(order.getAppServerId()));
		this.res.setKeyData(DataKeys.Payment.PRODUCT_ID, StringUtil.isNull(order.getAppProductId()));
		this.res.setKeyData(DataKeys.Payment.PRODUCT_COUNT, StringUtil.isNull(order.getAppProductCount()));
		this.res.setKeyData(DataKeys.Payment.PRODUCT_NAME, StringUtil.isNull(order.getAppProductName()));
		this.res.setKeyData(DataKeys.Payment.ROLE_ID, StringUtil.isNull(order.getAppRoleId()));
		this.res.setKeyData(DataKeys.Payment.ROLE_NAME, StringUtil.isNull(order.getAppRoleName()));
		this.res.setKeyData(DataKeys.Payment.ROLE_GRADE, StringUtil.isNull(order.getAppRoleGrade()));
		this.res.setKeyData(DataKeys.Payment.ROLE_BALANCE, StringUtil.isNull(order.getAppRoleBalance()));
		this.res.setKeyData(DataKeys.Payment.ORDER_AMOUNT, StringUtil.isNull(String.valueOf(order.getOrderAmount())));
		this.res.setKeyData(DataKeys.Common.APP_ID, StringUtil.isNull(order.getAppId()));
		this.res.setKeyData(DataKeys.Common.CHANNEL_ID, StringUtil.isNull(order.getAppChannelId()));
		this.res.setKeyData(DataKeys.Payment.APP_DATA, StringUtil.isNull(order.getAppData()));
	}

	public void setExtra(Map<String, String> extra) {
		this.res.setExtra(extra);
	}

	public void putAllExtra(Map<String, String> extra) {
		this.res.getExtra().putAll(extra);
	}

	public JsonResObj getRes() {
		return res;
	}

}
