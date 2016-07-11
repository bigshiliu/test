package com.hutong.supersdk.common.model;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.util.ParseJson;

public class PushMessage extends JsonReqObj {
	
	private JsonReqObj jsonData;
	
	private Map<String, String> aliasMap = new HashMap<String, String>();
	
	public static String PUSH_SCOPE_ALL = "ALL";
	
	public static String PUSH_SCOPE_ALIAS = "ALIAS";
	
	public PushMessage() {
		super();
	}
	
	public PushMessage(JsonReqObj jsonData) {
		this.jsonData = jsonData;
	}

	@JsonIgnore
	public void setAppId(String appId) {
		this.setKeyData(DataKeys.Common.APP_ID, appId);
	}
	
	@JsonIgnore
	public String getAppId() {
		return this.jsonData.getDataKey(DataKeys.Common.APP_ID);
	}
	
	@JsonIgnore
	public String getTimeToLive() {
		return this.jsonData.getDataKey(DataKeys.Push.TIME_TO_LIVE);
	}
	
	@JsonIgnore
	public void setTimeToLive(long time_to_live) {
		this.setKeyData(DataKeys.Push.TIME_TO_LIVE, String.valueOf(time_to_live));
	}
	
	@JsonIgnore
	public void setPushScope(String scope) {
		this.setKeyData(DataKeys.Push.PUSH_SCOPE, scope);
	}
	
	@JsonIgnore
	public String getPushScope() {
		return this.jsonData.getDataKey(DataKeys.Push.PUSH_SCOPE);
	}
	
	@JsonIgnore
	public void setChannelId(String channels) {
		this.setKeyData(DataKeys.Common.CHANNEL_ID, channels);
	}
	
	@JsonIgnore
	public String getChannelId() {
		return this.jsonData.getDataKey(DataKeys.Common.CHANNEL_ID);
	}
	
	@JsonIgnore
	public void setPushMessage(String msg) {
		this.setKeyData(DataKeys.Push.PUSH_MESSAGE, msg);
	}
	
	@JsonIgnore
	public String getPushMessage() {
		return this.jsonData.getDataKey(DataKeys.Push.PUSH_MESSAGE);
	}
	
	@JsonIgnore
	public void setPushScope2All(){
		this.setKeyData(DataKeys.Push.PUSH_SCOPE, PUSH_SCOPE_ALL);
	}
	
	@JsonIgnore
	public void setPushScope2Alias(){
		this.setKeyData(DataKeys.Push.PUSH_SCOPE, PUSH_SCOPE_ALIAS);
	}
	
	@JsonIgnore
	public String getPushAliasMap(){
		return this.jsonData.getDataKey(DataKeys.Push.PUSH_ALIAS_MAP);
	}

	@JsonIgnore
	public boolean isAll() {
		return this.getPushScope().equals(PUSH_SCOPE_ALL);
	}
	
	@JsonIgnore
	public boolean isAlias(){
		return this.getPushScope().equalsIgnoreCase(PUSH_SCOPE_ALIAS);
	}
	
	@JsonIgnore
	public void putPushAlias(String channelId, String alias){
		aliasMap.put(channelId, alias);
		this.setKeyData(DataKeys.Push.PUSH_ALIAS_MAP, ParseJson.encodeJson(aliasMap));
	}

	public static void main(String[] args) {
//		
//		PushMessage push = new PushMessage();
//		push.setAppId("test");
//		push.setPushScope2Alias();
//		push.setPushAlias("qqqq", new String[]{"111","222","333"});
//		push.setPushAlias("wwww", new String[]{"yyy","uuu","iii"});
//		
//		String aliasMapStr = push.getPushAliasMap();
//		Map<String, String[]> aliasMap = ParseJson.getJsonContentByStr(aliasMapStr, Map.class);
//		System.out.println(ParseJson.encodeJson(push));
	}
}
