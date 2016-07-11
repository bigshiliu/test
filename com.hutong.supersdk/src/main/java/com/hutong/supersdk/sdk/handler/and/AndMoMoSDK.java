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
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.momo.MoMoSDKInfo;

@Component("andMoMoSDK")
public class AndMoMoSDK implements IVerifyUserSDK, IPayCallBackSDK {

	public static final String SDK_ID = "AndMoMo";
	
	private final static Log logger = LogFactory.getLog(AndMoMoSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return MoMoSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		final String success = "success";
		final String failed = "failed";
		try {
			MoMoSDKInfo configInfo = (MoMoSDKInfo) config;
			String appId = configInfo.getAppId();
			String appSecret = configInfo.getAppKey();
			String tradeNo = paramMap.get("trade_no");
			String sign = paramMap.get("sign");
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("appid", appId);
			reqMap.put("app_secret", appSecret);
			reqMap.put("trade_no", tradeNo);
			reqMap.put("sign", sign);
			String url = configInfo.getPaymentCheckUrl();
			String result = HttpUtil.postForm(url, reqMap);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !"0".equals(String.valueOf(resultMap.get("ec")))){
				return failed;
			}
			String orderId = paramMap.get("app_trade_no");
			String platformOrderId = paramMap.get("trade_no");
			float money = "0".equals(paramMap.get("currency_type")) ? Float.parseFloat(paramMap.get("total_fee"))
					: Float.parseFloat(paramMap.get("total_fee")) / 10;
			String currencyType = "RMB";
			String payway = paramMap.get("channel_type");
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, money, currencyType, payway, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("",e);
		}
        return failed;
    }

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
        SDKVerifyRet ret = new SDKVerifyRet();
        try {
            MoMoSDKInfo configInfo = (MoMoSDKInfo) config;
            String appId = configInfo.getAppId();
            String appSecret = configInfo.getAppKey();
            String vToken = input.getSDKAccessToken();
            String userId = input.getSdkUid();
            Map<String, String> reqMap = new HashMap<String, String>();
            reqMap.put("appid", appId);
            reqMap.put("app_secret", appSecret);
            reqMap.put("vtoken", vToken);
            reqMap.put("userid", userId);

            String url = configInfo.getRequestUrl();
            String result = HttpUtil.postForm(url, reqMap);
            logger.debug(result);
            ret.setSdkUid(userId);
            ret.setSdkAccessToken(vToken);
            ret.setSdkRefreshToken("");
            return ret.success();
        } catch (Exception e) {
            logger.error("", e);
            ret.setErrorMsg(e.getMessage());
            return ret.fail();
        }
    }

}
