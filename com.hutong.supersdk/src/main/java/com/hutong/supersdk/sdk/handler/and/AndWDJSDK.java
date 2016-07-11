package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
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
import com.hutong.supersdk.sdk.modeltools.wandoujia.WDJSDKInfo;
import com.hutong.supersdk.sdk.utils.wandoujia.WDJ_SDKCheck;

@Component("andWDJSDK")
public class AndWDJSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public static final String SDK_ID = "AndWanDouJia";
	
	private static final Log logger = LogFactory.getLog(AndWDJSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return WDJSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String success = "success";
        String fail = "fail";
		try {
			WDJSDKInfo configInfo = (WDJSDKInfo) config;

			String content = paramMap.get("content");
			String pubKey = configInfo.getPubKey();
			if(!checkWdjSig(paramMap, pubKey))
				return fail;

			Map contentMap = ParseJson.getJsonContentByStr(content, Map.class);
            if (contentMap != null) {
                String orderId = String.valueOf(contentMap.get("out_trade_no"));
                String platformOrderId = String.valueOf(contentMap.get("orderId"));
                String amount = String.valueOf(contentMap.get("money"));
                float CostMoney = Float.parseFloat(amount) / 100L;
                String chargeType = String.valueOf(contentMap.get("chargeType"));
                String currencyType = "RMB";
                boolean iResult = callback.succeedPayment(orderId, platformOrderId, CostMoney, currencyType,
                        chargeType, ParseJson.encodeJson(paramMap));
                if (iResult)
                    return success;
            }
		} catch (Exception e) {
			logger.error("", e);
		}
        return "fail";
    }

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
        SDKVerifyRet ret = new SDKVerifyRet();
        try {
            WDJSDKInfo configInfo = (WDJSDKInfo) config;
            String uid = String.valueOf(input.getSdkUid());
            String token = input.getSDKAccessToken();
            String uidCheckUrl = configInfo.getUidCheckUrl();
            String wdjAppKeyId = configInfo.getWdjAppKeyId();
            String url = uidCheckUrl + "?uid=" + uid + "&token=" +URLEncoder.encode(token, "UTF-8") + "&appkey_id=" + wdjAppKeyId;
            String strRet = HttpUtil.get(url);
            logger.debug(strRet);
            if(StringUtil.equalsStringIgnoreCase(strRet, "false")){
                logger.error("Wdj checkUser failed! result:" + strRet);
                ret.setErrorMsg("Wdj checkUser failed!");
                return ret.fail();
            }
            ret.setSdkUid(uid);
            ret.setSdkAccessToken(token);
            return ret.success();
        } catch (Exception e) {
            logger.error("",e);
        }
        ret.setErrorMsg("URLEncode Error,UnsupportedEncodingException !");
        return ret.fail();
    }
	
	private static boolean checkWdjSig(Map<String, String> requestMap, String pubKey) {
		String content = requestMap.get("content");
		String sign = requestMap.get("sign");
		return WDJ_SDKCheck.wdjDoCheck(content, sign, pubKey);
	}
}
