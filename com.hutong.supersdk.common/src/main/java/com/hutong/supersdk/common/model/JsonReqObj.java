package com.hutong.supersdk.common.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.util.ParseJson;

/**
 * 请求对象
 * @author QINZH
 * time	动作时间
 * data 业务参数
 * extra 额外参数
 */
public class JsonReqObj {
	
	private String time;
	private Map<String, String> data;
	private Map<String, String> extra;
	
	public JsonReqObj() {
		this.data = new HashMap<String, String>();
		this.extra = new HashMap<String, String>();
	}

	/**
	 * 参数检查
	 * @param checkList 参数检查数组
	 * @return 参数数组中的参数全部存在并且不为空返回true,否则返回false
	 */
	public boolean checkParmas(String[] checkList) {
        if (checkList == null)
            return true;

		for (String key : checkList) {
			if (!this.data.containsKey(key)
					|| this.data.get(key) == null
					|| this.data.get(key).isEmpty())
				return false;
		}
		return true;
	}
	
	public void setSign(String sign) {
		this.data.put(DataKeys.Common.SIGN, sign);
	}
	
	@JsonIgnore
	public String getSign() {
		return this.data.get(DataKeys.Common.SIGN);
	}
	
	@JsonIgnore
	public String getAppId() {
		if (this.data.containsKey(DataKeys.Common.APP_ID)) {
			return this.data.get(DataKeys.Common.APP_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getAppChannelId() {
		if (this.data.containsKey(DataKeys.Common.CHANNEL_ID)) {
			return this.data.get(DataKeys.Common.CHANNEL_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getSdkId() {
		if (this.data.containsKey(DataKeys.Common.SDK_ID)) {
			return this.data.get(DataKeys.Common.SDK_ID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getSdkUid() {
		if(this.data.containsKey(DataKeys.LoginCheck.SDK_UID)){
			return this.data.get(DataKeys.LoginCheck.SDK_UID);
		}
		return null;
	}
	
	@JsonIgnore
	public String getSDKAccessToken() {
		if(this.data.containsKey(DataKeys.LoginCheck.ACCESS_TOKEN)) {
			return this.data.get(DataKeys.LoginCheck.ACCESS_TOKEN);
		}
		return null;
	}
	
	@JsonIgnore
	public String getSDKRefreshToken() {
		if(this.data.containsKey(DataKeys.LoginCheck.REFRESH_TOKEN)) {
			return this.data.get(DataKeys.LoginCheck.REFRESH_TOKEN);
		}
		return null;
	}
	
	@JsonIgnore
	public String getUid() {
		if(this.data.containsKey(DataKeys.Common.SUPERSDK_UID)) {
			return this.data.get(DataKeys.Common.SUPERSDK_UID);
		}
		return null;
	}
	
	public boolean containsDataKey(String key) {
		return this.data.containsKey(key);
	}
	
	public boolean containsExtraKey(String key) {
		return this.extra.containsKey(key);
	}
	
	public String getDataKey(String key) {
		if (this.data.containsKey(key)) {
			return this.data.get(key);
		}
		return "";
	}
	
	public void setKeyData(String key, String data) {
		this.data.put(key, data);
}
	
	public String getExtraKey(String key) {
		if (this.extra.containsKey(key))
			return this.extra.get(key);
		return "";
	}
	
	public void setKeyExtra(String key, String extra) {
		this.extra.put(key, extra);
	}
	
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Map<String, String> getData() {
		return data;
	}
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	public Map<String, String> getExtra() {
		return extra;
	}
	public void setExtra(Map<String, String> extra) {
		this.extra = extra;
	}
	
	@Override
	public String toString() {
		return ParseJson.encodeJson(this);
	}
	
}
