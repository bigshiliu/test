package com.hutong.supersdk.sdk.handler.and;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.qihu360.QiHu360SDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.qihu360.QihooUtil;

@Component("andQihu360SDK")
public class AndQiHu360SDK implements IPayCallBackSDK, IVerifyUserSDK {
	
	public static final String SDK_ID = "And360";
	
	private static final Log logger = LogFactory.getLog(AndQiHu360SDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return QiHu360SDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		return verifyQiHuUser(input, (QiHu360SDKInfo) config);

	}

	public static SDKVerifyRet verifyQiHuUser(JsonReqObj input, QiHu360SDKInfo configInfo) {
		//HTTP请求得到返回结果
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			//得到access_token
			String authCode = input.getSDKAccessToken();
            String urlSb = configInfo.getRequestUrl() +
                    "?access_token=" + authCode;
            String result = QihooUtil.requestUrl(urlSb, true);
			Map returnMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(returnMap == null || !returnMap.containsKey("id")){
				//异常处理,登陆异常
				ret.setErrorMsg(AndQiHu360SDK.SDK_ID + " check token error.");
			}
			else {
				ret.setSdkUid(returnMap.get("id").toString());
				ret.setSdkAccessToken(authCode);
				ret.setSdkRefreshToken("");

				ret.setExtra("name", String.valueOf(returnMap.get("name")));

				return ret.success();
			}
		} catch (IOException e) {
			logger.error("", e);
			ret.setErrorMsg(e.getMessage());
		}
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        String ok = "ok";
		try {
			QiHu360SDKInfo configInfo = (QiHu360SDKInfo) config;
			
			//如果支付返回成功，返回success
			String gateway_flag = paramMap.get("gateway_flag");
			//应用订单号
			String orderId = paramMap.get("app_order_id");
			//SDK(360)订单号
			String sdkOrderId = paramMap.get("order_id");
			//总价,以分为单位
			double payAmount = Double.parseDouble(paramMap.get("amount"))/100;
			//货币类型
			String currencyType = "RMB";
			//交易类型
			String payType = "";
			//进行签名验证
			if (!check360Sig(paramMap, configInfo)) {
				return "sign error";
			}
			if(!"success".equalsIgnoreCase(gateway_flag)){
				return "error";
			}
			boolean iResult = callback.succeedPayment(orderId, sdkOrderId, payAmount, currencyType, payType, ParseJson.encodeJson(paramMap));
			if (iResult)
                return ok;
		} catch (Exception e) {
            logger.error("", e);
        }
        return "error";
    }
	
	private boolean check360Sig(Map<String, String> paramMap, QiHu360SDKInfo configInfo)
			throws Exception {
		String secretkey = configInfo.getSecretKey();
		String sign = paramMap.get("sign");

		Set<String> params = paramMap.keySet();
		List<String> sortedParams = new ArrayList<String>(params);
		Collections.sort(sortedParams);
		StringBuilder sb = new StringBuilder();
		for (String paramKey : sortedParams) {
			if (paramKey.equals("sign") || paramKey.equals("sign_return")) {
				continue;
			}
			String value = paramMap.get(paramKey);
			if (value != null && !value.isEmpty()) {
				if (sb.length() > 0) {// not first one
					sb.append("#");
				}
				sb.append(value);
			}
		}

		String paramStr = sb.toString() + "#" + secretkey;
		String mySign = MD5Util.MD5(paramStr);

        return StringUtil.equalsStringIgnoreCase(mySign, sign);
	}
	
}
