package com.hutong.supersdk.sdk;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;

/**
 * 检查订单接口
 * @author QINZH
 *
 */
public interface ICheckOrderSDK extends ISDK {

	/**
	 * SDK实现的检查订单接口
	 * @param pOrder 订单对象
	 * @param jsonData 客户端发送的checkOrder参数，如果为null表示是服务器自动启动的检查订单操作
	 * @param config SDK配置信息
	 * @return
	 */
	SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData, Object config);
}
