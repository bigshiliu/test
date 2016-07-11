package com.hutong.supersdk.sdk;

import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

/**
 * 支付成功回调接口
 * @author QINZH
 *
 */
public interface IPaySuccessHandler extends ICheckOrderSuccessHandler {
	/**
	 * 查询订单
	 * @param orderId 订单ID
	 * @return
	 */
	PaymentOrder queryPayment(String orderId);
	
	/**
	 * 订单支付成功
	 * @param orderId SuperSDKOrderID
	 * @param sdkOrderId SDKOrderId
	 * @param payAmount
	 * @param currencyType
	 * @param payType
	 * @param source TODO
	 * @return
	 */
	boolean succeedPayment(String orderId, String sdkOrderId, double payAmount,
						   String currencyType, String payType, String source);

	/**
	 * 订单支付失败
	 * @param orderId
	 * @param sdkOrderId
	 * @return
	 */
	boolean failPayment(String orderId, String sdkOrderId);

}
