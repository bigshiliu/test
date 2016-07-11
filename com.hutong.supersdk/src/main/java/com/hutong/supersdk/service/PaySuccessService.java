package com.hutong.supersdk.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hutong.supersdk.iservice.notice.IPayNoticeService;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.IPaySuccessHandler;

@Service
public class PaySuccessService implements IPaySuccessHandler {
	
	@Autowired
	private PaymentOrderDao paymentOrderDao;
	
	@Autowired
	private IPayNoticeService payNoticeService;

	@Override
	public boolean succeedPayment(PaymentOrder pOrder, String sdkOrderId, double payAmount,
								  String currencyType, String payType, String source) {
		//订单成功，保存订单
		pOrder.paySuccess(sdkOrderId, payAmount, currencyType, payType, source);
		paymentOrderDao.saveOrUpdate(pOrder);
		//立即消息通知
		payNoticeService.asynchronizeNotice(pOrder);
		return true;
	}
	
	@Override
	public boolean succeedPayment(String orderId, String sdkOrderId, double payAmount,
								  String currencyType, String payType, String source) {
		PaymentOrder pOrder = paymentOrderDao.get(orderId);
		
		if (pOrder == null) {
			return false;
		}
		
		pOrder.paySuccess(sdkOrderId, payAmount, currencyType, payType, source);
		paymentOrderDao.saveOrUpdate(pOrder);
		
		//立即消息通知
		payNoticeService.asynchronizeNotice(pOrder);
		return true;
	}
	
	@Override
	public boolean failPayment(String orderId, String sdkOrderId) {
		PaymentOrder pOrder = paymentOrderDao.get(orderId);
		
		if (pOrder == null) {
			return false;
		}
		else if (!pOrder.isPaid()) {
			return true;
		}
		
		pOrder.payFail(sdkOrderId);
		paymentOrderDao.update(pOrder);
		return true;
	}

	@Override
	public PaymentOrder queryPayment(String orderId) {
		return paymentOrderDao.get(orderId);
	}

}
