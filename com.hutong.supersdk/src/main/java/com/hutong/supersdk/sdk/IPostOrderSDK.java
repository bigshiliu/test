package com.hutong.supersdk.sdk;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;

/**
 * 提交订单信息接口
 * @author Dongxu
 *
 */
public interface IPostOrderSDK extends ISDK{
	
	/**
	 * 提交订单
	 * @param paymentOrder 订单信息
	 * @param paramMap 参数
	 * @param confiMap 配置参数
	 * @param customParams 自定义参数
	 * @return
	 */
	public PostOrderRet postOrder(PaymentOrder order, Object config,
			JsonReqObj jsonReq);
}
