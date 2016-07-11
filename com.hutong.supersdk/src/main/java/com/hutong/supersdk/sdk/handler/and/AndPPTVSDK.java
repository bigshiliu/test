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
import com.hutong.supersdk.sdk.modeltools.pptv.PPTVPayBackRet;
import com.hutong.supersdk.sdk.modeltools.pptv.PPTVSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import org.springframework.util.StringUtils;

@Component("andPPTVSDK")
public class AndPPTVSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndPPTV";

	private final static Log logger = LogFactory.getLog(AndPPTVSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return PPTVSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. Start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			PPTVSDKInfo configInfo = (PPTVSDKInfo) config;
			// 固定参数 login
			String type = "type";
			// 固定参数 mobgame
			String app = "mobgame";
			String sessionid = input.getSDKAccessToken();
			String username = input.getExtraKey("username");
			String uid = input.getSdkUid();
			if (StringUtils.isEmpty(uid)) {
				logger.error(this.getSDKId() + " verifyUser. Login Error. UserId is null !");
				ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error.");
				return ret.fail();
			}
			if (StringUtils.isEmpty(sessionid)) {
				logger.error(this.getSDKId() + " verifyUser. Login Error. AccessToken is null !");
				ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error.");
				return ret.fail();
			}
			if (StringUtils.isEmpty(username)) {
				logger.error(this.getSDKId() + " verifyUser. Login Error. UserName is null !");
				ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error.");
				return ret.fail();
			}
			String url = configInfo.getRequestUrl();
			String urlStr = url + "?type=" + type + "&sessionid=" + sessionid + "&username=" + username + "&app=" + app;

			logger.debug(this.getSDKId() + " verifyUser. urlStr:" + urlStr);
			String result = HttpUtil.get(urlStr);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap != null && "1".equals(String.valueOf(resultMap.get("status")))) {
				ret.setSdkUid(input.getSdkUid());
				ret.setSdkAccessToken(sessionid);
				return ret.success();
			}
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error!");
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
		}
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. Start! paramMap:" + paramMap.toString());
		PPTVPayBackRet ret = new PPTVPayBackRet();
		try {
            PPTVSDKInfo configInfo = (PPTVSDKInfo) config;
            String sid = paramMap.get("sid");
			String roid = paramMap.get("roid");
			String username = paramMap.get("username");
			String oid = paramMap.get("oid");
			String amount = paramMap.get("amount");
			String extra = paramMap.get("extra");
			String time = paramMap.get("time");
			String sign = paramMap.get("sign");
			String key = configInfo.getKey();

			// 进行延签处理
			String mySignStr = sid + username + roid + oid + amount + time + key;
			logger.debug(this.getSDKId() + " payCallBack. mySignStr:" + mySignStr);
			String mySign = MD5Util.MD5(mySignStr);
			logger.debug(this.getSDKId() + " payCallBack. mySign:" + mySign);

			if (StringUtil.equalsStringIgnoreCase(sign, mySign)) {
				boolean iResult = callback.succeedPayment(extra, oid, Double.parseDouble(amount), "RMB", "",
						ParseJson.encodeJson(paramMap));
				if (iResult) {
					ret.setCode("1");
					return ret.toString();
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        ret.setCode("2");
        return ret.toString();
    }

}
