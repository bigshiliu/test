package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLDecoder;
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
import com.hutong.supersdk.sdk.modeltools.muzhiwan.MuZhiWanSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andMZWSDK")
public class AndMuZhiWanSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public static final String SDK_ID = "AndMuZhiWan";
	
	private final Log logger = LogFactory.getLog(AndMuZhiWanSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return MuZhiWanSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "SUCCESS";
		try {
			MuZhiWanSDKInfo configInfo = (MuZhiWanSDKInfo) config;
			String platformId = paramMap.get("orderID");
			if(null == platformId || platformId.equals("")){
				platformId = "MZW order id is empty";
			}

			String signKey = configInfo.getSignKey();
			if (!checkMZWSig(paramMap, signKey)) {
				logger.error("sign is error!");
				return "sign is error!";
			}
			String orderId = paramMap.get("extern");
			float payAmount = Float.parseFloat(paramMap.get("money"));
			String currencyType = "RMB";
			String payType = "";

            boolean iResult = callback.succeedPayment(orderId, platformId, payAmount, currencyType, payType, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
			else
				return "fail";
		} catch (Exception e) {
			logger.error("",e);
			return "failed. Got exception";
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            MuZhiWanSDKInfo configInfo = (MuZhiWanSDKInfo) config;
            String platformSessionId = input.getSDKAccessToken();
            String url = configInfo.getRequestUrl();
            String appKey = configInfo.getAppKey();
            Map<String, Object> userMap;
            String result = HttpUtil.get(url + "?token=" + platformSessionId + "&appkey=" + appKey);
			logger.info(result);
			result = result.replaceAll("'", "\"");
			@SuppressWarnings("unchecked")
			Map<String, Map<String, Object>> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !"1".equals(String.valueOf(resultMap.get("code")))){
				ret.setErrorMsg(this.getSDKId() + " Login Error");
				return ret.fail();
			}
			userMap = resultMap.get("user");
            ret.setSdkUid(userMap.get("uid") + "");
            ret.setSdkAccessToken(platformSessionId);
            return ret.success();
        } catch (Exception e) {
            logger.error("MuZhiWan Verify User Error.", e);
            ret.setErrorMsg(this.getSDKId() + " Login Error");
            return ret.fail();
        }
	}
	
	public  boolean checkMZWSig(Map<String, String> requestMap,String signKey) throws Exception {

		logger.debug(requestMap.toString());

		String appKey = requestMap.get("appkey");
		String orderId = requestMap.get("orderID");

		String productName = requestMap.get("productName") ;
		//productName="60元宝";
		productName = URLDecoder.decode(productName, "gbk");
		String productDesc = requestMap.get("productDesc");
		productDesc = URLDecoder.decode(productDesc, "gbk");
		String productID = requestMap.get("productID");

		String money = requestMap.get("money");

		String uid = requestMap.get("uid");

		String extern = requestMap.get("extern");

		String signStr = appKey + orderId + productName + productDesc + productID + money + uid + extern + signKey;

		String mySign = MD5Util.MD5(signStr).toLowerCase();
		String sign = requestMap.get("sign").replace("\n", "");

        return StringUtil.equalsStringIgnoreCase(mySign, sign);
	}
}
