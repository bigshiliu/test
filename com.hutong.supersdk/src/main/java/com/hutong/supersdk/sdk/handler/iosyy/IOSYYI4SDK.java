package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.i4.I4SDKInfo;
import com.hutong.supersdk.sdk.utils.i4.IOSI4PayCore;

@Component("iOSYYI4SDK")
public class IOSYYI4SDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYI4Tools";
	
	private final static Log logger = LogFactory.getLog(IOSYYI4SDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return I4SDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String success = "success";
		String fail = "fail";
		try {
			I4SDKInfo configInfo = (I4SDKInfo) config;

			String pubKey = configInfo.getPubKey();
			if(!verifySignature(paramMap, pubKey)){
				logger.error(this.getSDKId() + " check Sign failed.");
				return fail;
			}
			if(!"0".equals(paramMap.get("status"))){
				return fail;
			}
			String platformOrderId = paramMap.get("order_id");
			String orderId = paramMap.get("billno");
			double amount = Double.parseDouble(paramMap.get("amount"));
			
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, amount, "RMB", "", ParseJson.encodeJson(paramMap));
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
			I4SDKInfo configInfo = (I4SDKInfo) config;

			String token = input.getSDKAccessToken();
			String sdkUid = input.getSdkUid();

			String url = configInfo.getRequestUrl();
			String response = HttpUtil.get(url + "?token=" + token);
			logger.debug(response);
			Map resultMap = ParseJson.getJsonContentByStr(response, Map.class);
			if(resultMap == null || !"0".equals(String.valueOf(resultMap.get("status")))) {
				ret.setErrorMsg(this.getSDKId() + " VerifyUser Error!");
				return ret.fail();
			}
			sdkUid = StringUtils.isEmpty(resultMap.get("userid")) ? sdkUid : String.valueOf(resultMap.get("userid"));

			String username = StringUtils.isEmpty(resultMap.get("username")) ?  "" : String.valueOf(resultMap.get("username"));

			ret.setSdkUid(sdkUid);
			ret.setExtra("username", username);
			ret.setSdkAccessToken(token);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
		}
        return ret.fail();

	}
	
	/**
     * 异步通知消息验证
     * @param para 异步通知消息
     * @return 验证结果
     */
    private static boolean verifySignature(Map<String, String> para, String pubKey) {
        try {
			String respSignature = para.get("sign");
			// 除去数组中的空值和签名参数
			Map<String, String> filteredReq = IOSI4PayCore.paraFilter(para);
			Map<String, String> signature = IOSI4PayCore.parseSignature(respSignature, pubKey);
			for (String key : filteredReq.keySet()) {
			    String value = filteredReq.get(key);
			    String signValue = signature.get(key);
			    if (!value.equals(signValue)) {
			    	return false;
			    }
			}
		} catch (Exception e) {
			logger.error("",e);
			return false;
		}
        return true;
    }
}
