package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.anzhi.AnZhiObj;
import com.hutong.supersdk.sdk.modeltools.anzhi.AnZhiSDKInfo;
import com.hutong.supersdk.sdk.utils.Base64;
import com.hutong.supersdk.sdk.utils.Des3Util;

@Component("andAnZhiSDK")
public class AndAnZhiSDK implements IPayCallBackSDK, IVerifyUserSDK, ICheckOrderSDK {

	public static final String SDK_ID = "AndAnZhi";

	private static final Log logger = LogFactory.getLog(AndAnZhiSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return AnZhiSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		AnZhiSDKInfo configInfo = (AnZhiSDKInfo) config;
		SDKVerifyRet ret = new SDKVerifyRet();
		String platformUserId = input.getSdkUid();
		String token = input.getSDKAccessToken();

		String url = configInfo.getRequestUrl();
		String appKey = configInfo.getAppKey();
		String appSecret = configInfo.getAppSecret();

		Map<String, String> paramMap = new HashMap<String, String>();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		paramMap.put("time", dateFormat.format(new Date()));
		paramMap.put("appkey", appKey);
		paramMap.put("sid", token);
		String sign = Base64.encode((appKey + token + appSecret).getBytes());
		paramMap.put("sign", sign);
		try {
			String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
			result = result.replaceAll("'", "\"");
			@SuppressWarnings("unchecked")
			Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap == null || !"1".equals(String.valueOf(resultMap.get("sc")))) {
				logger.error(this.getSDKId() + "Login Error.result:" + result);
				ret.setErrorMsg(this.getSDKId() + "Login Error.");
				return ret.fail();
			}
		} catch (Exception e) {
			ret.fail();
			ret.setErrorMsg(e.getMessage());
		}
		ret.setSdkUid(platformUserId);
		ret.setSdkAccessToken(token);
		ret.setSdkRefreshToken("");
		ret.success();
		return ret;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		AnZhiSDKInfo configInfo = (AnZhiSDKInfo) config;
		String success = "success";
		String failure = "failure";
		try {
			logger.debug(paramMap.toString());

			String key = configInfo.getAppSecret();
			AnZhiObj anZhiObj = getAnZhiObj(paramMap, key);

			String platformId = anZhiObj.getOrderId();
			if (platformId == null || platformId.equals("")) {
				platformId = "anZhi order is empty";
			}

			if (anZhiObj.getCode() != 1) {
				return failure;
			}
			float sdkRedBagMoney = 0f;
			if(!StringUtils.isEmpty(anZhiObj.getRedBagMoney()))
				sdkRedBagMoney = Float.parseFloat(anZhiObj.getRedBagMoney()) / 100f;
			float sdkOrderAmount = Float.parseFloat(anZhiObj.getOrderAmount()) / 100f;
			String orderId = anZhiObj.getCpInfo();
			String currencyType = "RMB";
			String payType = "";
			boolean iResult = callback.succeedPayment(orderId, platformId, sdkOrderAmount + sdkRedBagMoney, currencyType, payType, ParseJson.encodeJson(paramMap));
			if (iResult) {
				return success;
			} else {
				return failure;
			}
		} catch (Exception e) {
			logger.error("",e);
			return failure;
		}
	}

	private AnZhiObj getAnZhiObj(Map<String, String> requestMap, String appSecret) {

		String data = requestMap.get("data");

		data = Des3Util.decrypt(data, appSecret);

		AnZhiObj anZhiObj;
		try {
			anZhiObj = ParseJson.getJsonContentByStr(data, AnZhiObj.class);
		} catch (Exception e) {
			logger.error("", e);
			anZhiObj = new AnZhiObj();
			anZhiObj.setCode(-1);
		}

		return anZhiObj;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
		logger.debug(this.getSDKId() + " checkOrder.");
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		AnZhiSDKInfo configInfo = (AnZhiSDKInfo) config;
		try {
			//设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String time = df.format(new Date());
			String appkey = configInfo.getAppKey();
			String type = "0";
			String mintradetime = "";
			String maxtradetime = "";
			String appsecret = configInfo.getAppSecret();
			String sdk_order_id = jsonData.getExtraKey("sdk_order_id");
			//appkey+tradenum+mintradetime+maxtradetime+appsecret
			String sign = Base64.encode((appkey+sdk_order_id+mintradetime+maxtradetime+appsecret).getBytes());
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("time", time);
			paramMap.put("appkey", appkey);
			paramMap.put("type", type);
			paramMap.put("tradenum", sdk_order_id); 
			paramMap.put("mintradetime", mintradetime);
			paramMap.put("maxtradetime", maxtradetime);
			paramMap.put("sign", sign);
			
			String url = configInfo.getCheckOrderUrl();
			
			String result = HttpUtil.postForm(url, paramMap);
			result = result.replaceAll("'", "\"");
			Map<String, String> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			//st为1表示成功
			if(resultMap != null && "1".equals(resultMap.get("sc"))){
				String msg = resultMap.get("msg");
				byte[] msgByte = Base64.decode(msg);
				String msgStr = new String(msgByte);
				msgStr = msgStr.replaceAll("'", "\"");
				msgStr = msgStr.substring(1, msgStr.length()-1);
				Map<String, Object> msgMap = ParseJson.getJsonContentByStr(msgStr, Map.class);
				if(msgMap != null && "1".equals(String.valueOf(msgMap.get("tradestatus")))){
					ret.success(sdk_order_id, pOrder.getOrderAmount(), "RMB", "", ParseJson.encodeJson(pOrder));
					return ret;
				}
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
