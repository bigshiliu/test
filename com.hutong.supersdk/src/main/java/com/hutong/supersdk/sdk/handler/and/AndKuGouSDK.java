package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

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
import com.hutong.supersdk.sdk.modeltools.kugou.KuGouSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andKuGouSDK")
public class AndKuGouSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndKuGou";

	private final static Log logger = LogFactory.getLog(AndKuGouSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return KuGouSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.info(this.getSDKId() + " vierfyUser start !!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		KuGouSDKInfo configInfo = (KuGouSDKInfo) config;
		String token = input.getSDKAccessToken();
		String requestUrl = configInfo.getRequestUrl() + "&token=" + token;
		String uid = input.getSdkUid();
		try {
			String result = HttpUtil.get(requestUrl);
			@SuppressWarnings("unchecked")
            Map<String, Map<String,String>> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap != null) {
				Map<String, String> responseRes = resultMap.get("response");
				if (responseRes != null && "0".equals(responseRes.get("code"))) {
					ret.setSdkUid(uid);
					ret.setSdkAccessToken(token);
					return ret.success();
				} else {
					logger.error(this.getSDKId() + " verifyUser Error. ErrorCode:" + (responseRes != null ? responseRes.get("code") : null) + " ErrorMsg:"
							+ String.valueOf(responseRes != null ? responseRes.get("message_cn") : null));
					ret.setErrorMsg(String.valueOf(responseRes != null ? responseRes.get("prompt") : null));
					return ret.fail();
				}
			}
			else {
				ret.setErrorMsg("result=" + result);
				return ret.fail();
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error.");
			return ret.fail();
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String success = "SUCCESS";
		String fail = "FAIL";
		try {
			KuGouSDKInfo configInfo = (KuGouSDKInfo) config;
			String status = String.valueOf(paramMap.get("status"));
			if (!"1".equals(status)) {
				logger.debug(this.getSDKId() + " payCallBack Fail! status:" + status);
				return fail;
			}
			String orderid = String.valueOf(paramMap.get("orderid"));
			String outorderid = paramMap.get("outorderid");
			String amount = paramMap.get("amount");
			String username = paramMap.get("username");
			String time = String.valueOf(paramMap.get("time"));
			String ext1 = String.valueOf(paramMap.get("ext1"));
			String ext2 = String.valueOf(paramMap.get("ext2"));
			String key = configInfo.getKey();
			String sign = paramMap.get("sign");
			String mySign = MD5Util.MD5(orderid + outorderid + amount + username + status + time + ext1 + ext2 + key);
			if(sign.equalsIgnoreCase(mySign)){
				boolean iResult = callback.succeedPayment(outorderid, orderid, Double.parseDouble(amount),
                        "RMB", "", ParseJson.encodeJson(paramMap));
				if(iResult)
					return success;
			}
			return fail;
		} catch (Exception e) {
			logger.error("", e);
			return fail;
		}
	}

}
