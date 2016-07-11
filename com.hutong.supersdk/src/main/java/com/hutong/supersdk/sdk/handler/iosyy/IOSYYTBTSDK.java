package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
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
import com.hutong.supersdk.sdk.modeltools.tongbutui.TBTSDKInfo;
import org.springframework.util.StringUtils;

@Component("iOSYYTBTSDK")
public class IOSYYTBTSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYTongBuTui";
	
	private final static Log logger = LogFactory.getLog(IOSYYTBTSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return TBTSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug("payCallBack start");
		String resultSuc = "{\"status\":\"success\"}";
		String resultFail = "{\"status\":\"fail\"}";
		try {
			TBTSDKInfo configInfo = (TBTSDKInfo) config;
			logger.debug(ParseJson.encodeJson(paramMap));
			String secretKey = configInfo.getAppKey();
			String source = paramMap.get("source");
			String trade_no = paramMap.get("trade_no");
			String amount = paramMap.get("amount");
			String partner = paramMap.get("partner");
			String paydes = paramMap.get("paydes");
			if(paydes == null) {
				paydes = "tbt";
			}
			String debug = paramMap.get("debug");
			String tborder = paramMap.get("tborder");
            String sign = paramMap.get("sign");
            double costMoney = Double.parseDouble(amount) / 100;

            String signStr;
            if (tborder == null) {
				signStr = "source=" + source + "&trade_no=" + trade_no + "&amount="
						+ amount + "&partner=" + partner + "&paydes=" + paydes
						+ "&debug=" + debug + "&key=" + secretKey;
			} else {
				signStr = "source=" + source + "&trade_no=" + trade_no + "&amount="
						+ amount + "&partner=" + partner + "&paydes=" + paydes
						+ "&debug=" + debug + "&tborder=" + tborder + "&key="
						+ secretKey;
			}
            logger.debug("check signStr = "+signStr);
			String mySign = DigestUtils.md5Hex(signStr);
			if(StringUtil.equalsString(mySign, sign)){
				logger.error("Sign check error. sign=" + sign + " mySign=" + mySign);
				return resultFail;
			}

            tborder = StringUtils.isEmpty(tborder) ? "Order is empty" : tborder;
			boolean iResult = callback.succeedPayment(trade_no, tborder, costMoney, "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return resultSuc;
		} catch (Exception e) {
			logger.error("", e);
		}
        return resultFail;
    }

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            TBTSDKInfo configInfo = (TBTSDKInfo) config;
            String token = input.getSDKAccessToken();
            String url = configInfo.getRequestUrl();
            String appId = configInfo.getAppKey();
            String urlStr = url + "?session=" + token + "&appid=" + appId;
            long result = Long.parseLong(HttpUtil.get(urlStr));
            String PlatformUserId;
            if(1 < result){
                PlatformUserId = result + "";
            }else if(0 == result){
                ret.fail();
                ret.setErrorMsg(this.getSDKId() + " verifyUser Error, Session is overtime !");
                return ret;
            }else{
                ret.fail();
                ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
                return ret;
            }
            ret.setSdkUid(PlatformUserId);
            ret.setSdkAccessToken(token);
            return ret.success();
        } catch (Exception e) {
            logger.error("", e);
        }
        return ret.fail();
    }

}
