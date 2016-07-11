package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.HashMap;
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
import com.hutong.supersdk.sdk.modeltools.pengyouwan.PengYouWanPayRet;
import com.hutong.supersdk.sdk.modeltools.pengyouwan.PengYouWanSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andPengYouWanSDK")
public class AndPengYouWanSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	private static final String SDK_ID = "AndPengYouWan";
	
	private final static Log logger = LogFactory.getLog(AndPengYouWanSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return PengYouWanSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. start! paramMap:" + paramMap.toString());
		PengYouWanPayRet payRet = new PengYouWanPayRet();
		try {
			PengYouWanSDKInfo configInfo = (PengYouWanSDKInfo) config;
			//进行延签操作
			String sign = paramMap.get("sign");
			//CP订单号
			String cp_orderid = paramMap.get("cp_orderid");
			//渠道订单号
			String ch_orderid = paramMap.get("ch_orderid");
			//订单金额元
			String amount = paramMap.get("amount");
			String apiSecret = configInfo.getApiSecret();
			String mySignStr = apiSecret + cp_orderid + ch_orderid + amount;
			String mySign = MD5Util.MD5(mySignStr);
			if(StringUtil.equalsStringIgnoreCase(sign, mySign)){
				boolean iResult = callback.succeedPayment(cp_orderid, ch_orderid, Double.parseDouble(amount), "RMB", "", ParseJson.encodeJson(paramMap));
				if(iResult){
					payRet.setAck(200);
					payRet.setMsg("处理成功");
					return ParseJson.encodeJson(payRet);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		payRet.setAck(-1);
		payRet.setMsg("处理失败");
		return ParseJson.encodeJson(payRet);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. start!");
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            PengYouWanSDKInfo configInfo = (PengYouWanSDKInfo) config;
            String Token = input.getSDKAccessToken();
			String Uid = input.getSdkUid();
			String tid = MD5Util.MD5_16(Uid + Token);
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("tid", tid);
			paramMap.put("token", Token);
			paramMap.put("uid", Uid);
			
			String url = configInfo.getRequestUrl();
			String result = HttpUtil.postJson(url, ParseJson.encodeJson(paramMap));
			if(!StringUtils.isEmpty(result)){
				Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				if(resultMap != null && 200 == Integer.parseInt(String.valueOf(resultMap.get("ack")))){
					ret.setSdkUid(Uid);
					ret.setSdkAccessToken(Token);
					return ret.success();
				}
				logger.debug(this.getSDKId() + " verifyUser is Error! ErrorMsg:" + (resultMap != null ? resultMap.get("Msg") : null));
			}
		} catch (Exception e) {
            logger.error("", e);
		}
        ret.setErrorMsg(this.getSDKId() + " verifyUser is Error!");
        return ret.fail();
    }

}
