package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools._7k7k._7k7kLoginRet;
import com.hutong.supersdk.sdk.modeltools._7k7k._7k7kSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("and7K7KSDK")
public class And_7K7KSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "And7K7K";
	
	private static final Log logger = LogFactory.getLog(And_7K7KSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return _7k7kSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.info(this.getSDKId() + " payCallBack. start! paramMap:" + paramMap.toString());
		String success = "success";
		String fail = "fail";
		_7k7kSDKInfo configInfo = (_7k7kSDKInfo) config;
		try {
			String uid = paramMap.get("uid");
			String cporder = paramMap.get("cporder");
			String money = paramMap.get("money");
			String order = paramMap.get("order");
			String cpappid = paramMap.get("cpappid");
			String sign = paramMap.get("sign");
			String appsecret = configInfo.getAppsecret();
			String mySignStr = uid + cporder + money + order + cpappid + appsecret;
			String mySign = MD5Util.MD5(mySignStr);
			
			logger.debug(this.getSDKId() + " payCallBack. mySign:" + mySign + " sign:" + sign);
			
			if(StringUtil.equalsStringIgnoreCase(sign, mySign)){
				boolean iResult = callback.succeedPayment(cporder, order, Double.parseDouble(money), "RMB", "", ParseJson.encodeJson(paramMap));
				if(iResult){
					return success;
				}
			}
			return fail;
		} catch (Exception e) {
			logger.error("", e);
			return fail;
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.info(this.getSDKId() + " verifyUser. start!");
		//verifyUser返回
		SDKVerifyRet ret = new SDKVerifyRet();
		//SDKconfig配置对象
		_7k7kSDKInfo configInfo = (_7k7kSDKInfo) config;
		//SDK登陆验证返回
		_7k7kLoginRet loginRet;
		try {
			String uid = input.getSdkUid();
			String vkey = input.getSDKAccessToken();
			String appid = configInfo.getAppid();
			String appsecret = configInfo.getAppsecret();
			String signStr = uid + vkey + appid + appsecret;
			String sign = MD5Util.MD5(signStr);
			String url = configInfo.getRequestUrl();
			
			String urlStr = url + "/uid/" + uid + "/vkey/" + vkey + "/appid/" + appid + "/sign/" + sign;
			
			logger.debug(this.getSDKId() + " verifyUser. requestUrl:" + urlStr);
			
			String result = HttpUtil.get(urlStr);
			
			logger.debug(this.getSDKId() + " veifyUser. result:" + result);
			
			loginRet = ParseJson.getJsonContentByStr(result, _7k7kLoginRet.class);
			//如果status返回为0,则表示验证通过
			if( loginRet != null && (!StringUtils.isEmpty(loginRet.getStatus())) && loginRet.getStatus().equals("0")){
				ret.setSdkUid(uid);
				ret.setSdkAccessToken(vkey);
				ret.success();
				return ret;
			}
			logger.error(this.getSDKId() + " verifyUser. ErrorMsg:" + (loginRet != null ? loginRet.getMsg() : "null"));
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " verifyUser is Error!");
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " verifyUser is Error!");
			return ret;
		}
	}

}
