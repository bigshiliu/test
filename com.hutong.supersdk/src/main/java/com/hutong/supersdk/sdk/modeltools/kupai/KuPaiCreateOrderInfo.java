package com.hutong.supersdk.sdk.modeltools.kupai;

public class KuPaiCreateOrderInfo {
	
	//应用编号
	private String appid;
	//商品编号
	private String waresid;
	//商户订单号
	private String cporderid;
	//货币类型
	private String currency;
	//用户在商户中的唯一表示
	private String appuserid;
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getWaresid() {
		return waresid;
	}
	public void setWaresid(String waresid) {
		this.waresid = waresid;
	}
	public String getCporderid() {
		return cporderid;
	}
	public void setCporderid(String cporderid) {
		this.cporderid = cporderid;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getAppuserid() {
		return appuserid;
	}
	public void setAppuserid(String appuserid) {
		this.appuserid = appuserid;
	}
}
