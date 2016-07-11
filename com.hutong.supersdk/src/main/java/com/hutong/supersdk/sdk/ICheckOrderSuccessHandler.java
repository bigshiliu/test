package com.hutong.supersdk.sdk;

import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public interface ICheckOrderSuccessHandler {

	/**
	 * 订单支付成功
	 * @param paymentOrder 订单信息
	 * @return
	 */
	boolean succeedPayment(PaymentOrder paymentOrder, String sdkOrderId, double payAmount,
						   String currencyType, String payType, String source);
}
