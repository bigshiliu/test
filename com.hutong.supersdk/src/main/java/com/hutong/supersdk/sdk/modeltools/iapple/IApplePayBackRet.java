package com.hutong.supersdk.sdk.modeltools.iapple;

import com.hutong.supersdk.common.util.ParseJson;

public class IApplePayBackRet {
	
	private int status;
	private long translDO;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getTranslDO() {
		return translDO;
	}
	public void setTranslDO(long translDO) {
		this.translDO = translDO;
	}

	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}
	
}
