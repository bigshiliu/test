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
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools._37wan.PayCallbackRet;
import com.hutong.supersdk.sdk.modeltools._37wan._37WanLoginRet;
import com.hutong.supersdk.sdk.modeltools._37wan._37WanSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("and_37WanSDK")
public class And_37WanSDK implements IVerifyUserSDK, IPayCallBackSDK{

	private static final String SDK_ID = "And37Wan";
	
	private final static Log logger = LogFactory.getLog(And_37WanSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return _37WanSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack start !");
		logger.debug(paramMap.toString());
		PayCallbackRet ret = new PayCallbackRet();
		_37WanSDKInfo configInfo = (_37WanSDKInfo) config;
		try {
			String time = paramMap.get("time");
			String sign = paramMap.get("sign");
			String oid = paramMap.get("oid");
			String doid = paramMap.get("doid");
			String dsid = paramMap.get("dsid");
			String uid = paramMap.get("uid");
			String coin = paramMap.get("coin");
			String moneyStr = paramMap.get("money");
			String secret = configInfo.getSecret();
			String signStr = time + secret + oid + doid + dsid + uid + moneyStr + coin;
			logger.info(this.getSDKId() + " payCallBack. signStr:" + signStr);
			String mySign = MD5Util.MD5(signStr);
			logger.info(this.getSDKId() + " payCallBack. mySign:" + mySign);
			if(!"".equals(signStr) && !"".equals(mySign) && sign.equalsIgnoreCase(mySign)){
				double money = Double.parseDouble(moneyStr);
				boolean iResult = callback.succeedPayment(doid, oid, money, "RMB", "", ParseJson.encodeJson(paramMap));
				if(iResult){
					ret.setState(1);
					ret.setMsg("成功");
					return ParseJson.encodeJson(ret);
				}
				ret.setState(0);
				ret.setMsg("失败");
				return ParseJson.encodeJson(ret);
			}else{
				logger.info(this.getSDKId() + " payCallBack. sign Error! sign:" + sign + " mySign:" + mySign);
				ret.setState(0);
				ret.setMsg("失败");
				return ParseJson.encodeJson(ret);
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.setState(0);
			ret.setMsg("失败");
			return ParseJson.encodeJson(ret);
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.info(this.getSDKId() + " verifyUser start !");
		SDKVerifyRet ret = new SDKVerifyRet();
		_37WanSDKInfo configInfo = (_37WanSDKInfo) config;
		int pid = configInfo.getPid();
		int gid = configInfo.getGid();
		String key = configInfo.getKey();
		int time = (int) (System.currentTimeMillis()/1000);
		String signStr = MD5Util.MD5(gid + "" + time + "" + key);
		String token = input.getSDKAccessToken();
		String url = configInfo.getRequestUrl();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("pid", String.valueOf(pid));
		paramMap.put("gid", String.valueOf(gid));
		paramMap.put("time", String.valueOf(time));
		paramMap.put("sign", signStr);
		paramMap.put("token", token);
		logger.info(this.getSDKId() + " verifyUser, paramMap:" + paramMap.toString());
		String result = HttpUtil.postForm(url, paramMap);
		_37WanLoginRet loginRet = ParseJson.getJsonContentByStr(result, _37WanLoginRet.class);
		if(loginRet == null || 1 != loginRet.getState()){
			logger.error(this.getSDKId() + " verifyUser Error! result:" + result);
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
			return ret;
		}else{
			ret.setSdkUid(loginRet.getData().get("uid"));
			ret.setExtra("disname", loginRet.getData().get("disname"));
			ret.setSdkAccessToken(token);
			ret.success();
			logger.info(this.getSDKId() + " verifyUser end !");
			return ret;
		}
	}
	
//	public static void main(String[] args) {
//		double gid = 200.00;
//		String key = "abcd";
//		int time = (int) (System.currentTimeMillis()/1000);
//		String signStr = gid+""+time+key;
//		System.out.println(MD5Util.MD5(signStr));
//	}
}
