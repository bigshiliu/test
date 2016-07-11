package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLEncoder;
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
import com.hutong.supersdk.sdk.modeltools.huawei.HuaWeiSDKInfo;
import com.hutong.supersdk.sdk.utils.huawei.HuaWeiRSA;
import com.hutong.supersdk.sdk.utils.huawei.HuaweiResultDomain;
import org.springframework.util.StringUtils;

@Component("andHuaWeiSDK")
public class AndHuaWeiSDK implements IPayCallBackSDK, IVerifyUserSDK {

	public static String SDK_ID = "AndHuaWei";

	private static Log logger = LogFactory.getLog(AndHuaWeiSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return HuaWeiSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			String token = input.getSDKAccessToken();
			HuaWeiSDKInfo configInfo = (HuaWeiSDKInfo) config;
			token = URLEncoder.encode(token, "utf-8").replace("+", "%2B");

			String url = configInfo.getRequestUrl();

			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("nsp_svc", "OpenUP.User.getInfo");
			paramMap.put("nsp_ts", System.currentTimeMillis() / 1000L + "");
			paramMap.put("access_token", token);

			String sendUrl = url + "?nsp_svc=OpenUP.User.getInfo&nsp_ts=" + paramMap.get("nsp_ts") + "&access_token="
					+ paramMap.get("access_token");
			String result = HttpUtil.get(sendUrl);
			logger.debug(result);

			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap != null && !StringUtils.isEmpty(String.valueOf(resultMap.get("userID")))) {
                ret.setSdkUid(String.valueOf(resultMap.get("userID")));
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            logger.error(this.getSDKId() + " Login Error.result:" + result);
            ret.setErrorMsg(this.getSDKId() + " Login Error.");
            return ret.fail();
        } catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " Login Error,UnsupportedEncodingException!");
			return ret.fail();
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        logger.debug(paramMap.toString());
        HuaweiResultDomain ret = new HuaweiResultDomain();
        try {
            HuaWeiSDKInfo configInfo = (HuaWeiSDKInfo) config;

			String sign = paramMap.get("sign");

			String pubKey = configInfo.getPubKey();
			//验证成功
			if (rsaDoCheck(paramMap, sign, pubKey)) {
				ret.setResult(0);
			//验证失败	
			}else{
				ret.setResult(1);
				return ret;
			}
			String platformOrderId = paramMap.get("orderId");
			String sdkOrderId = paramMap.get("requestId");
			String amount = paramMap.get("amount");
			String payType = paramMap.get("payType");
			float CostMoney =Float.parseFloat(amount); 
			String currencyType = "RMB";
			boolean iResult = callback.succeedPayment(sdkOrderId, platformOrderId, CostMoney, currencyType,
                    payType, ParseJson.encodeJson(paramMap));
			if(iResult)
				ret.setResult(0);
			else
				ret.setResult(99);
			return ret;
		} catch (Exception e) {
			logger.error("",e);
			ret.setResult(99);
			return ret;
		}
	}

	private static boolean rsaDoCheck(Map<String, String> params, String sign, String publicKey) {
		// 获取待签名字符串
		String content = HuaWeiRSA.getSignData(params);
		// 验签
		return HuaWeiRSA.doCheck(content, sign, publicKey);
	}
}
