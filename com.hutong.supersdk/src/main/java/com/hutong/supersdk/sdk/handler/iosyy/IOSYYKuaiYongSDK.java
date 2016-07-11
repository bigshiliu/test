package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.util.Arrays;
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
import com.hutong.supersdk.sdk.modeltools.kuaiyong.KuaiYongSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.kuaiyong.Base64;
import com.hutong.supersdk.sdk.utils.kuaiyong.RSAEncrypt;
import com.hutong.supersdk.sdk.utils.kuaiyong.RSASignature;
import com.hutong.supersdk.sdk.utils.kuaiyong.Util;
import org.springframework.util.StringUtils;

@Component("iOSYYKuaiYongSDK")
public class IOSYYKuaiYongSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYKuaiYong";
	
	private final static Log logger = LogFactory.getLog(IOSYYKuaiYongSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return KuaiYongSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "success";
		String fail = "fail";
		try {
			KuaiYongSDKInfo configInfo = (KuaiYongSDKInfo) config;
			Map<String, String> transformedMap = new HashMap<String, String>();
			String notify_data = paramMap.get("notify_data");
			transformedMap.put("notify_data", notify_data);
			String orderid = paramMap.get("orderid");
			transformedMap.put("orderid", orderid);
			String sign = paramMap.get("sign");
			transformedMap.put("sign", sign);
			String dealseq = paramMap.get("dealseq");
			transformedMap.put("dealseq", dealseq);
			String uid = paramMap.get("uid");
			transformedMap.put("uid", uid);
			String subject = paramMap.get("subject");
			transformedMap.put("subject", subject);
			String v = paramMap.get("v");
			transformedMap.put("v", v);
			logger.debug("transformedMap is : " + transformedMap.toString());
			// 将sign除外的参数按自然升序排序后组装成验签数据
			String signData = Util.getSignData(transformedMap);
			logger.debug("signData is : " + signData);
			String rsaPublicKey = configInfo.getPubKey();
			logger.debug("rsaPublicKey is : " + rsaPublicKey);
			if(!RSASignature.doCheck(signData, sign, rsaPublicKey, "utf-8")){
				logger.error("RSASignature is failed");
				return fail;
			}
			logger.debug("RSASignature is success");
			// "RSA验签成功，数据可信
			RSAEncrypt rsaEncrypt = new RSAEncrypt();
			rsaEncrypt.loadPublicKey(rsaPublicKey);
			byte[] dcDataStr = Base64.decode(notify_data);
			byte[] plainData;
			plainData = rsaEncrypt.decrypt(rsaEncrypt.getPublicKey(), dcDataStr);
			String notifyData = new String(plainData, "UTF-8");
			logger.debug("plainData is " + Arrays.toString(plainData));
			logger.debug("notifyData is " + notifyData);

			Map<String, String> plainMap = new HashMap<String, String>();
			String[] strArray = notifyData.split("&");
			for (String str : strArray) {
				String[] keyValueArray = str.split("=");
				String key = keyValueArray[0];
				String value = "";
				if (keyValueArray.length >= 2) {
					value = keyValueArray[1];
				}
				plainMap.put(key, value);
			}

			logger.debug("plainMap is " + plainMap.toString());
			dealseq = plainMap.get("dealseq");
			String fee = plainMap.get("fee");
			String payresult = plainMap.get("payresult");
			if (!payresult.equals("0")) {
				logger.error("order failed payResult=" + payresult);
				return fail;
			}

			String platformId = orderid;
            platformId = StringUtils.isEmpty(platformId) ? "kuaiyong order id is empty" : platformId;
			// 取得充值人民币
			double CostMoney = Double.parseDouble(fee);
			boolean iResult = callback.succeedPayment(dealseq, platformId, CostMoney, "RMB",
                    payresult, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
        return fail;
    }

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            KuaiYongSDKInfo configInfo = (KuaiYongSDKInfo) config;
            String token = input.getSDKAccessToken();
            String appKey = configInfo.getAppKey();
            String content = appKey.toLowerCase() + token.toLowerCase();
            String mySign = MD5Util.MD5(content);
            String url = configInfo.getRequestUrl();
            String getInfo = url + "?tokenKey=" + token + "&sign=" + mySign;
            String result = HttpUtil.get(getInfo);
            Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
            if (resultMap == null || !"0".equals(String.valueOf(resultMap.get("code")))) {
                ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
                return ret.fail();
            }
            Map dataMap = ParseJson.getJsonContentByStr(ParseJson.encodeJson(resultMap.get("data")), Map.class);
            if (dataMap != null) {
                ret.setSdkUid(String.valueOf(dataMap.get("guid")));
                ret.setExtra("username", String.valueOf(dataMap.get("username")));
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            logger.error("SDK response error. result=" + result);
        } catch (Exception e) {
            logger.error("", e);
        }
        return ret.fail();
    }

}
