package com.hutong.supersdk.iservice.notice;

import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

public interface ICheckOrderService extends INoticeService {
	
	/**
	 * 
	 * @param orderId
	 */
	public void queueToCheck(final PaymentOrder pOrder);

}
