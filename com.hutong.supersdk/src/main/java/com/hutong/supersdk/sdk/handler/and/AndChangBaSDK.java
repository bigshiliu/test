package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
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
import com.hutong.supersdk.sdk.modeltools.changba.ChangBaLoginRet;
import com.hutong.supersdk.sdk.modeltools.changba.ChangBaSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.changba.SignHelper;

@Component("andChangBaSDK")
public class AndChangBaSDK  implements IVerifyUserSDK, IPayCallBackSDK{
	
	private static final String SDK_ID = "AndChangBa";
	
	private final static Log logger = LogFactory.getLog(AndChangBaSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return ChangBaSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. start! paramMap:" + paramMap.toString());
		String fail = "FAILURE";
		String success = "SUCCESS";
		ChangBaSDKInfo configInfo = (ChangBaSDKInfo) config;
		try {
			String transdata = paramMap.get("transdata");
			Map transMap = ParseJson.getJsonContentByStr(transdata, Map.class);
			//交易结果
			if(transMap != null && 0 == Integer.parseInt(String.valueOf(transMap.get("result")))){
				//RSA私钥
				String priKey = configInfo.getPriKey();
				//RSA公钥
				String pubKey = configInfo.getPubKey();
				// 签名
				String sign = SignHelper.sign(transdata, priKey);
				// 验签
				if (SignHelper.verify(transdata, sign, pubKey)) {
					//唱吧订单号
					String transid = String.valueOf(transMap.get("transid"));
					//商户订单号
					String cporderid = String.valueOf(transMap.get("cporderid"));
					//支付方式
					String payType = String.valueOf(transMap.get("paytype"));
					//支付金额
					double money = Double.parseDouble(String.valueOf(transMap.get("money")));
					
					boolean iResult = callback.succeedPayment(cporderid, transid, money, "RMB", payType, ParseJson.encodeJson(paramMap));
					if(iResult)
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
		logger.debug(this.getSDKId() + " verifyUser. start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			ChangBaSDKInfo configInfo = (ChangBaSDKInfo) config;
			String accesstoken = input.getSDKAccessToken();
			String ver = "1.0";
			//游戏渠道名称
			String id = configInfo.getId();
			String secret = configInfo.getSecret();
			String sigStr = "accesstoken=" + accesstoken + "&id=" + id + "&ver=" + ver;
			String str = sigStr + MD5Util.MD5(secret);
			logger.debug(this.getSDKId() + " verifyUser. sigStr:" + str);
			String sig = MD5Util.MD5(str);
			
			String url = configInfo.getRequestUrl() + "?ver=" + ver + "&id=" + id + "&accesstoken=" + accesstoken + "&sig=" + sig;
			
			String result = HttpUtil.get(url);

			ChangBaLoginRet loginRet = ParseJson.getJsonContentByStr(result, ChangBaLoginRet.class);
			// 0标示成功
			if(loginRet != null && 0 == loginRet.getErrno()){
				ret.setSdkUid(loginRet.getData().get("uid"));
				ret.setSdkAccessToken(accesstoken);
				ret.setExtra("nickname", loginRet.getData().get("nickname"));
				return ret.success();
			}
			logger.error(this.getSDKId() + " verifyUser. Login Error! ErrorMsg:" + (loginRet != null ? loginRet.getErrmsg() : null));
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error!");
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Error!");
			return ret.fail();
		}
	}
}
