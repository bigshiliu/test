package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.baidu.BaiduContent;
import com.hutong.supersdk.sdk.modeltools.baidu.BaiduResult;
import com.hutong.supersdk.sdk.modeltools.baidu.BaiduSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.baidu.BaiduBase64Util;

@Component("andBaiduSDK")
public class AndBaiduSDK implements IPayCallBackSDK, IVerifyUserSDK, ICheckOrderSDK{
	
	public static final String SDK_ID = "AndBaiDu";
	
	private static final String VERSION_360 = "3.6.0";
	
	private static final Log logger = LogFactory.getLog(AndBaiduSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return BaiduSDKInfo.class;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		String sdk_version = input.getExtraKey("SDK_VERSION");
		if(VERSION_360.compareTo(sdk_version) < 0){
			return verifyUser360(input, config);
		}else{
			SDKVerifyRet ret = new SDKVerifyRet();
	        try {
	            BaiduSDKInfo configInfo = (BaiduSDKInfo) config;

	            String appId = configInfo.getAppId();
	            String secretKey = configInfo.getSecretKey();
	            String token = input.getSDKAccessToken();

	            Map<String, String> paramMap = new HashMap<String, String>();
	            paramMap.put("AppID", appId);
	            paramMap.put("AccessToken", token);

	            String sign = MD5Util.MD5(appId + token + secretKey).toLowerCase();
	            paramMap.put("Sign", sign);
	            String url = configInfo.getRequestUrl();

	            String result = HttpUtil.postForm(url, paramMap);
				logger.debug(result);

	            Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				if(resultMap == null || resultMap.get("ResultCode") == null || ((Integer) resultMap.get("ResultCode")) != 1){
					logger.error(this.getSDKId() + "Login Error" );
					ret.setErrorMsg(this.getSDKId() + "Login_Error.");
					return ret.fail();
				}

				Integer resultCode = (Integer) resultMap.get("ResultCode");

				String resultSign = (String) resultMap.get("Sign");
				String encodeCon = (String) resultMap.get("Content");
				String content = BaiduBase64Util.decode(encodeCon);
				logger.debug(content);
				String checkSign = MD5Util.MD5(appId + resultCode + encodeCon + secretKey);
				if (!checkSign.equalsIgnoreCase(resultSign)) {
					logger.error(this.getSDKId() + "Login_Error. resultSign=" + resultSign + " checkSign=" + checkSign);
					ret.setErrorMsg(this.getSDKId() + "Login_Error.");
					return ret.fail();
				}
				BaiduContent bdContent = ParseJson.getJsonContentByStr(content, BaiduContent.class);
	            if (bdContent == null) {
	                ret.setErrorMsg("Baidu Response Error.");
	                return ret.fail();
	            }

				ret.setSdkUid(bdContent.getUID().toString());
				ret.setSdkAccessToken(token);
				ret.setSdkRefreshToken("");
				ret.success();
			} catch (Exception e) {
				logger.error(this.getSDKId() + "Login Error.:" + e.getMessage());
				ret.setErrorMsg(this.getSDKId() + "Login_Error.");
				ret.fail();
			}
	        return ret;
		}
	}

