package com.hutong.supersdk.sdk.handler.iosyy;

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
import com.hutong.supersdk.sdk.modeltools.baidu.BaiduPayBack;
import com.hutong.supersdk.sdk.modeltools.baidu.BaiduSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("iOSYYBaiduSDK")
public class IOSYYBaiduSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "IOSYYBaiDu";

	private final static Log logger = LogFactory.getLog(IOSYYBaiduSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return BaiduSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. Start! paramMap:" + paramMap);
		BaiduPayBack ret = new BaiduPayBack();
		try {
			/**
			 * CooOrderSerial商户订单号 百度91SDK订单号
			 * Note支付描述 SuperSDK订单号
			 */
			BaiduSDKInfo configInfo = (BaiduSDKInfo) config;
			//订单状态 1为成功
			String PayStatus = paramMap.get("PayStatus");
			if((!StringUtils.isEmpty(PayStatus)) && "1".equals(PayStatus)){
				String AppId = paramMap.get("AppId");
				String Act = "1";
				String ProductName = paramMap.get("ProductName");
				String ConsumeStreamId = paramMap.get("ConsumeStreamId");
				String CooOrderSerial = paramMap.get("CooOrderSerial");
				String Uin = paramMap.get("Uin");
				String GoodsId = paramMap.get("GoodsId");
				String GoodsInfo = paramMap.get("GoodsInfo");
				String GoodsCount = paramMap.get("GoodsCount");
				String OriginalMoney = paramMap.get("OriginalMoney");
				String OrderMoney = paramMap.get("OrderMoney");
				String Note = paramMap.get("Note");
				String CreateTime = paramMap.get("CreateTime");
				String AppKey = configInfo.getAppKey();
				String Sign = paramMap.get("Sign");
				String mySignStr = AppId + Act + ProductName + ConsumeStreamId + CooOrderSerial + Uin + GoodsId + GoodsInfo + GoodsCount
						+ OriginalMoney + OrderMoney + Note + PayStatus + CreateTime +  AppKey;
                String mySign = MD5Util.MD5(mySignStr); 
                logger.debug(SDK_ID + " payCallBack. Sign:" + Sign + " mySign:" + mySign);
                if(StringUtil.equalsStringIgnoreCase(Sign, mySign)){
                	boolean iResult = callback.succeedPayment(CooOrderSerial, ConsumeStreamId, Double.parseDouble(OrderMoney),
                            "RMB", "", ParseJson.encodeJson(paramMap));
                	if(iResult){
                		ret.setErrorCode("1");
                		ret.setErrorDesc("接受成功");
                		return ret.toString();
                	}
                }
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        ret.setErrorCode("0");
        ret.setErrorDesc("接受失败");
        return ret.toString();
    }

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. Start!");
        SDKVerifyRet ret = new SDKVerifyRet();
        try {
            BaiduSDKInfo configInfo = (BaiduSDKInfo) config;
            String AppId = configInfo.getAppId();
			String AppKey = configInfo.getAppKey();
			String Act = "4";
			String Uin = input.getSdkUid();
			String SessionId = input.getSDKAccessToken();
			String SignStr = AppId + Act + Uin + SessionId + AppKey;
			String Sign = MD5Util.MD5(SignStr);
			String url = configInfo.getRequestUrl() + "?AppId=" + AppId + "&Act=" + Act + "&Uin=" + Uin + "&Sign=" + Sign + "&SessionId=" + SessionId;
			logger.debug(this.getSDKId() + " verifyUser. url:" + url);
			String result = HttpUtil.get(url);
			logger.debug(this.getSDKId() + " verifyUser. result:" + result);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && "1".equals(String.valueOf(resultMap.get("ErrorCode")))) {
                ret.setSdkUid(Uin);
                ret.setSdkAccessToken(SessionId);
                return ret.success();
			}
            logger.error(this.getSDKId() + " verifyUser. Login Check Error! ErrMsg:" +
                    (resultMap != null ? resultMap.get("ErrorDesc") : null));
            ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Check Error! ErrMsg:" +
                    (resultMap != null ? resultMap.get("ErrorDesc") : null));
		} catch (Exception e) {
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Login check error!");
		}
        return ret.fail();
    }
}
