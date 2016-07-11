package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.lang.math.NumberUtils;
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
import com.hutong.supersdk.sdk.modeltools.le8.Le8SDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("iOSYYLe8SDK")
public class IOSYYLe8SDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYLe8";
	
	private final static Log logger = LogFactory.getLog(IOSYYLe8SDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return Le8SDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "success";
		String fail = "fail";
		try {
			Le8SDKInfo configInfo = (Le8SDKInfo) config;
			String n_time = paramMap.get("n_time");
			n_time = URLEncoder.encode(n_time,"UTF-8");
			String appid = paramMap.get("appid");
			appid = URLEncoder.encode(appid,"UTF-8");
			String o_id = paramMap.get("o_id");
			o_id = URLEncoder.encode(o_id,"UTF-8");
			String t_fee = paramMap.get("t_fee");
			t_fee = URLEncoder.encode(t_fee,"UTF-8");
			String g_name = paramMap.get("g_name");
			g_name = URLEncoder.encode(g_name,"UTF-8");
			String g_body = paramMap.get("g_body");
			g_body = URLEncoder.encode(g_body,"UTF-8");
			String t_status = paramMap.get("t_status");
			t_status = URLEncoder.encode(t_status,"UTF-8");
			String o_sign = paramMap.get("o_sign");
			String o_orderid = paramMap.get("o_orderid");
			o_orderid = URLEncoder.encode(o_orderid,"UTF-8");
			String appKey = configInfo.getAppKey();
			String encodeStr = "n_time=" + n_time + "&appid=" + appid + "&o_id=" + o_id + "&t_fee=" + t_fee + "&g_name=" + g_name + "&g_body=" + g_body + "&t_status=" + t_status;
			String mySignStr = encodeStr + appKey;
			String sign = MD5Util.MD5(mySignStr);
			logger.debug(encodeStr);
			logger.debug("sign:" + sign + " o_sign:" + o_sign);
			if (StringUtil.equalsStringIgnoreCase(sign, o_sign)) {

				boolean iResult = t_status.equals("1") ? callback.succeedPayment(o_id, o_orderid, NumberUtils.toDouble(t_fee),
						"RMB", "", ParseJson.encodeJson(paramMap)) : callback.failPayment(o_id, o_orderid);

				if (iResult)
					return success;
				else
					logger.error("save Payment failed! orderId: " + o_id);
			}
			else
				logger.error("IOSLe8SDK succeed Order sign Error.");
		} catch (Exception e) {
			logger.error("", e);
		}
		return fail;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            Le8SDKInfo configInfo = (Le8SDKInfo) config;
            String url = configInfo.getRequestUrl();
            String appId = configInfo.getAppId();
            String uid = input.getSdkUid();
            String token = input.getSDKAccessToken();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("appid", appId);
            paramMap.put("t", token);
            paramMap.put("uid", uid);
            String result = HttpUtil.postForm(url, paramMap);
            if("success".equals(result)){
                ret.setSdkUid(uid);
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
        } catch (Exception e) {
            logger.error("", e);
            ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
        }
        return ret.fail();
    }

}
