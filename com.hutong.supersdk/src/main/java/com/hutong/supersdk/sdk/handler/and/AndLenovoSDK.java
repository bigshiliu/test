package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
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
import com.hutong.supersdk.sdk.modeltools.lenovo.LenovoObj;
import com.hutong.supersdk.sdk.modeltools.lenovo.LenovoSDKInfo;
import com.lenovo.pay.sign.CpTransSyncSignValid;

@Component("andLenovoSDK")
public class AndLenovoSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public static final String SDK_ID = "AndLenovo";
	
	private final static Log logger = LogFactory.getLog(AndLenovoSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return LenovoSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "SUCCESS";
        String failure = "FAILURE";
        try {
            LenovoSDKInfo configInfo = (LenovoSDKInfo) config;
            logger.debug(paramMap.toString());
			String pubKey = configInfo.getPub_key();
			String transdata = paramMap.get("transdata");
			String sign = paramMap.get("sign");
			if(StringUtils.isBlank(transdata) || StringUtils.isBlank(sign))
				return failure;

			if(!CpTransSyncSignValid.validSign(transdata, sign, pubKey))
				return failure;


            LenovoObj lenovoObj = ParseJson.getJsonContentByStr(transdata, LenovoObj.class);

            if(lenovoObj == null || lenovoObj.getResult() != 0)
				return failure;

			String transId = lenovoObj.getTransid();
            transId = (org.springframework.util.StringUtils.isEmpty(transId)) ? "Lenovo Order id is empty" : transId;

            String orderId = lenovoObj.getExorderno();
			float amount = lenovoObj.getMoney() * 1.0f / 100f;
			String payWay = String.valueOf(lenovoObj.getPaytype());
            boolean iResult = callback.succeedPayment(orderId, transId, amount, "RMB", payWay, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("",e);
		}
        return failure;
    }

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            LenovoSDKInfo configInfo = (LenovoSDKInfo) config;
            String lpsust = input.getSDKAccessToken();
            String url = configInfo.getRequestUrl();
            String realm = configInfo.getRealm();
            url = url + "?lpsust=" + lpsust + "&realm=" + realm;
			String result = HttpUtil.get(url);
			logger.debug("request url:" + url + " result:" + result);
            //TODO 此处应当使用XML对象处理返回,避免字符串格式不满足的情况
			int start = result.indexOf("<AccountID>");
			int end = result.indexOf("</AccountID>");
			if(start == -1 || end == -1){
				ret.setErrorMsg(this.getSDKId() + " Login Error.");
				return ret.fail();
			}
            String accountId = result.substring(start + "<AccountID>".length(), end);
            ret.setSdkUid(accountId);
            ret.setSdkAccessToken(lpsust);
            return ret.success();
        } catch (Exception e) {
            logger.error("", e);
            ret.setErrorMsg(this.getSDKId() + " Login Error.");
            return ret.fail();
        }
	}
	
}
