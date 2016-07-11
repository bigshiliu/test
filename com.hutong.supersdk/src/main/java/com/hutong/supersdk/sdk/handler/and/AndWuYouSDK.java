package com.hutong.supersdk.sdk.handler.and;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.EncryptUtil;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.wuyou.WuYouSDKInfo;

@Component("andWuYouSDK")
public class AndWuYouSDK implements IVerifyUserSDK {
	
	private static final String SDK_ID = "AndWuYou";
	
	private final static Log logger = LogFactory.getLog(AndWuYouSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return WuYouSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			WuYouSDKInfo configInfo = (WuYouSDKInfo) config;
			// 获取参数
			String token = input.getSDKAccessToken();
			String secretKey = configInfo.getSecretKey();
			String appId = input.getExtraKey(DataKeys.Platform.APP_ID);
			String uid = input.getSdkUid();
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(DataKeys.Platform.PLATFORM_USER_TOKEN, token);
			paramMap.put(DataKeys.Platform.PLATFORM_USER_ID, uid);
			paramMap.put(DataKeys.Platform.APP_ID, appId);
			String sign = EncryptUtil.generateSign(paramMap, secretKey);
			// 组装请求参数
			String url = configInfo.getRequestUrl();
			JsonReqObj jsonData = new JsonReqObj();
			
			jsonData.setTime(String.valueOf(System.currentTimeMillis()));
			jsonData.setData(paramMap);
			jsonData.setSign(sign);
			
			String jsonDataStr = ParseJson.encodeJson(jsonData);
			paramMap.clear();
			paramMap.put("jsonData", jsonDataStr);
			
			String result = HttpUtil.postForm(url, paramMap);
			JsonResObj jsonRes = ParseJson.getJsonContentByStr(result, JsonResObj.class);
			if (jsonRes != null && jsonRes.isStatusOk()) {
				ret.setSdkUid(jsonRes.getData().get("id"));
				ret.setSdkAccessToken(token);
				return ret.success();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
		return ret.fail();
	}

}
