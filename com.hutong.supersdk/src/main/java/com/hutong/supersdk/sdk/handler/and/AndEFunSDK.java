package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.efan.EFunSDKInfo;
import com.hutong.supersdk.sdk.modeltools.efan.EFunSDKLogRet;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andEFunSDK")
public class AndEFunSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndEFun";
	
	private final static Log logger = LogFactory.getLog(AndEFunSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return EFunSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. start!");
		EFunSDKLogRet loginRet = new EFunSDKLogRet();
        try {
            EFunSDKInfo configInfo = (EFunSDKInfo) config;
            String pOrderId = paramMap.get("pOrderId");
			String userId = paramMap.get("userId");
			String remark = paramMap.get("remark");
			String creditId = paramMap.get("creditId");
			String currency = paramMap.get("currency");
			String amount = paramMap.get("amount");
			String serverCode = paramMap.get("serverCode");
			String stone = paramMap.get("stone");
			String time = paramMap.get("time");
			String payKey = configInfo.getPayKey();
			
			String md5Str = paramMap.get("md5Str");
			String myMd5Str = MD5Util.MD5(pOrderId + serverCode + creditId+userId + amount + stone + time + payKey);
			
			if(StringUtil.equalsStringIgnoreCase(md5Str, myMd5Str)){
				boolean iResult = callback.succeedPayment(remark, pOrderId, Double.parseDouble(amount),
                        currency, "", ParseJson.encodeJson(paramMap));
				if(iResult){
					loginRet.setCode("0000");
					return ParseJson.encodeJson(loginRet);
				}
			}
			loginRet.setCode("0010");
			return ParseJson.encodeJson(loginRet);
		} catch (Exception e) {
			logger.error("", e);
			loginRet.setCode("0010");
			return ParseJson.encodeJson(loginRet);

		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            EFunSDKInfo configInfo = (EFunSDKInfo) config;
            String appKey = configInfo.getAppKey();
			String signature = input.getExtraKey("signature");
			String userId = input.getSdkUid();
			String timestamp = input.getExtraKey("timestamp"); 
			String mySign = MD5Util.MD5(appKey + userId + timestamp).toUpperCase();
			if(StringUtil.equalsStringIgnoreCase(signature, mySign)){
				ret.setSdkUid(userId);
				ret.setSdkAccessToken("");
				return ret.success();
			}
			ret.setErrorMsg(this.getSDKId() + " verifyUser is Error");
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser is Error");
			return ret.fail();
		}
	}

}
