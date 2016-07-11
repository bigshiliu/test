package com.hutong.supersdk.iservice.notice;

import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public interface IPayNoticeService extends INoticeService {
	
	/**
	 * 异步支付通知,以post方式向 app service通知订单支付信息
	 * @param pOrder
	 */
	public void asynchronizeNotice(final PaymentOrder pOrder);
	
	/**
	 * 同步订单通知，以post方式向app server通知订单支付信息
	 * @param pOrder
	 */
	public boolean synchronizeNotice(final PaymentOrder pOrder);
	
	public void loadUnnoticedQueue();
	
	public void loadUnnoticedQueue(String appId);
}
