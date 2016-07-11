package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.hutong.supersdk.sdk.modeltools.xiaomi.XiaoMiSDKInfo;
import com.hutong.supersdk.sdk.utils.Encrypt;

@Component("andXiaoMiSDK")
public class AndXiaoMiSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public static final String SDK_ID = "AndXiaoMi";
	
	private static final Log logger = LogFactory.getLog(AndXiaoMiSDK.class); 
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return XiaoMiSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		Map<String, String> result = new HashMap<String, String>();
		try {
			XiaoMiSDKInfo configInfo = (XiaoMiSDKInfo) config;
			String platformOrderId = paramMap.get("orderId");
			String orderId = paramMap.get("cpOrderId");
			
			String appSecret = configInfo.getAppSecretKey();
			if (!checkMiSig(paramMap, appSecret)) {
				result.put("errcode", "1525");
				result.put("errMsg", "sign error!");
				return result;
			}
			String orderStatus = paramMap.get("orderStatus");
			if (!orderStatus.equals("TRADE_SUCCESS")) {
				result.put("errcode", "200");
				result.put("errMsg", "success");
				return result;
			}
			float amount = Float.parseFloat(paramMap.get("payFee")) * 1.0f / 100f;
			String currencyType = "RMB";
			String payType = "";
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, amount, currencyType, payType, ParseJson.encodeJson(paramMap));
			if(iResult){
				result.put("errcode", "200");
				result.put("errMsg", "success");
				return result;
			}else{
				result.put("errcode", "1506");
				result.put("errMsg", "fail,no this order");
				return result;
			}
		} catch (Exception e) {
			logger.error("",e);
			result.put("errcode", "1506");
			result.put("errMsg", "Internal Error.");
			return result;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			XiaoMiSDKInfo configInfo = (XiaoMiSDKInfo) config;
			String miAppId = configInfo.getAppId();
			String miAppSecret = configInfo.getAppSecretKey();
			String token = input.getSDKAccessToken();
			String platformUserId = input.getSdkUid();
			String url = configInfo.getRequestUrl();
			String checkStr = "appId=" + miAppId + "&session=" + token + "&uid=" + platformUserId;
			String signature = Encrypt.HmacSHA1Encrypt(checkStr, miAppSecret);
			url += "?" + checkStr + "&signature=" + signature;
			String response = HttpUtil.get(url);
			logger.debug(response);
            Map resultMap = ParseJson.getJsonContentByStr(response, Map.class);
            if(null != resultMap && 200 == Integer.parseInt(String.valueOf(resultMap.get("errcode")))){
                ret.setSdkUid(platformUserId);
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            ret.setErrorMsg(this.getSDKId() + "Login Error,result:" + response);
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
		}
		return ret.fail();
	}
	
	private boolean checkMiSig(Map<String, String> requestMap, String secretkey) throws Exception {
		String sign = requestMap.get("signature");

        Set<String> params = requestMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String paramKey : sortedParams) {
			if (paramKey.equals("signature") || paramKey.equals("PlatformId")) {
				continue;
			}
			String code = requestMap.get(paramKey);
			String value = URLDecoder.decode(code, "UTF-8");
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(paramKey).append('=').append(value);
        }
		String paramStr = sb.toString();
		String mySign = Encrypt.HmacSHA1Encrypt(paramStr, secretkey);

        return StringUtil.equalsStringIgnoreCase(mySign, sign);
    }
}
