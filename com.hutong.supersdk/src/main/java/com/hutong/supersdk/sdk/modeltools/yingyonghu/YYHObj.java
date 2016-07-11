package com.hutong.supersdk.sdk.modeltools.yingyonghu;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class YYHObj implements Serializable {
	
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
	private String paytype;

	/**
	 * @return the exorderno
	 */
	public String getExorderno() {
		return exorderno;
	}

	/**
	 * @param exorderno the exorderno to set
	 */
	public void setExorderno(String exorderno) {
		this.exorderno = exorderno;
	}

	/**
	 * @return the transid
	 */
	public String getTransid() {
		return transid;
	}

	/**
	 * @param transid the transid to set
	 */
	public void setTransid(String transid) {
		this.transid = transid;
	}

	/**
	 * @return the waresid
	 */
	public String getWaresid() {
		return waresid;
	}

	/**
	 * @param waresid the waresid to set
	 */
	public void setWaresid(String waresid) {
		this.waresid = waresid;
	}

	/**
	 * @return the feetype
	 */
	public Integer getFeetype() {
		return feetype;
	}

	/**
	 * @param feetype the feetype to set
	 */
	public void setFeetype(Integer feetype) {
		this.feetype = feetype;
	}

	/**
	 * @return the money
	 */
	public Integer getMoney() {
		return money;
	}

	/**
	 * @param money the money to set
	 */
	public void setMoney(Integer money) {
		this.money = money;
	}

	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}

	/**
	 * @return the result
	 */
	public Integer getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(Integer result) {
		this.result = result;
	}

	/**
	 * @return the transtype
	 */
	public Integer getTranstype() {
		return transtype;
	}

	/**
	 * @param transtype the transtype to set
	 */
	public void setTranstype(Integer transtype) {
		this.transtype = transtype;
	}

	/**
	 * @return the transtime
	 */
	public String getTranstime() {
		return transtime;
	}

	/**
	 * @param transtime the transtime to set
	 */
	public void setTranstime(String transtime) {
		this.transtime = transtime;
	}

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
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
