package com.hutong.supersdk.mysql.inst.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;
import com.hutong.supersdk.service.PayNoticeService;

@Entity
@Table(name = "T_PAY_NOTICE_QUEUE")
public class PayNoticeQueue extends ABaseDomain {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "Order_Id")
	private String orderId;
	
	@Column(name = "Notice_Times")
	private Integer noticeTimes;
	
	@Column(name = "Queue_Type")
	private String queueType;
	
	@Column(name = "App_Id")
	private String appId;
	
	public PayNoticeQueue() {}
	
	public PayNoticeQueue(PaymentOrder pOrder) {
		this.orderId = pOrder.getOrderId();
		this.queueType = PayNoticeService.FIRST_QUEUE;
		
		this.noticeTimes = 0;
		
		this.appId = pOrder.getAppId();
	}
	
	public void notice() {
		this.noticeTimes++;
		
		if (this.queueType.equals(PayNoticeService.FIRST_QUEUE)) {
			if (this.noticeTimes >= PayNoticeService.FIRST_QUEUE_NOTICE_TIMES) {
				this.queueType = PayNoticeService.SECOND_QUEUE;
				this.noticeTimes = 0;
			}
		}
		else if (this.queueType.equals(PayNoticeService.SECOND_QUEUE)) {
			if (this.noticeTimes >= PayNoticeService.SECOND_QUEUE_NOTICE_TIMES) {
				this.queueType = PayNoticeService.FAILED_QUEUE;
			}
		}
		else {
		}
	}
	
	public void success() {
		this.queueType = PayNoticeService.NOTICE_SUCCESS;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public Integer getNoticeTimes() {
		return noticeTimes;
	}

	public void setNoticeTimes(Integer noticeTimes) {
		this.noticeTimes = noticeTimes;
	}

	public String getQueueType() {
		return queueType;
	}

	public void setQueueType(String queueTyme) {
		this.queueType = queueTyme;
	}

	public String getAppId() {
		return this.appId;
	}
	
	public void setAppId(String appId) {
		this.appId = appId;
	}

}
