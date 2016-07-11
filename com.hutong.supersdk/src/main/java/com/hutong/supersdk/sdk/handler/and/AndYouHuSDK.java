package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.youhu.YouHuSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andYouHuSDK")
public class AndYouHuSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndYouHuPay";
	
	private static final Log logger = LogFactory.getLog(AndYouHuSDK.class);
		
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YouHuSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack Start !!! paramMap:" + paramMap.toString());
		String OK = "OK";
		String FAIL = "FAIL";
		try {
			YouHuSDKInfo configInfo = (YouHuSDKInfo) config;
			//组装签名Map
			Map<String, Object> signMap = new HashMap<String, Object>();
			for(String key:paramMap.keySet()){
				signMap.put(key, paramMap.get(key));
			}
			//声明不签名字段
			String[] notIn = new String[]{"sign","version"};
			String keys = configInfo.getKeys();
			String mySign = SignUtil.createSignData(signMap, notIn, true, "&");
			mySign = MD5Util.MD5(mySign + keys);
			String sign = paramMap.get("sign");
			if(!StringUtil.equalsStringIgnoreCase(mySign, sign)){
				logger.error(this.getSDKId() + " signError, sign:" + sign + " ------ mySign:" +mySign);
				return FAIL;
			}
			String order = paramMap.get("order");
			String pipaworder = paramMap.get("pipaworder");
			double amount = Double.valueOf(paramMap.get("amount"));
			boolean iResult = callback.succeedPayment(order, pipaworder, amount, "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return OK;
		} catch (Exception e) {
			logger.error("" ,e);
		}
		return FAIL;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start !!! JsonReqObj :" + input.toString() + " configInfo :" + config.toString());
		
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			YouHuSDKInfo configInfo = (YouHuSDKInfo) config;
			String url = configInfo.getRequestUrl();
			String appId = configInfo.getAppId();
			String merchantId = configInfo.getMerchantId();
			String merchantAppId = configInfo.getMerchantAppId();
			String sid = input.getSDKAccessToken();
			String userName = input.getExtraKey("username");
			String time = input.getExtraKey("time");
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("username", userName);
			paramMap.put("appId", appId);
			paramMap.put("merchantId", merchantId);
			paramMap.put("merchantAppId", merchantAppId);
			paramMap.put("sid", sid);
			paramMap.put("time", time);
			String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && "1".equals(String.valueOf(resultMap.get("result")))) {
				ret.setSdkUid(String.valueOf(resultMap.get("uid")));
				ret.setSdkAccessToken(sid);
				ret.setExtra("username", userName);
				return ret.success();
			}
			logger.error(this.getSDKId() + " Login Error result: " + result);
			ret.setErrorMsg(this.getSDKId() + " Login Error.");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " Login Error ");
		}
		return ret.fail();
	}

}
