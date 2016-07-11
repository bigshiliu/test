package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.iTools.IToolsSDKInfo;
import com.hutong.supersdk.sdk.utils.RSASignature;

@Component("iOSYYToolsSDK")
public class IOSYYIToolsSDK implements IVerifyUserSDK, IPayCallBackSDK {

	private static final String SDK_ID = "IOSYYITools";
	
	private final static Log logger = LogFactory.getLog(IOSYYIToolsSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return IToolsSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "success";
		String fail = "fail";
		try {
			String notifyData = paramMap.get("notify_data");
			String sign = paramMap.get("sign");

			IToolsSDKInfo configInfo = (IToolsSDKInfo) config;
			String notifyJson = RSASignature.decrypt(notifyData, configInfo.getPubKey());
			if(!RSASignature.verify(notifyJson, sign, configInfo.getPubKey()))
				return fail;

			Map info = ParseJson.getJsonContentByStr(notifyJson, Map.class);
			if(null == info)
				return fail;

			if(!"success".equalsIgnoreCase(String.valueOf(info.get("result"))))
				return fail;

			String platformId = String.valueOf(info.get("order_id"));
			if (platformId.equals("") || platformId.equals("null"))
				return fail;

			String orderId = String.valueOf(info.get("order_id_com"));
			double amount = Double.parseDouble(String.valueOf(info.get("amount")));
			boolean iResult = callback.succeedPayment(orderId, platformId, amount, "RMB", "", ParseJson.encodeJson(paramMap));
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
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            IToolsSDKInfo configInfo = (IToolsSDKInfo) config;
            String sessionid = input.getSDKAccessToken();
            String signStr = "appid=" + configInfo.getAppId() + "&sessionid=" + sessionid;
            logger.debug(this.getSDKId() + " verifyUser signStr:" + signStr);
            String sign = DigestUtils.md5Hex(signStr);
            String url = configInfo.getRequestUrl() + "&sign=" + sign + "&" + signStr;
            Map requestMap = ParseJson.getJsonContentByUrl(url, Map.class);
            if (requestMap == null || !"success".equals(String.valueOf(requestMap.get("status")))) {
                ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
                return ret.fail();
            }
            //按照_从token中获取userId
            String arr[] = sessionid.split("_");
            String userId = arr[0];
            ret.setSdkUid(userId);
            ret.setSdkAccessToken(sessionid);
            return ret.success();
        } catch (Exception e) {
            logger.error("", e);
        }
        return ret.fail();
    }

}
