package com.hutong.supersdk.sdk.handler.and;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.sdk.modeltools.opensdk.PaymentResponse;
import com.hutong.supersdk.sdk.modeltools.opensdk.UserResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.opensdk.OpenSDKInfo;
import com.hutong.supersdk.util.RSAUtil;

@Component("andOpenSDK")
public class AndOpenSDK implements IVerifyUserSDK, ICheckOrderSDK {
	private static final String SDK_ID = "AndOpenSDK";

	private static final Log logger = LogFactory.getLog(AndOpenSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return OpenSDKInfo.class;
	}

	@Override
	public SDKCheckOrderRet checkOrder(PaymentOrder pOrder, JsonReqObj jsonData, Object config) {
		logger.debug(this.getSDKId() + " checkOrder.");
		//通过OpenSDK的订单查询接口,查询订单支付状态是否成功 GET请求
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
        if(StringUtils.isEmpty(pOrder.getSdkOrderId()))
            return ret.fail();
        try {
            OpenSDKInfo configInfo = (OpenSDKInfo) config;
            Map<String, Object> plainMap = new HashMap<String, Object>();
			plainMap.put("time", System.currentTimeMillis());
			plainMap.put("grant_app", configInfo.getGrant_app());
			String q = URLEncoder.encode(RSAUtil.encryptByPublicKey(ParseJson.encodeJson(plainMap), configInfo.getPub_key()), "UTF-8");
            String v = URLEncoder.encode(configInfo.getGrant_app(), "UTF-8");
			String response = HttpUtil.get(configInfo.getCheckOrderUrl() + "/" + pOrder.getSdkOrderId() + "?v=" + v + "&q=" + q);
            PaymentResponse result = ParseJson.getJsonContentByStr(response, PaymentResponse.class);
            if (result != null && "ok".equals(result.getStatus()) && "SUCCESS".equals(result.getPayment().getPay_status())) {
                return ret.success(result.getPayment().getId(), Double.parseDouble(result.getPayment().getPay_amount()),
                        StringUtils.isEmpty(result.getPayment().getCurrency()) ? "" : result.getPayment().getCurrency(), "", response);
            }
			return ret.addToCheckQueue().fail();
		} catch (Exception e) {
			logger.error("", e);
			return ret.addToCheckQueue().fail();
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start.");
		SDKVerifyRet ret = new SDKVerifyRet();
		OpenSDKInfo configInfo = (OpenSDKInfo) config;
		try {
			// 密文 使用RSA加密
			Map<String, String> plainMap = new HashMap<String, String>();
			plainMap.put("grant_app", configInfo.getGrant_app());
			plainMap.put("open_id", input.getSdkUid());
			plainMap.put("token", input.getSDKAccessToken());
			plainMap.put("time", String.valueOf(System.currentTimeMillis()));
			String q = URLEncoder.encode(
					RSAUtil.encryptByPublicKey(ParseJson.encodeJson(plainMap), configInfo.getPub_key()), "UTF-8");
			String response = HttpUtil.get(configInfo.getRequestUrl() + "?v=" + configInfo.getGrant_app() + "&q=" + q);
            UserResponse result = ParseJson.getJsonContentByStr(response, UserResponse.class);
			if (result != null && "ok".equals(result.getStatus())) {
                ret.setSdkUid(result.getUser().getOpen_info().getOpen_id());
                ret.setSdkAccessToken(result.getUser().getOpen_info().getToken());
                return ret.success();
			}
			logger.error(this.getSDKId() + " verifyUser Error, result:" + response);
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			return ret.fail();
		}
	}

}
