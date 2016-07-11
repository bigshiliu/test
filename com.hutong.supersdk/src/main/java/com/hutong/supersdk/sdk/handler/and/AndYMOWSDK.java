package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.youmiouwan.YMOWSDKInfo;
import com.hutong.supersdk.sdk.utils.youmiouwan.UmipayHelper;

@Component("andYMOWSDK")
public class AndYMOWSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndYouMiOuWan";
	
	private final static Log logger = LogFactory.getLog(AndYMOWSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YMOWSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack start !");
		String success = "success";
		String fail = "fail";
		try {
			YMOWSDKInfo configInfo = (YMOWSDKInfo) config;

			String orderStatus = paramMap.get("orderStatus");
			String remark = paramMap.get("remark");
			if(!"1".equals(orderStatus)){
				logger.debug(this.getSDKId() + " payCallBack Error. orderStatus:" + orderStatus + " remark:" + remark);
				return fail;
			}
			//透传参数
			String callbackInfo = paramMap.get("callbackInfo");
			String orderId = paramMap.get("orderId");
			String payType = paramMap.get("payType");
			//单位为元,浮点数
			double amount = Double.parseDouble(paramMap.get("amount"));
			
			String serverSecret = configInfo.getServerSecret();
			UmipayHelper helper = new UmipayHelper();
			if (!helper.verifyUrlSign(paramMap, serverSecret)) {
				logger.debug(this.getSDKId() + " payCallBack Error. verifyUrlSign error !");
				return fail;
			}
			boolean iResult = callback.succeedPayment(callbackInfo, orderId, amount, "RMB", payType, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
        return fail;
    }

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start !");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            YMOWSDKInfo configInfo = (YMOWSDKInfo) config;
            //偶玩用户的唯一标识
			String uid = input.getSdkUid();
			String timestamp = input.getExtraKey("timestamp");
			String sign = input.getExtraKey("sign");
			//密钥
			String serverSecret = configInfo.getServerSecret();
			logger.debug(this.getSDKId() + " uid:" + uid + " timestamp:" + timestamp + " sign:" + sign);
			UmipayHelper helper = new UmipayHelper();
			if(helper.verifyLoginSign(uid, timestamp, sign, serverSecret)){
                ret.setSdkUid(uid);
                return ret.success();
            }
            //验证失败
            ret.setErrorMsg(this.getSDKId() + " verifyUser Error, check sign error !");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
		}
        return ret.fail();
    }
}
