package com.hutong.supersdk.mysql.inst.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;
import com.hutong.supersdk.service.CheckOrderService;

@Entity
@Table(name = "T_CHECK_ORDER_QUEUE")
public class CheckOrderQueue extends ABaseDomain {
	
private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "Order_Id")
	private String orderId;
	
	@Column(name = "App_Id")
	private String appId;
	
	@Column(name = "Notice_Times")
	private Integer noticeTimes;
	
	@Column(name = "Queue_Type")
	private String queueType;
	
	public CheckOrderQueue() {}
	
	public CheckOrderQueue(PaymentOrder pOrder) {
		this.orderId = pOrder.getOrderId();
		this.appId = pOrder.getAppId();
		
		this.queueType = CheckOrderService.CURRENT_QUEUE;
		
		this.noticeTimes = 0;
	}
	
	public void check() {
		this.noticeTimes++;
		
		if (this.queueType.equals(CheckOrderService.CURRENT_QUEUE)) {
			if (this.noticeTimes >= CheckOrderService.CURRENT_QUEUE_CHECK_TIMES) {
				this.queueType = CheckOrderService.FIRST_QUEUE;
				this.noticeTimes = 0;
			}
		}
		else if (this.queueType.equals(CheckOrderService.FIRST_QUEUE)) {
			if (this.noticeTimes >= CheckOrderService.FIRST_QUEUE_NOTICE_TIMES) {
				this.queueType = CheckOrderService.SECOND_QUEUE;
				this.noticeTimes = 0;
			}
		}
		else if (this.queueType.equals(CheckOrderService.SECOND_QUEUE)) {
			if (this.noticeTimes >= CheckOrderService.SECOND_QUEUE_NOTICE_TIMES) {
				this.queueType = CheckOrderService.FAILED_QUEUE;
			}
		}
		else {
		}
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
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

}
