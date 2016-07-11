package com.hutong.supersdk.sdk.modeltools.baidu;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class BaiduContent implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("UID")
	private Long uid;
	
	@JsonProperty("MerchandiseName")
	private String merchandiseName;
	
	@JsonProperty("OrderMoney")
	private Double orderMoney;
	
	@JsonProperty("StartDateTime")
	private String startDateTime;
	
	@JsonProperty("BankDateTime")
	private String bankDateTime;
	
	@JsonProperty("OrderStatus")
	private Integer orderStatus;
	
	@JsonProperty("StatusMsg")
	private String statusMsg;
	
	@JsonProperty("ExtInfo")
	private String extInfo;
	
	public BaiduContent() {
	}

	public Long getUID() {
		return uid;
	}

	public void setUID(Long uID) {
		this.uid = uID;
	}

	public String getMerchandiseName() {
		return merchandiseName;
	}

	public void setMerchandiseName(String merchandiseName) {
		this.merchandiseName = merchandiseName;
	}

	public Double getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(Double orderMoney) {
		this.orderMoney = orderMoney;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getBankDateTime() {
		return bankDateTime;
	}

	public void setBankDateTime(String bankDateTime) {
		this.bankDateTime = bankDateTime;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getExtInfo() {
		return extInfo;
	}

	public void setExtInfo(String extInfo) {
		this.extInfo = extInfo;
	}
}
