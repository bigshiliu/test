package com.hutong.supersdk.sdk.modeltools;

import java.util.HashMap;
import java.util.Map;

public class SDKCheckOrderRet {
	
	public static final String STATUS_OK = "ok";
	public static final String STATUS_FAIL = "fail";
	
	private String status;
	private Map<String, String> extra = new HashMap<String, String>();
	private boolean addCheckQueue = false;

	/**
	 * 激活订单时的sdkOrderId
	 */
	private String sdkOrderId;
    /**
     * 实际支付金额
     */
	private double payAmount;
    /**
     * 支付货币类型
     */
	private String currencyType;
    /**
     * 支付类型
     */
	private String payType;
    /**
     * 第三方SDK服务器请求或返回的原始数据JSON字符串
     */
	private String source;
	
	public boolean isAddCheckQueue() {
		return addCheckQueue;
	}

	public SDKCheckOrderRet addToCheckQueue() {
		this.addCheckQueue = true;
		return this;
	}

	public SDKCheckOrderRet success(String sdkOrderId, double payAmount,
                                    String currencyType, String payType, String source) {
        this.sdkOrderId = sdkOrderId;
        this.payAmount = payAmount;
        this.currencyType = currencyType;
        this.payType = payType;
        this.source = source;

		this.status = STATUS_OK;
		return this;
	}
	
	public SDKCheckOrderRet fail() {
		this.status = STATUS_FAIL;
		this.addCheckQueue = false;
		return this;
	}
	
	public boolean isSuccess() {
		return this.status.equals(STATUS_OK);
	}
	
	public SDKCheckOrderRet setExtra(String key, String value) {
		this.extra.put(key, value);
		return this;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Map<String, String> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}

    public String getSdkOrderId() {
        return sdkOrderId;
    }

    public void setSdkOrderId(String sdkOrderId) {
        this.sdkOrderId = sdkOrderId;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
