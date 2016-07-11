package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.leshitv.LeShiTVLoginRet;
import com.hutong.supersdk.sdk.modeltools.leshitv.LeShiTVSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andLeShiTVSDK")
public class AndLeShiTVSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndLeShiTV";

	private final static Log logger = LogFactory.getLog(AndLeShiTVSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return LeShiTVSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            LeShiTVSDKInfo configInfo = (LeShiTVSDKInfo) config;
            String uid = input.getSdkUid();
            String access_token = input.getSDKAccessToken();
            String client_id = configInfo.getClient_id();
            String url = configInfo.getRequestUrl();
            String urlStr = url + "?client_id=" + client_id + "&uid=" + uid + "&access_token=" + access_token;
            logger.debug(this.getSDKId() + " verifyUser. urlStr:" + urlStr);

            String result = HttpUtil.get(urlStr);
            logger.debug(this.getSDKId() + " verifyUser. result:" + result);

            LeShiTVLoginRet logRet = ParseJson.getJsonContentByStr(result, LeShiTVLoginRet.class);
            if (logRet == null || !"1".equals(logRet.getStatus())) {
                ret.setErrorMsg(this.getSDKId() + " verifyUser. error:" + (logRet != null ? logRet.getError() : null) + " errorCode:"
                        + (logRet != null ? logRet.getError_code() : null));
                return ret.fail();
            }
            ret.setSdkUid(uid);
            ret.setSdkAccessToken(access_token);
            return ret.success();
        }
        catch (Exception ex) {
            logger.error("" ,ex);
            ret.setErrorMsg(ex.getMessage());
            return ret.fail();
        }
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. paramMap:" + paramMap.toString());

		String success = "success";
		String fail = "fail";
		try {

			LeShiTVSDKInfo configInfo = (LeShiTVSDKInfo) config;

			// 进行验签,转换签名Map
			Map<String, Object> signMap = SignUtil.convert(paramMap);
			// 不进行签名的参数
			String[] notIn = new String[] { "sign", "cooperator_order_no", "extra_info", "original_price" };
			String mySignStr = SignUtil.createSignData(signMap, notIn, true, "&");

			String privateKey = configInfo.getPrivateKey();
			
			//组装签名str
			mySignStr = mySignStr + "&key=" + privateKey;
			logger.debug(this.getSDKId() + " payCallBack. mySignStr:" + mySignStr);

			String mySign = MD5Util.MD5(mySignStr);
			logger.debug(this.getSDKId() + " payCallBack. mySign:" + mySign);

			String sign = paramMap.get("sign");
			logger.debug(this.getSDKId() + " payCallBack. sign:" + sign);

			if (!StringUtil.equalsStringIgnoreCase(sign, mySign)) {
				logger.debug(this.getSDKId() + " payCallBack. check sign error !");
				return fail;
			}

			String out_trade_no = paramMap.get("out_trade_no");
			String price = paramMap.get("price");
			String cooperator_order_no = paramMap.get("cooperator_order_no");

			boolean iResult = callback.succeedPayment(cooperator_order_no, out_trade_no,
					Double.parseDouble(price), "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
        return fail;
    }

}
