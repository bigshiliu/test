package com.hutong.supersdk.sdk.modeltools.mumayi;

public class MMYPayResult {
	
	private String uid;
	private String orderID;
	private String payType;
	private String prodctName;
	private String productPrice;
	private String productDesc;
	private String orderTime;
	private String tradeSign;
	private String tradeState;
	
	public String getTradeState() {
		return tradeState;
	}
	public void setTradeState(String tradeState) {
		this.tradeState = tradeState;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getOrderID() {
		return orderID;
	}
	public void setOrderID(String orderID) {
		this.orderID = orderID;
	}
	public String getProdctName() {
		return prodctName;
	}
	public void setProdctName(String prodctName) {
		this.prodctName = prodctName;
	}
	public String getProductPrice() {
		return productPrice;
	}
	public void setProductPrice(String productPrice) {
		this.productPrice = productPrice;
	}
	public String getProductDesc() {
		return productDesc;
	}
	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getTradeSign() {
		return tradeSign;
	}
	public void setTradeSign(String tradeSign) {
		this.tradeSign = tradeSign;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
}
