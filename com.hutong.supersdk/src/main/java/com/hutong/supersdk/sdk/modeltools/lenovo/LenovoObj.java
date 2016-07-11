package com.hutong.supersdk.sdk.modeltools.lenovo;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class LenovoObj implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("exorderno")
	private String exorderno;

	@JsonProperty("transid")
	private String transid;

	@JsonProperty("waresid")
	private String waresid;

	@JsonProperty("appid")
	private String appid;

	@JsonProperty("feetype")
	private Integer feetype;

	@JsonProperty("money")
	private Integer money;

	@JsonProperty("count")
	private Integer count;

	@JsonProperty("result")
	private Integer result;

	@JsonProperty("transtype")
	private Integer transtype;

	@JsonProperty("transtime")
	private String transtime;
	
	@JsonProperty("cpprivate")
	private String cpprivate;
	
	@JsonProperty("paytype")
	private Integer paytype;
	
	public LenovoObj() {
	}

	public String getExorderno() {
		return exorderno;
	}

	public void setExorderno(String exorderno) {
		this.exorderno = exorderno;
	}

	public String getTransid() {
		return transid;
	}

	public void setTransid(String transid) {
		this.transid = transid;
	}

	public String getWaresid() {
		return waresid;
	}

	public void setWaresid(String waresid) {
		this.waresid = waresid;
	}

	public Integer getFeetype() {
		return feetype;
	}

	public void setFeetype(Integer feetype) {
		this.feetype = feetype;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Integer getTranstype() {
		return transtype;
	}

	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
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

	public Integer getPaytype() {
		return paytype;
	}

	public void setPaytype(Integer paytype) {
		this.paytype = paytype;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}
}
