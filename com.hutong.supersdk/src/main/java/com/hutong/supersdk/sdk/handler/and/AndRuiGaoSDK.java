package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.ruigao.RuiGaoSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andRuiGaoSDK")
public class AndRuiGaoSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID ="AndRuiGao";
	
	private final static Log logger = LogFactory.getLog(AndRuiGaoSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return RuiGaoSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. start!");
		String success = "success";
		String fail = "fail";
		try {
			RuiGaoSDKInfo configInfo = (RuiGaoSDKInfo) config;
			String paymentStatusCode = paramMap.get("PaymentStatusCode");
			if(!"0".equals(paymentStatusCode))
				return fail;

			//商户密钥
			String merchantKey = configInfo.getMerchantKey();
			//商户ID
			String merId = paramMap.get("MerId");
			//通知加密字符串
			String encString = paramMap.get("EncString");
			//订单ID
			String orderId = paramMap.get("OrderId");
			//订单金额
			String money = paramMap.get("Money");
			//加密验证
			String myEncStr = merId + orderId + money + merchantKey;
			//MD5加密
			String myEnc = MD5Util.MD5(myEncStr);
			logger.debug(this.getSDKId() + " payCallBack. encString:" + encString + " myEnc:" + myEnc);
			if(!StringUtil.equalsStringIgnoreCase(encString, myEnc))
				return fail;

			//支付注释
			String note = paramMap.get("Note");
			boolean iResult = callback.succeedPayment(note, orderId, Double.parseDouble(money), "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
		return fail;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			RuiGaoSDKInfo configInfo = (RuiGaoSDKInfo) config;
			//商户ID
			String merId = configInfo.getMerchantId();
			//固定参数,请求编号
			String act = "4";
			//用户的鱼丸互动帐号
			String uin = input.getSdkUid();
			//用户的登录SessionKey
			String sessionKey = input.getSDKAccessToken();
			//商户密钥
			String merchantKey = configInfo.getMerchantKey();
			//通知加密字符串
			String encString = merId + act + uin + sessionKey + merchantKey;
			logger.debug(this.getSDKId() + " verifyUser. encString:" + encString);
			String encStringMd5 = MD5Util.MD5(encString);
			logger.debug(this.getSDKId() + " verifyUser. encStringMd5:" + encStringMd5);
			String url = configInfo.getRequestUrl();
			
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("MerId", merId);
			reqMap.put("Act", act);
			reqMap.put("Uin", uin);
			reqMap.put("SessionKey", sessionKey);
			reqMap.put("EncString", encStringMd5);
			
			String result = HttpUtil.postForm(url, reqMap);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && "1".equals(String.valueOf(resultMap.get("ErrorCode")))){
				ret.setSdkUid(uin);
				ret.setSdkAccessToken(sessionKey);
				return ret.success();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Check Error!");
		return ret.fail();
	}
}