	@SuppressWarnings("unchecked")
	private SDKVerifyRet verifyUser360(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            BaiduSDKInfo configInfo = (BaiduSDKInfo) config;

            String appId = configInfo.getAppId();
            String secretKey = configInfo.getSecretKey();
            String token = input.getSDKAccessToken();

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("AppID", appId);
            paramMap.put("AccessToken", token);

            String sign = MD5Util.MD5(appId + token + secretKey).toLowerCase();
            paramMap.put("Sign", sign);
            String url = configInfo.getRequestUrl360();

            String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);

            Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || resultMap.get("ResultCode") == null || ((Integer) resultMap.get("ResultCode")) != 1){
				logger.error(this.getSDKId() + "Login Error" );
				ret.setErrorMsg(this.getSDKId() + "Login_Error.");
				return ret.fail();
			}

			Integer resultCode = (Integer) resultMap.get("ResultCode");

			String resultSign = (String) resultMap.get("Sign");
			String encodeCon = (String) resultMap.get("Content");
			String content = BaiduBase64Util.decode(encodeCon);
			logger.debug(content);
			String checkSign = MD5Util.MD5(appId + resultCode + encodeCon + secretKey);
			if (!checkSign.equalsIgnoreCase(resultSign)) {
				logger.error(this.getSDKId() + "Login_Error. resultSign=" + resultSign + " checkSign=" + checkSign);
				ret.setErrorMsg(this.getSDKId() + "Login_Error.");
				return ret.fail();
			}
			BaiduContent bdContent = ParseJson.getJsonContentByStr(content, BaiduContent.class);
            if (bdContent == null) {
                ret.setErrorMsg("Baidu Response Error.");
                return ret.fail();
            }

			ret.setSdkUid(bdContent.getUID().toString());
			ret.setSdkAccessToken(token);
			ret.setSdkRefreshToken("");
			ret.success();
		} catch (Exception e) {
			logger.error(this.getSDKId() + "Login Error.:" + e.getMessage());
			ret.setErrorMsg(this.getSDKId() + "Login_Error.");
			ret.fail();
		}
        return ret;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		BaiduSDKInfo configInfo = (BaiduSDKInfo) config;
		String appId = configInfo.getAppId();
		String secretKey = configInfo.getSecretKey();
		
		BaiduResult ret = new BaiduResult(Integer.parseInt(appId));
		try {
			logger.debug(paramMap.toString());
			
			String orderId = paramMap.get("CooperatorOrderSerial");
			String encodeCon = paramMap.get("Content");
			String sdkOrderId = paramMap.get("OrderSerial");
			String currencyType = "RMB";
			String payType = "";
			String sign = paramMap.get("Sign");
			String checkSign = MD5Util.MD5(appId + sdkOrderId + orderId + encodeCon + secretKey);
			if(!checkSign.equalsIgnoreCase(sign)){
				logger.debug("sign=" + sign + " checkSign=" + checkSign);
				ret.setResultCode(0);
				ret.setResultMsg("Sign Check Failed.");
				return ret;
			}
			String content = BaiduBase64Util.decode(encodeCon);
			logger.debug(content);
			BaiduContent bdContent = ParseJson.getJsonContentByStr(content, BaiduContent.class);
			if (bdContent == null || bdContent.getOrderStatus() != 1) {
                logger.info(bdContent);
                ret.setResultCode(0);
				return ret;
			}
			boolean iResult = callback.succeedPayment(orderId, sdkOrderId, bdContent.getOrderMoney(), currencyType, payType, ParseJson.encodeJson(paramMap));
			
			if(iResult){
				ret.setResultCode(1);
				ret.setResultMsg("success");
				ret.setSign(MD5Util.MD5(appId + "1" + secretKey));
			}else{
				ret.setResultCode(0);
				ret.setResultMsg("Internal Error.");
				ret.setSign(MD5Util.MD5(appId + "0" + secretKey));
			}
			return ret;
		} catch (Exception e) {
			logger.error("",e);
			ret.setResultCode(0);
			ret.setResultMsg("Internal Error.");
			ret.setSign(MD5Util.MD5(appId + "0" + secretKey));
			return ret;
		}
	}

	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
		logger.debug(this.getSDKId() + " checkOrder. start!");
		String sdk_version = jsonData.getExtraKey("SDK_VERSION");
		if(VERSION_360.compareTo(sdk_version) < 0){
			return checkOrder360(pOrder, jsonData, config);
		}else{
			SDKCheckOrderRet ret = new SDKCheckOrderRet();
			BaiduSDKInfo configInfo = (BaiduSDKInfo) config;
			try {
				String url = configInfo.getCheckOrderUrl();
				String AppID = configInfo.getAppId();
				String CooperatorOrderSerial = pOrder.getOrderId();
				String OrderType = "1";
				String Action = "10002";
				String SecretKey = configInfo.getSecretKey();
				String signStr = AppID + CooperatorOrderSerial + SecretKey;
				String Sign = MD5Util.MD5(signStr);
				
				Map<String, String> paramMap = new  HashMap<String, String>();
				paramMap.put("AppID", AppID);
				paramMap.put("CooperatorOrderSerial", CooperatorOrderSerial);
				paramMap.put("OrderType", OrderType);
				paramMap.put("Sign", Sign);
				paramMap.put("Action", Action);
				
				String result = HttpUtil.postForm(url, paramMap);
				
				@SuppressWarnings("unchecked")
				Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				//订单状态, 1表示成功
				if(resultMap != null && 1 == Integer.parseInt(String.valueOf(resultMap.get("ResultCode")))){
					String resContent = URLDecoder.decode(String.valueOf(resultMap.get("Content")), "UTF-8");
					String base64EndContent = StringUtil.decodeBase64(resContent);
					if(StringUtils.isEmpty(base64EndContent))
						return ret.fail();
					@SuppressWarnings("unchecked")
					Map<String, Object> contentMap = ParseJson.getJsonContentByStr(base64EndContent, Map.class);
					if(StringUtils.isEmpty(contentMap.get("OrderStatus")) && !"1".equals(String.valueOf(contentMap.get("OrderStatus"))))
						return ret.fail();
					if(StringUtils.isEmpty(contentMap.get("OrderSerial")))
						return ret.fail();
					if(StringUtils.isEmpty(contentMap.get("OrderMoney")))
						return ret.fail();
					//查询成功,解析成功,延签成功
					ret.success(String.valueOf(contentMap.get("OrderSerial")), Double.parseDouble(String.valueOf(contentMap.get("OrderMoney"))), "RMB", "", ParseJson.encodeJson(contentMap));
					return ret;
				}
				ret.fail();
				return ret;
			} catch (Exception e) {
				logger.error("", e);
				ret.fail();
				return ret;
			}
			
		}
	}

	private SDKCheckOrderRet checkOrder360(PaymentOrder pOrder, JsonReqObj jsonData, Object config) {
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		BaiduSDKInfo configInfo = (BaiduSDKInfo) config;
		try {
			String url = configInfo.getCheckOrderUrl360();
			String AppID = configInfo.getAppId();
			String CooperatorOrderSerial = pOrder.getOrderId();
			String OrderType = "1";
			String Action = "10002";
			String SecretKey = configInfo.getSecretKey();
			String signStr = AppID + CooperatorOrderSerial + SecretKey;
			String Sign = MD5Util.MD5(signStr);
			
			Map<String, String> paramMap = new  HashMap<String, String>();
			paramMap.put("AppID", AppID);
			paramMap.put("CooperatorOrderSerial", CooperatorOrderSerial);
			paramMap.put("OrderType", OrderType);
			paramMap.put("Sign", Sign);
			paramMap.put("Action", Action);
			
			String result = HttpUtil.postForm(url, paramMap);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			//订单状态, 1表示成功
			if(resultMap != null && 1 == Integer.parseInt(String.valueOf(resultMap.get("ResultCode")))){
				String resContent = URLDecoder.decode(String.valueOf(resultMap.get("Content")), "UTF-8");
				String base64EndContent = StringUtil.decodeBase64(resContent);
				if(StringUtils.isEmpty(base64EndContent))
					return ret.fail();
				@SuppressWarnings("unchecked")
				Map<String, Object> contentMap = ParseJson.getJsonContentByStr(base64EndContent, Map.class);
				if(StringUtils.isEmpty(contentMap.get("OrderStatus")) && !"1".equals(String.valueOf(contentMap.get("OrderStatus"))))
					return ret.fail();
				if(StringUtils.isEmpty(contentMap.get("OrderSerial")))
					return ret.fail();
				if(StringUtils.isEmpty(contentMap.get("OrderMoney")))
					return ret.fail();
				//查询成功,解析成功,延签成功
				ret.success(String.valueOf(contentMap.get("OrderSerial")), Double.parseDouble(String.valueOf(contentMap.get("OrderMoney"))), "RMB", "", ParseJson.encodeJson(contentMap));
				return ret;
			}
			ret.fail();
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			return ret;
		}
	}
	
}
