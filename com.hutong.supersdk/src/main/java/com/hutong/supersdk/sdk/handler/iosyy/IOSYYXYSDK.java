package com.hutong.supersdk.sdk.handler.iosyy;

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
import com.hutong.supersdk.sdk.modeltools.xy.XYSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("iOSYYXYSDK")
public class IOSYYXYSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYXYTools";
	
	private final static Log logger = LogFactory.getLog(IOSYYXYSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return XYSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String success = "success";
		String fail = "fail";
		try {
			XYSDKInfo configInfo = (XYSDKInfo) config;
			String appKey = configInfo.getAppKey();
			String payKey = configInfo.getPayKey();
			
			String orderId = paramMap.get("orderid");
			String uid = paramMap.get("uid");
			String serverId = paramMap.get("serverid");
			String amount = paramMap.get("amount");
			String extra = paramMap.get("extra");
			String ts = paramMap.get("ts");
			String sign = paramMap.get("sign");
			String sig = paramMap.get("sig");
			
			String checkSign = MD5Util.MD5(appKey + "amount=" + amount + "&extra=" + extra + "&orderid=" + orderId 
					+ "&serverid=" + serverId + "&ts=" + ts + "&uid=" + uid);
			String checkSig = MD5Util.MD5(payKey + "amount=" + amount + "&extra=" + extra + "&orderid=" + orderId 
					+ "&serverid=" + serverId + "&ts=" + ts + "&uid=" + uid);
			if(!StringUtil.equalsStringIgnoreCase(sign, checkSign) || !StringUtil.equalsStringIgnoreCase(sig, checkSig)){
				logger.error("Check Sign & Sig Failed. Sign=" + sign + " checkSign=" + checkSign + " Sig=" + sig + " CheckSig=" + checkSig);
				return fail;
			}
			boolean iResult = callback.succeedPayment(extra, orderId, Double.parseDouble(amount), "RMB", "", ParseJson.encodeJson(paramMap));
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
			XYSDKInfo configInfo = (XYSDKInfo) config;
			String uid = input.getSdkUid();
			String token = input.getSDKAccessToken();
			String appId = configInfo.getAppId();

			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("uid", uid);
			reqMap.put("appid", appId);
			reqMap.put("token", token);

			String url = configInfo.getRequestUrl();
			String result = HttpUtil.postForm(url, reqMap);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !"0".equals(String.valueOf(resultMap.get("ret")))) {
				ret.setErrorMsg(this.getSDKId() + " verifyUser Error! ErrorMsg:"
						+ (resultMap != null ? resultMap.get("error") : null));
				return ret.fail();
			}
			ret.setSdkUid(uid);
			ret.setSdkAccessToken(token);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
        }
        ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
        return ret.fail();
    }

}
