package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
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
import com.hutong.supersdk.sdk.modeltools.youlong.YouLongSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andYouLongSDK")
public class AndYouLongSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndYouLong";
	
	private final Log logger = LogFactory.getLog(AndYouLongSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YouLongSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String success = "OK";
		String failure = "FAIL";
		try {
			YouLongSDKInfo configInfo = (YouLongSDKInfo) config;

			String orderId = paramMap.get("orderId");
			String userName = paramMap.get("userName");
			String amount = paramMap.get("amount");
			String extra = paramMap.get("extra");
			String flag = paramMap.get("flag");
			String pKey = configInfo.getpKey();
			if(StringUtils.isBlank(flag)){
				return failure;
			}
			String sign = MD5Util.MD5(orderId + userName + amount + extra + pKey);
			if(!StringUtil.equalsStringIgnoreCase(flag, sign))
				return failure;

			Double amt = Double.parseDouble(amount);
			boolean iResult = callback.succeedPayment(orderId, "", amt, "RMB", "", ParseJson.encodeJson(paramMap));
			if (iResult)
				return success;
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}
		return failure;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            YouLongSDKInfo configInfo = (YouLongSDKInfo) config;
            String token = input.getSDKAccessToken();
            String ip = input.getExtraKey("ip");
            String pid = configInfo.getPid();
            String url = configInfo.getRequestUrl();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", token);
            paramMap.put("pid", pid);
            paramMap.put("token", token);

            if(StringUtils.isEmpty(ip)){
                paramMap.put("ip", ip);
            }
            String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && "1".equals(String.valueOf(resultMap.get("state")))){
                ret.setSdkUid(String.valueOf(resultMap.get("username")));
                ret.setExtra("username", String.valueOf(resultMap.get("username")));
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            logger.error("Login Verify Error. result=" + result);
            ret.setErrorMsg(this.getSDKId()	+ "Login Error.");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
		}
        return ret.fail();
    }

}
