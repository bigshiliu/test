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
import com.hutong.supersdk.sdk.modeltools.baofengyingyin.BFYYRet;
import com.hutong.supersdk.sdk.modeltools.baofengyingyin.BFYYSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
	
@Component("andBFYYSDK")
public class AndBFYYSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndBaoFengYingYin";

	private final static Log logger = LogFactory.getLog(AndBFYYSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return BFYYSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		BFYYSDKInfo configInfo = (BFYYSDKInfo) config;
		BFYYRet bfyyRet;
		try {
			String url = configInfo.getRequestUrl();
			String token = input.getSDKAccessToken();
			String urlStr = url + "?token=" + token;
			String result = HttpUtil.get(urlStr);
			bfyyRet = ParseJson.getJsonContentByStr(result, BFYYRet.class);
			if (bfyyRet == null || null == bfyyRet.getCode() || 1 != bfyyRet.getCode()) {
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
				return ret;
			}
			ret.setSdkUid(bfyyRet.getData().get("uid"));
			ret.setSdkAccessToken(token);
			ret.success();
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error!");
			return ret;
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		BFYYSDKInfo configInfo = (BFYYSDKInfo) config;
		String success = "success";
		String fail = "fail";
		try {
			String bill_no = paramMap.get("bill_no");
			String amount = paramMap.get("amount");
			String extra = paramMap.get("extra");
			String game_id = configInfo.getGameId();
			String key = configInfo.getKey();
			String server_id = paramMap.get("server_id");
			String user_id = paramMap.get("user_id");
			String sign = paramMap.get("sign");
			String signStr = amount + bill_no + extra + game_id + server_id + user_id + key;
			String mySign = MD5Util.MD5(signStr).toUpperCase();
			logger.debug(this.getSDKId() + " payCallBack.  sign:" + sign);
			logger.debug(this.getSDKId() + " payCallBack.  signStr:" + signStr);
			logger.debug(this.getSDKId() + " payCallBack.  mySign:" + mySign);
			if(null != sign && !"".equals(sign) && !"".equals(mySign) && sign.equals(mySign)) {
				boolean iResult = callback.succeedPayment(extra, bill_no, Double.parseDouble(amount), "RMB", "", ParseJson.encodeJson(paramMap));
				if(iResult){
					return success;
				}
				logger.error(this.getSDKId() + "payCallBack. callBack Error !");
				return fail;
			}
			logger.error(this.getSDKId() + " payCallBack. check Sign Error !");
			return fail;
		} catch (Exception e) {
			logger.error("", e);
			return fail;
		}
	}

}
