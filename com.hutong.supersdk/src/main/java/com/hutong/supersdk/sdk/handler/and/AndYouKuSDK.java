package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.youku.YouKuSDKInfo;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.util.PropertiesUtil;

@Component("andYouKuSDK")
public class AndYouKuSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndYouKu";

	private static final Log logger = LogFactory.getLog(AndYouKuSDK.class);

	// superSDKConfig.properties名称
	private final static String SUPERSDK_CONFIG_NAME = "superSDKConfig";

	@Autowired
	private PaymentOrderDao paymentOrderDao;

	@Autowired
	private AppConfigDao appConfigDao;

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YouKuSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + "vierfyUser start !!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			YouKuSDKInfo configInfo = (YouKuSDKInfo) config;
			String token = input.getSDKAccessToken();
			String appKey = configInfo.getAppKey();
			String payKey = configInfo.getPayKey();
			String msg = "appkey=" + appKey + "&sessionid=" + token;
			Map<String, String> reqMap = new HashMap<String, String>();
			String sign = getHmacSha1(msg, payKey);
			reqMap.put("sessionid", token);
			reqMap.put("appkey", appKey);
			reqMap.put("sign", sign);
			String url = configInfo.getRequestUrl();
			String result = HttpUtil.postForm(url, reqMap);
			logger.debug("SendUrl=" + url + " params=" + reqMap.toString());
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && StringUtil.equalsStringIgnoreCase(String.valueOf(resultMap.get("status")), "success")) {
				ret.setSdkUid(String.valueOf(resultMap.get("uid")));
				ret.setSdkAccessToken(token);
				return ret.success();
			}
			logger.debug("verifyUser failed! result=" + result);
			ret.setErrorMsg(this.getSDKId() + " Login Error.");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " Login Error.");
		}
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack start!!! paramMap:" + paramMap.toString());
		Map<String, String> retMap = new HashMap<String, String>();
		try {
			YouKuSDKInfo configInfo = (YouKuSDKInfo) config;
			String payKey = configInfo.getPayKey();
			String apporderID = String.valueOf(paramMap.get("apporderID"));
			String uid = String.valueOf(paramMap.get("uid"));
			String price = String.valueOf(paramMap.get("price"));
			String result = String.valueOf(paramMap.get("result"));
			String sign = String.valueOf(paramMap.get("sign"));
			String passthrough = String.valueOf(paramMap.get("passthrough"));
			String callBackUrl = getWebPayBackUrl(passthrough);
			String msg = callBackUrl + "?apporderID=" + apporderID + "&price=" + price + "&uid=" + uid;
			String mySign = this.getHmacSha1(msg, payKey);
			if (!StringUtil.equalsStringIgnoreCase(mySign, sign)) {
				logger.error("Sign Check Failed. params=" + paramMap.toString() + " sign=" + sign + " mySign=" + mySign);
				retMap.put("status", "failed");
				retMap.put("desc", "Sign Check Failed");
				return retMap;
			}
			if (paramMap.containsKey("result") && !result.equals("1") && !result.equals("2")) {
				retMap.put("status", "success");
				retMap.put("desc", "Order Status is not 1 or 2.");
				return retMap;
			}
			double amount;
			if (paramMap.containsKey("result")) {
				amount = 1.0 * Integer.parseInt(paramMap.get("success_amount")) / 100;
			}
			else {
				amount = 1.0 * Integer.parseInt(price) / 100;
			}
			boolean iResult = callback.succeedPayment(passthrough, apporderID, amount, "RMB", "", ParseJson.encodeJson(paramMap));
			if (iResult) {
				retMap.put("status", "success");
				retMap.put("desc", "");
				return retMap;
			}
			else {
				retMap.put("status", "failed");
				retMap.put("desc", "Order Status Error.");
				return retMap;
			}
		} catch (Exception e) {
			logger.error("Order Check Failed", e);
			retMap.put("status", "failed");
			retMap.put("desc", "Other Error.");
			return retMap;
		}
	}
	
	private String getHmacSha1(String msg, String key)
			throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(),"HmacMD5");
		
		Mac mac = Mac.getInstance("HmacMD5");
		mac.init(keySpec);
		byte[] digMsg = mac.doFinal(msg.getBytes());
		StringBuilder signBuf = new StringBuilder();
		for(byte b : digMsg) {
			if (Integer.toHexString(0xFF & b).length() == 1)
				signBuf.append("0").append(Integer.toHexString(0xFF & b));
			else
				signBuf.append(Integer.toHexString(0xFF & b));
		}
		return signBuf.toString();
	}

	/**
	 * 根据SuperSDK订单号获取支付回调地址
	 * @param orderId orderId
	 * @return 支付回调地址(包含域名)
	 */
	private String getWebPayBackUrl(String orderId){
		logger.debug("PathUtil getPayBackUrl. orderId:" + orderId);
		//保存当前AppId
		String localAppId = ThreadHelper.getAppId();
		//根据订单号获取渠道ID
		PaymentOrder paymentOrder = paymentOrderDao.queryByOrderId(orderId);
		if(null == paymentOrder)
			return "";
		String channelId = paymentOrder.getAppChannelId();
		if(StringUtils.isEmpty(channelId))
			return "";
		ThreadHelper.setAppId(null);
		//根据AppId获取shortId
		AppConfig appConfig = appConfigDao.findById(localAppId);
		if(null == appConfig)
			return "";
		String shortId = appConfig.getShortAppId();
		if(StringUtils.isEmpty(shortId))
			return "";
		//获取配置文件中回调地址
		String payBackUrl = PropertiesUtil.findValueByKey(SUPERSDK_CONFIG_NAME, "superSDK_url");
		//操作完毕,将数据源切换到原始状态
		ThreadHelper.setAppId(localAppId);
		logger.debug("PathUtil getPayBackUrl. payBackUrl:" + payBackUrl +"supersdk-web/payback/" + shortId + "/" + channelId);
		return payBackUrl +"supersdk-web/payback/" + shortId + "/" + channelId;
	}
}
