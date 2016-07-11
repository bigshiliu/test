package com.hutong.supersdk.sdk.modeltools.aibei;

import org.codehaus.jackson.annotate.JsonProperty;

public class AiBeiObj {

	@JsonProperty("transtype")
	private Integer transtype;

	@JsonProperty("cporderid")
	private String cporderid;

	@JsonProperty("transid")
	private String transid;
	
	@JsonProperty("appuserid")
	private String appuserid;
	
	@JsonProperty("appid")
	private String appid;

	@JsonProperty("waresid")
	private Integer waresid;
	
	@JsonProperty("feetype")
	private Integer feetype;
	
	@JsonProperty("money")
	private Float money;
	
	@JsonProperty("currency")
	private String currency;
	
	@JsonProperty("result")
	private Integer result;
	
	@JsonProperty("transtime")
	private String transtime;
	
	@JsonProperty("cpprivate")
	private String cpprivate;
	
	@JsonProperty("paytype")
	private String paytype;

	public Integer getTranstype() {
		return transtype;
	}

	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
	}

	public String getCporderid() {
		return cporderid;
	}

	public void setCporderid(String cporderid) {
		this.cporderid = cporderid;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getAppuserid() {
		return appuserid;
	}

	public void setAppuserid(String appuserid) {
		this.appuserid = appuserid;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public Integer getWaresid() {
		return waresid;
	}

	public void setWaresid(Integer waresid) {
		this.waresid = waresid;
	}

	public Integer getFeetype() {
		return feetype;
	}

	public void setFeetype(Integer feetype) {
		this.feetype = feetype;
	}

	public Float getMoney() {
		return money;
	}

	public void setMoney(Float money) {
		this.money = money;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public String getTranstime() {
		return transtime;
	}

	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}

	public String getCpprivate() {
		return cpprivate;
	}

	public void setCpprivate(String cpprivate) {
		this.cpprivate = cpprivate;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}
}

