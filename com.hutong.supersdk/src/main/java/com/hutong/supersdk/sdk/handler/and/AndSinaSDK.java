package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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
import com.hutong.supersdk.sdk.modeltools.sina.SinaSDKInfo;
import com.hutong.supersdk.sdk.utils.Encrypt;
import com.hutong.supersdk.sdk.utils.MD5Util;
import org.springframework.util.StringUtils;

@Component("andSinaSDK")
public class AndSinaSDK implements IVerifyUserSDK, IPayCallBackSDK{
	
	public static final String SDK_ID = "AndSina";
	
	private static Log logger = LogFactory.getLog(AndSinaSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return SinaSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		try {
			SinaSDKInfo configInfo = (SinaSDKInfo) config;

			String platformOrderId = paramMap.get("order_id");
            platformOrderId = (StringUtils.isEmpty(platformOrderId)) ? "sina order id is empty" : platformOrderId;

			String appSecret = configInfo.getAppSecret();
			if (!checkSinaSig(paramMap, appSecret)) {
				logger.error("sign is error!");
				return "FAILURE";
			}

			String orderId = paramMap.get("pt");
			double CostMoney = Double.parseDouble(paramMap.get("actual_amount")) / 100d;
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, CostMoney, "RMB", "", ParseJson.encodeJson(paramMap));
			if (iResult)
				return "OK";
		} catch (Exception ex) {
			logger.error("", ex);
		}
        return "FAILURE";
    }

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            SinaSDKInfo configInfo = (SinaSDKInfo) config;
            String platformUserId = input.getSdkUid();
            String token = input.getSDKAccessToken();
            String deviceId = input.getExtraKey("deviceID");
            String signatureKey = configInfo.getSignaturekey();
            String appKey = configInfo.getAppKey();
            String url = configInfo.getRequestUrl();
            SortedMap<String, String> paramMap = new TreeMap<String, String>();
            paramMap.put("suid", platformUserId);
            paramMap.put("appkey", appKey);
            paramMap.put("deviceid", deviceId);
            paramMap.put("token", token);
            String signature = buildVerifyUserSign(paramMap, signatureKey);
            paramMap.put("signature", signature.toLowerCase());
            String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
            Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !token.equals(resultMap.get("token"))){
				logger.error(this.getSDKId() + " Login Error result: " + result);
				ret.setErrorMsg(this.getSDKId() + " Login Error.");
				return ret.fail();
			}
            ret.setSdkUid(String.valueOf(resultMap.get("suid")));
            ret.setSdkAccessToken(token);
            return ret.success();
        } catch (Exception e) {
            logger.debug("", e);
        }
        ret.setErrorMsg(this.getSDKId() + " Login Error ");
        return ret.fail();
    }
	
	private String buildVerifyUserSign(SortedMap<String, String> paramMap, String appKey){
		StringBuilder sb = new StringBuilder();
		for (Iterator<Entry<String, String>> it = paramMap.entrySet().iterator(); it.hasNext();) {
			Entry<String, String> kv = it.next();
			String k = kv.getKey();
			String v = kv.getValue();
			if (k.equals("signature")) {
				continue;
			}
			sb.append(k);
			sb.append('=');
			sb.append(v);
			if (it.hasNext()) {
				sb.append('&');
			}
		}
		sb.append('|');
		sb.append(appKey);
		String concat = sb.toString();
        return MD5Util.MD5(concat);
	}
	
	private boolean checkSinaSig(Map<String, String> requestMap, String appSecret)
            throws UnsupportedEncodingException {
		String sign = requestMap.get("signature");

		Set<String> params = requestMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String paramKey : sortedParams) {
			if (paramKey.equals("signature")) {
				continue;
			}
			String value = requestMap.get(paramKey);
			if (value != null) {
				if (sb.length() > 0) {
					sb.append('|');
				}
				sb.append(paramKey).append('|').append(value);
			}
		}
		sb.append('|').append(appSecret);
		String mySign = Encrypt.getSha1(sb.toString());

		return StringUtil.equalsStringIgnoreCase(mySign, sign);
	}
}
