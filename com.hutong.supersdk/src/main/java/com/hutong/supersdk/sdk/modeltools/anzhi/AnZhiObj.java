package com.hutong.supersdk.sdk.modeltools.anzhi;

import java.io.Serializable;

public class AnZhiObj implements Serializable {
	
	private static final long serialVersionUID = 1L;
	//订单金额,单位为分
	private String payAmount = "";
	
	private String uid = "";
	
	private long notifyTime = 0;
	
	private String cpInfo = "";
	
	private String memo = "";
	//实际支付金额,单位为分
	private String orderAmount = "";
	
	private String orderAccount = "";
	
	private int code = 0;//1是成功  其他为失败
	
	private String orderTime = "";
	
	private String msg = "";
	
	private String orderId = "";
	
	private String redBagMoney = "";

	public String getRedBagMoney() {
		return redBagMoney;
	}

	public void setRedBagMoney(String redBagMoney) {
		this.redBagMoney = redBagMoney;
	}

	/**
	 * @return the payAmount
	 */
	public String getPayAmount() {
		return payAmount;
	}

	/**
	 * @param payAmount the payAmount to set
	 */
	public void setPayAmount(String payAmount) {
		this.payAmount = payAmount;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the notifyTime
	 */
	public long getNotifyTime() {
		return notifyTime;
	}

	/**
	 * @param notifyTime the notifyTime to set
	 */
	public void setNotifyTime(long notifyTime) {
		this.notifyTime = notifyTime;
	}

	/**
	 * @return the cpInfo
	 */
	public String getCpInfo() {
		return cpInfo;
	}

	/**
	 * @param cpInfo the cpInfo to set
	 */
	public void setCpInfo(String cpInfo) {
		this.cpInfo = cpInfo;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * @return the orderAmount
	 */
	public String getOrderAmount() {
		return orderAmount;
	}

	/**
	 * @param orderAmount the orderAmount to set
	 */
	public void setOrderAmount(String orderAmount) {
		this.orderAmount = orderAmount;
	}

	/**
	 * @return the orderAccount
	 */
	public String getOrderAccount() {
		return orderAccount;
	}

	/**
	 * @param orderAccount the orderAccount to set
	 */
	public void setOrderAccount(String orderAccount) {
		this.orderAccount = orderAccount;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the orderTime
	 */
	public String getOrderTime() {
		return orderTime;
	}

	/**
	 * @param orderTime the orderTime to set
	 */
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	/**
	 * @return the msg
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @param msg the msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
}