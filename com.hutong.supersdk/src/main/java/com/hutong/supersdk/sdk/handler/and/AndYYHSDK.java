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
import com.hutong.supersdk.sdk.modeltools.yingyonghu.YYHData;
import com.hutong.supersdk.sdk.modeltools.yingyonghu.YYHObj;
import com.hutong.supersdk.sdk.modeltools.yingyonghu.YingYongHuiSDKInfo;
import com.hutong.supersdk.sdk.utils.yingyonghui.YYHCpTransSyncSignValid;
import org.springframework.util.StringUtils;

@Component("andYYHSDK")
public class AndYYHSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndYingYongHui";
	
	private static final Log logger = LogFactory.getLog(AndYYHSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YingYongHuiSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack Start !!!");
		logger.debug("paramMap:" + paramMap.toString());

		String success = "success";
		String failed = "failed";
		try {
			YingYongHuiSDKInfo configInfo = (YingYongHuiSDKInfo) config;
			String transdata = paramMap.get("transdata");
			String sign = paramMap.get("sign");
			String key = configInfo.getAppKey();
			
			if(!YYHCpTransSyncSignValid.validSign(transdata, sign, key)){
				logger.error("YYHSDK check Sign failed.");
				return failed;
			}
			
			YYHObj yyhObj = ParseJson.getJsonContentByStr(transdata, YYHObj.class);
			
			if (yyhObj == null || yyhObj.getResult() != 0)
				return failed;
			
			String platformOrderId = yyhObj.getTransid();
            platformOrderId = StringUtils.isEmpty(platformOrderId) ? "YYH order id is empty" : platformOrderId;

			String orderId = yyhObj.getExorderno();
			float amount = Float.parseFloat(String.valueOf(yyhObj.getMoney())) * 1.0f / 100f;
			String payWay = String.valueOf(yyhObj.getTranstype());
			
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, amount, "RMB", payWay, ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
        return failed;
    }

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start!!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            YingYongHuiSDKInfo configInfo = (YingYongHuiSDKInfo) config;
            String token = input.getSDKAccessToken();
            String appId = configInfo.getLoginId();
            String appKey = configInfo.getLoginKey();
            String url = configInfo.getRequestUrl();

            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("login_id", appId);
            paramMap.put("login_key", appKey);
            paramMap.put("ticket", token);

            String result = HttpUtil.postForm(url, paramMap);
			logger.debug(this.getSDKId() + " HttpPost result:" + result);
            Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !"0".equals(String.valueOf(resultMap.get("status")))){
				ret.setErrorMsg(this.getSDKId() + " verifyUser Login Error!");
				return ret.fail();
			}
            YYHData yyhData = ParseJson.getJsonContentByStr(ParseJson.encodeJson(resultMap.get("data")), YYHData.class);
            if (yyhData != null) {
                ret.setSdkUid(String.valueOf(yyhData.getUser_id()));
                ret.setExtra("user_name", yyhData.getUser_name());
                ret.setSdkAccessToken(yyhData.getTicket());
                return ret.success();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        ret.setErrorMsg(this.getSDKId() + " verifyUser Login Error!");
        return ret.fail();
    }

}
