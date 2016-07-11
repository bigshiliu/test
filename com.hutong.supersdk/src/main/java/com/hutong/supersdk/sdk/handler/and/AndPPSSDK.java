package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.pps.PPSSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import org.springframework.util.StringUtils;

@Component("andPPSSDK")
public class AndPPSSDK implements IPayCallBackSDK, IVerifyUserSDK {
	
	public static final String SDK_ID = "AndPPS";
	
	private final static Log logger = LogFactory.getLog(AndPPSSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return PPSSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			PPSSDKInfo configInfo = (PPSSDKInfo) config;
			String uid = input.getSdkUid();
			String token = input.getExtraKey("sign");
			String timestamp = input.getExtraKey("time");
			String key = configInfo.getKey();
			String mySign = MD5Util.MD5(uid + "&" + timestamp + "&" + key);
			if (!mySign.equalsIgnoreCase(token)) {
                logger.error(this.getSDKId() + " Login Error. sign=" + token + " mySign=" + mySign);
                ret.setErrorMsg(this.getSDKId() + " Login Error.");
                return ret.fail();
            }
			ret.setSdkUid(uid);
			ret.setSdkAccessToken(token);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
			return ret.fail();
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		ReturnObj retResult = new ReturnObj();
		try {
            PPSSDKInfo configInfo = (PPSSDKInfo) config;
            String platformId = paramMap.get("order_id");
            platformId = StringUtils.isEmpty(platformId) ? "pps order id is empty" : platformId;

            String payKey = configInfo.getPayKey();
            if(!checkPPSSig(paramMap, payKey)){
				retResult.result = -1;
				retResult.message = "sign error";
				return ParseJson.encodeJson(retResult);
			}
			
			String userId = paramMap.get("user_id");
			if(userId == null || userId.equals("")){
				retResult.result = -3;
				retResult.message = "user_id null!";
				return ParseJson.encodeJson(retResult);
			}
			String orderId = paramMap.get("userData");
			String valueString = paramMap.get("money");
			float amount = Float.parseFloat(valueString);
			String currencyType = "RMB";
			String payType = "";
			boolean iResult = callback.succeedPayment(orderId, platformId, amount,
                    currencyType, payType, ParseJson.encodeJson(paramMap));
			if(iResult){
				retResult.result = 0;
				retResult.message = "success";
			}else{
				retResult.result = -6;
				retResult.message = "exception error";
			}
			return ParseJson.encodeJson(retResult);
		} catch (Exception e) {
			logger.error("",e);
            retResult.result = -6;
            retResult.message = e.getMessage();
			return ParseJson.encodeJson(retResult);
		}
	}
	
	private boolean checkPPSSig(Map<String, String> requestMap,String key) throws UnsupportedEncodingException {
		logger.debug(requestMap.toString());

		String user_id = requestMap.get("user_id");
		String role_id = requestMap.get("role_id");
		String order_id = requestMap.get("order_id");
		String money = requestMap.get("money");
		String time = requestMap.get("time");
		String signStr = user_id  + role_id + order_id + money + time + key;
		String mySign = DigestUtils.md5Hex(signStr);
		logger.debug("mySign is : " + mySign);
		String sign = requestMap.get("sign");
		logger.debug("msg sign is : " + sign);

        return StringUtil.equalsStringIgnoreCase(mySign, sign);
	}
	
	static class ReturnObj {
		int result;
		String message;
		public int getResult() {
			return result;
		}
		public void setResult(int result) {
			this.result = result;
		}
		public String getMessage() {
			return message;
		}
        public void setMessage(String message) {
			this.message = message;
		}
	}
}
