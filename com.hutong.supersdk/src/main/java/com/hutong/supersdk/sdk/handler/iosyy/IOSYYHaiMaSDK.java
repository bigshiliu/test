package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
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
import com.hutong.supersdk.sdk.modeltools.haima.HaiMaSDKInfo;
import com.hutong.supersdk.sdk.utils.aibei.MD5;

@Component("iOSYYHaiMaSDK")
public class IOSYYHaiMaSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "IOSYYHaiMa";

	private final static Log logger = LogFactory.getLog(IOSYYHaiMaSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return HaiMaSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();

		try {
			HaiMaSDKInfo configInfo = (HaiMaSDKInfo) config;

			String url = configInfo.getRequestUrl();
			String appId = configInfo.getAppId();
			String token = input.getSDKAccessToken();
			String uid = input.getSdkUid();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("appid", appId);
			paramMap.put("t", token);

			String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
			if (StringUtil.equalsStringIgnoreCase("success", result)) {
				ret.setSdkUid(uid);
				ret.setSdkAccessToken(token);
				return ret.success();
			}
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
		}
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "success";
		String fail = "fail";
		try {
			HaiMaSDKInfo configInfo = (HaiMaSDKInfo) config;
			String n_time = paramMap.get("notify_time");
			String appid = paramMap.get("appid");
			String o_id = paramMap.get("out_trade_no");
			String t_fee = paramMap.get("total_fee");
			String g_name = paramMap.get("subject");
			String g_body = paramMap.get("body");
			String t_status = paramMap.get("trade_status");
			String o_sign = paramMap.get("sign");

			String appKey = configInfo.getAppKey();
			String encodeStr = "notify_time=" + encodeUrl(n_time) + "&appid=" + encodeUrl(appid) + "&out_trade_no="
					+ encodeUrl(o_id) + "&total_fee=" + encodeUrl(t_fee) + "&subject=" + encodeUrl(g_name) + "&body="
					+ encodeUrl(g_body) + "&trade_status=" + t_status;
			//组装sign
			String sign = MD5.md5Digest(encodeStr + appKey);
			logger.debug(encodeStr);
			
			//分辨判断UTF-8转码后的结果
			if (StringUtil.equalsStringIgnoreCase(sign, o_sign) && "1".equals(t_status)) {
                boolean iResult = callback.succeedPayment(o_id, "", Double.parseDouble(t_fee), "RMB", "", ParseJson.encodeJson(paramMap));
                if(iResult)
                    return success;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        return fail;
    }

	private static String encodeUrl(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "utf8");
	}
}
