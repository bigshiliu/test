package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
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
import com.hutong.supersdk.sdk.modeltools.dl.DLSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andDLSDK")
public class AndDLSDK implements IPayCallBackSDK, IVerifyUserSDK {
	
	public static final String SDK_ID = "AndDangLe";
	
	private static final Log logger = LogFactory.getLog(AndDLSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return DLSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
        SDKVerifyRet ret = new SDKVerifyRet();
        try {
            DLSDKInfo configInfo = (DLSDKInfo) config;
			String platformUserId = input.getSdkUid();
			String token = input.getSDKAccessToken();
			String appId = configInfo.getAppId();
			String appKey = configInfo.getAppKey();
			String url = configInfo.getRequestUrl();
			String sig = MD5Util.MD5(appId + "|" + appKey + "|" + token + "|" + platformUserId);
			String urlStr = url + "?appid=" + appId + "&token=" + token + "&umid=" + platformUserId + "&sig=" + sig;

			String result = HttpUtil.get(urlStr);
			logger.debug(result);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && 2000 == Integer.parseInt(String.valueOf(resultMap.get("msg_code")))){
				int interval = Integer.parseInt(String.valueOf(resultMap.get("valid")));
				if(1 == interval){
					ret.setSdkUid(platformUserId);
					ret.setSdkAccessToken(token);
					ret.setSdkRefreshToken("");
					return ret.success();
				}
			}
			logger.error(this.getSDKId() + " Login Error. result:" + result);
			ret.setErrorMsg(this.getSDKId() + " Login Error.");
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
			return ret.fail();
		}
		
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        logger.debug(paramMap.toString());
        try {
            DLSDKInfo configInfo = (DLSDKInfo) config;
            String paymentKey = configInfo.getPaymentKey();
            String result = paramMap.get("result");
            String money = paramMap.get("money");
            String orderNo = paramMap.get("order");
            String memberId = paramMap.get("mid");
            String dateTime = paramMap.get("time");
            String signature = paramMap.get("signature");
            String ext = paramMap.get("ext");
            String str = "order=" + orderNo + "&money=" + money + "&mid="
                    + memberId + "&time=" + dateTime + "&result=" + result
                    + "&ext=" + ext + "&key=" + paymentKey;
            String sig = MD5Util.MD5(str).toLowerCase();
            String orderId = paramMap.get("ext");
            if(null == result || !"1".equals(result) || !signature.equalsIgnoreCase(sig)){
                logger.error("SignCheck Erorr. mySign=" + sig + " sign=" + signature);
                return "fail";
            }
            String currencyType = "RMB";
            String payType = "";
            boolean iResult = callback.succeedPayment(orderId, "", Float.parseFloat(money),
                    currencyType, payType, ParseJson.encodeJson(paramMap));
            if(iResult)
                return "success";
            return "fail";
        } catch (Exception e) {
            logger.error("", e);
            return "fail";
        }
    }

}
