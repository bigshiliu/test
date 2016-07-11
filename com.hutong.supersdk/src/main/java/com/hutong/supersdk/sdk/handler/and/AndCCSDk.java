package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.chongchong.CCSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andCCSDK")
public class AndCCSDk implements IPayCallBackSDK, IVerifyUserSDK {
	
	private static final String SDK_ID = "AndChongChong";
	
	private final static Log logger = LogFactory.getLog(AndCCSDk.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return CCSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser");
		String uid = input.getSdkUid();
		
		SDKVerifyRet ret = new SDKVerifyRet();
		ret.setSdkUid(uid);
		if (input.getExtra() != null && !input.getExtra().isEmpty()) {
			ret.success();
			ret.setExtra(input.getExtra());
		}
		return ret;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payback:" + paramMap.toString());
		String success = "success";
		String fail = "fail";
		
		CCSDKInfo configInfo = (CCSDKInfo) config;
		
		if (paramMap.containsKey("packageId")) {
			paramMap.put("packageId", configInfo.getGameId());
		}
		
		String sign = paramMap.get("sign");
		String encStr = SignUtil.createSignData(SignUtil.convert(paramMap), new String[] {"sign"}, true, "&");
		encStr = encStr + "&" + configInfo.getAppSecret();
		String mySign = MD5Util.MD5(encStr);
		if (mySign.equalsIgnoreCase(sign)) {
			String statusCode = String.valueOf(paramMap.get("statusCode"));
			
			if ("0000".equals(statusCode)) {
				String transactionNo = String.valueOf(paramMap.get("transactionNo"));
				String partnerTransactionNo = String.valueOf(paramMap.get("partnerTransactionNo"));
//				String productId = String.valueOf(paramMap.get("productId"));
				Float orderPrice = Float.parseFloat(String.valueOf(paramMap.get("orderPrice")));
				
				boolean iRet = callback.succeedPayment(partnerTransactionNo, transactionNo, orderPrice,
						"RMB", "", ParseJson.encodeJson(paramMap));
				if (iRet) {
					return success;
				}
				else {
					logger.error("succeed order:" + partnerTransactionNo + " failed.");
					return fail;
				}
			}
			else {
				return fail;
			}
			
		}
		else {
			logger.error("Sign Check Failed. mySign=" + mySign + " sign=" + sign);
			return fail;
		}
	}

}
