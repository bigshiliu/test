package com.hutong.supersdk.sdk.modeltools._3gmenhu;

import java.io.Serializable;

public class ThirdGData implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String orderid;
	
	private int gameid;
	
	private String token;
	
	private int cpid;
	
	private int access;
	
	private float paytotalfee;
	
	private int paytypeid;
	
	private String cporderid;
	
	private String stime;

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public int getGameid() {
		return gameid;
	}

	public void setGameid(int gameid) {
		this.gameid = gameid;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getCpid() {
		return cpid;
	}

	public void setCpid(int cpid) {
		this.cpid = cpid;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public float getPaytotalfee() {
		return paytotalfee;
	}

	public void setPaytotalfee(float paytotalfee) {
		this.paytotalfee = paytotalfee;
	}

	public int getPaytypeid() {
		return paytypeid;
	}

	public void setPaytypeid(int paytypeid) {
		this.paytypeid = paytypeid;
	}

	public String getCporderid() {
		return cporderid;
	}

	public void setCporderid(String cporderid) {
		this.cporderid = cporderid;
	}

	public String getStime() {
		return stime;
	}

	public void setStime(String stime) {
		this.stime = stime;
	}

	
}
