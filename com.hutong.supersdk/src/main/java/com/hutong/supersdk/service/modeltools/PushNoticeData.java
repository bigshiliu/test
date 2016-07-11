package com.hutong.supersdk.service.modeltools;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;

/**
 * 推送流程处理转换对象,
 * 由jsonData进行转换,方便操作
 * @author QINZH
 *
 */
public class PushNoticeData {
	
	private JsonReqObj jsonData;
	private String push_scope;
	
	private String PUSH_SCOPE_ALL = "ALL";
	
	public PushNoticeData(){
		this.jsonData = new JsonReqObj();
	}
	
	public PushNoticeData(JsonReqObj jsonData){
		this.jsonData = jsonData;
		this.push_scope = this.getPushScope();
	}
	
	public String getPushScope() {
		return this.jsonData.getDataKey(DataKeys.Push.PUSH_SCOPE);
	}
	
	public String getPushMessage() {
		return this.jsonData.getDataKey(DataKeys.Push.PUSH_MESSAGE);
	}
	
	public boolean isAll() {
		return this.push_scope.equals(PUSH_SCOPE_ALL);
	}
	
	public String getChannelId() {
		return this.jsonData.getDataKey(DataKeys.Common.CHANNEL_ID);
	}
	
	public String getAppId() {
		return this.jsonData.getDataKey(DataKeys.Common.APP_ID);
	}
	
}
