package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.aiyouxi.AiYouXiSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.aiyouxi.RequestParasUtil;

@Component("andAiYouXiSDK")
public class AndAiYouXiSDK implements IPayCallBackSDK, IVerifyUserSDK {

	public static final String SDK_ID = "AndAiYouXi";

	private final static Log logger = LogFactory.getLog(AndAiYouXiSDK.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return AiYouXiSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		AiYouXiSDKInfo configInfo = (AiYouXiSDKInfo) config;
		String code = input.getSDKAccessToken();
		String content;
		String url = configInfo.getRequestUrl();
		String clientId = configInfo.getClientId();
		String clientSecret = configInfo.getClientSecret();
		Map<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "authorization_code");
		params.put("code", code);
		params.put("client_secret", clientSecret);

		logger.info("url=" + url);
		logger.info("params=" + params.toString());

		try {
			RequestParasUtil.signature("2", clientId, clientSecret, "MD5", "v1.0", params);
			content = RequestParasUtil.sendPostRequest(url, params);
			@SuppressWarnings("unchecked")
			Map<String, String> result = ParseJson.getJsonContentByStr(content, Map.class);

			if (result == null) {
				logger.error(this.getSDKId() + " Login failed ! content : " + content);
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " Login failed ! content : ");
				return ret;
			}

			Object userId = result.get("user_id");
			if (StringUtils.isEmpty(userId)) {
				logger.error(this.getSDKId() + " Login failed ! content : " + content);
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " Login failed ! content : ");
				return ret;
			}
			ret.setSdkUid(userId.toString());
			ret.setSdkAccessToken(result.get("access_token"));
			ret.setSdkRefreshToken(result.get("refresh_token"));
			ret.success();
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
			return ret;
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		StringBuilder retStr = new StringBuilder();
		AiYouXiSDKInfo configInfo = (AiYouXiSDKInfo) config;
		try {
			String mth = paramMap.get("method");
			if ("check".equals(mth)) {
				String orderId = paramMap.get("cp_order_id");
				String correlator = paramMap.get("correlator");
				String order_time = paramMap.get("order_time");
				String appSecret = configInfo.getAppKey();
				String mySign = MD5Util.MD5(orderId + correlator + order_time + mth + appSecret);
				String sign = paramMap.get("sign");
				if(!sign.equalsIgnoreCase(mySign)){
					logger.error("sign error mysign: " + mySign);
					return "sign error";
				}
				PaymentOrder order = callback.queryPayment(orderId);
				String resultCode = "0";
				String cp_order_time = sdf.format(new Date());
				String game_account = "";
				String fee = "";
				if(order == null){
					logger.error("order can not found! cp_order_id: " + orderId);
					resultCode = "99";
				}else{
					game_account = order.getSupersdkUid() + "";
					fee = order.getPayAmount() + "";
				}
				
				retStr.append("<sms_pay_check_resp>");
				retStr.append("<correlator>").append(correlator).append("</correlator>");
				retStr.append("<game_account>").append(game_account).append("</game_account>");//网游游戏账号（重要，作为客服维护凭据）
				retStr.append("<fee>").append(fee).append("</fee>");//支付金额，单位：元
				retStr.append("<if_pay>").append(resultCode).append("</if_pay>");//0 确认扣款 其他 确认不扣款
				retStr.append("<order_time>").append(cp_order_time).append("</order_time>");//订单时间戳，14位时间格式，是CP服务器时间(yyyyMMddHHmmss)
				retStr.append("</sms_pay_check_resp>");
				return retStr.toString();
			} else if("callback".equals(mth)){
				String orderId = paramMap.get("cp_order_id");
				String correlator = paramMap.get("correlator");
				String result_code = paramMap.get("result_code");
				String fee = paramMap.get("fee");
				String pay_type = paramMap.get("pay_type");
				String sign = paramMap.get("sign");
				String appSecret = configInfo.getAppKey();
				String currencyType = "RMB";
				String mySign = MD5Util.MD5(orderId + correlator + result_code + fee + pay_type + mth + appSecret);
				if (!sign.equalsIgnoreCase(mySign)) {
					logger.error("sign error mysign: " + mySign);
					return "sign error ";
				}
				String resultCode = "0";
				if("00".equalsIgnoreCase(result_code)){
					double amount = NumberUtils.toDouble(fee);
					boolean iResult = callback.succeedPayment(orderId, "", amount, currencyType, pay_type, ParseJson.encodeJson(paramMap));
					if(!iResult){
						logger.debug("save Payment failed! orderId: " + orderId);
						resultCode = "99";
					}
				}
				retStr.append("<cp_notify_resp>");
				retStr.append("<h_ret>").append(resultCode).append("</h_ret>");
				retStr.append("<cp_order_id>").append(orderId).append("</cp_order_id>");
				retStr.append("</cp_notify_resp>");
				return retStr.toString();
			}else{
				return "netgid error:" + mth;
			}
		} catch (Exception e) {
			logger.error("payCallBack Error",e);
			return e.toString();
		}
	}

}
