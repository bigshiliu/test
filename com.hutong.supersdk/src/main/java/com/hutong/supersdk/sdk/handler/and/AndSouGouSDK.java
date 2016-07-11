package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.HashMap;
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
import com.hutong.supersdk.sdk.modeltools.sougou.SouGouSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;
import org.springframework.util.StringUtils;

@Component("andSouGouSDK")
public class AndSouGouSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndSouGou";

	private final static Log logger = LogFactory.getLog(AndSouGouSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return SouGouSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " veriftUser. Start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			SouGouSDKInfo configInfo = (SouGouSDKInfo) config;

			// 获取参数
			String gid = configInfo.getGid();
			String secret = configInfo.getSecret();
			String uid = input.getSdkUid();
			String token = input.getSDKAccessToken();

			// 创建请求参数
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("gid", gid);
			paramMap.put("session_key", token);
			paramMap.put("user_id", uid);

			// 签名
			String authStr = "gid=" + gid + "&session_key=" + token + "&user_id=" + uid + "&" + secret;
			String auth = MD5Util.MD5(authStr);
			logger.debug(this.getSDKId() + " verifyUser. authStr:" + authStr + " auth:" + auth);
			paramMap.put("auth", auth);
			
			String url;
			if(!StringUtils.isEmpty(input.getExtraKey("IS_DEBUG")) && "true".equals(input.getExtraKey("IS_DEBUG"))){
				url = configInfo.getDebugRequestUrl();
			}else {
				url = configInfo.getRequestUrl();
			}
 			String result = HttpUtil.postForm(url, paramMap);
			logger.debug(this.getSDKId() + " verifyUser. result:" + result);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			//返回值
			if(resultMap != null && StringUtil.equalsStringIgnoreCase("true", String.valueOf(resultMap.get("result")))) {
				ret.setSdkUid(uid);
				ret.setSdkAccessToken(token);
				return ret.success();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error!");
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. Start! paramMap:" + paramMap.toString());
		String OK = "OK";
		try {
			SouGouSDKInfo configInfo = (SouGouSDKInfo) config;
			String platformId = paramMap.get("oid");
			platformId = StringUtils.isEmpty(platformId) ? "SoGou order id is empty" : platformId;

            String sign = paramMap.get("auth");

            String souGouPayKey = configInfo.getPayKey();
            Map<String, Object> signMap = SignUtil.convert(paramMap);
            String mySignStr = SignUtil.createSignData(signMap, new String[]{"auth"}, true, "&");
            mySignStr = mySignStr + "&{" + souGouPayKey + "}";
			String mySign = MD5Util.MD5(mySignStr);

			if(!StringUtil.equalsStringIgnoreCase(sign, mySign)){
				logger.debug(this.getSDKId() + " verifyUser. SouGou check Sign False!");
				return "ERR_200";
			}

			String userId = paramMap.get("uid");
			if (StringUtils.isEmpty(userId)) {
				logger.error(this.getSDKId() + " verifyUser. SouGou UID is Null!");
				return "ERR_300";
			}
			//透传参数
			String orderId = paramMap.get("appdata");
			String valueString = paramMap.get("realAmount");
			boolean iResult = callback.succeedPayment(orderId, platformId, Double.parseDouble(valueString),
                    "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return OK;
		} catch (Exception e) {
			logger.error("", e);
		}
        return "ERR_500";
    }

}
