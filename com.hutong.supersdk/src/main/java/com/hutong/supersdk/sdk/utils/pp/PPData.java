package com.hutong.supersdk.sdk.utils.pp;

import org.codehaus.jackson.annotate.JsonProperty;

public class PPData {
	
//	{
//	    "order_id": "2015020430422813",
//	    "billno": "3cd05357cc0a47a7bc3ea8551465c0e2",
//	    "account": "realzjx3",
//	    "amount": "10.00",
//	    "status": 0,
//	    "app_id": "5341",
//	    "uuid": "",
//	    "zone": "0",
//	    "roleid": "001100085480"
//	}
	
	@JsonProperty("order_id")
	private String order_id;
	
	@JsonProperty("billno")
	private String billno;
	
	@JsonProperty("account")
	private String account;
	
	@JsonProperty("amount")
	private float amount;
	
	@JsonProperty("status")
	private Integer status;
	
	@JsonProperty("app_id")
	private String app_id;
	
	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("zone")
	private String zone;
	
	@JsonProperty("roleid")
	private String roleid;

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getBillno() {
		return billno;
	}

	public void setBillno(String billno) {
		this.billno = billno;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
}
