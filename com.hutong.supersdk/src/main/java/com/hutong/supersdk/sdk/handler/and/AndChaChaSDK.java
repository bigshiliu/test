package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.chacha.ChaChaSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("andChaChaSDK")
public class AndChaChaSDK implements IPayCallBackSDK, IVerifyUserSDK, ICheckOrderSDK {
	
	private static final String SDK_ID = "AndChaChaTools";
	
	private final static Log logger = LogFactory.getLog(AndChaChaSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return ChaChaSDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser Start!!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		ChaChaSDKInfo configInfo = (ChaChaSDKInfo) config;
		try {
			String game_uin = input.getSdkUid();
			String appid = configInfo.getAppid();
			String token = input.getSDKAccessToken();
			String t = String.valueOf(System.currentTimeMillis());
			String serverKey = configInfo.getServerKey();
			String url = configInfo.getRequestUrl();
			String signStr = game_uin + appid + t + serverKey;
			logger.debug("signStr:" + signStr);
			String sign = MD5Util.MD5(signStr);
			String urlStr = url + "?game_uin=" + game_uin + "&appid=" + appid + "&token=" + token + "&t=" + t + "&sign=" +sign;
			logger.debug("urlStr" + urlStr);
			String result = HttpUtil.get(urlStr);
			if(null != result && !"".equals(result)){
				if("true".equals(result)){
					ret.setSdkUid(game_uin);
					ret.setSdkAccessToken(token);
					return ret.success();
				}
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
				return ret;
			}else{
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
				return ret;
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
			ret.fail();
			return ret;
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		String SUCCESS = "success";
		String FAILURE = "failure";
		ChaChaSDKInfo configInfo = (ChaChaSDKInfo) config;
		try {
			String status = paramMap.get("status");
			if(!"".equals(status) && "1".equals(status)){
				String trade_no = paramMap.get("trade_no");
				String serialNumber = paramMap.get("serialNumber");
				String money = paramMap.get("money");
				String t = paramMap.get("t");
				String sign = paramMap.get("sign");
				String server_key = configInfo.getServerKey();
				String signStr = serialNumber + money + status + t + server_key;
				logger.debug("signStr:" + signStr);
				String mySign = MD5Util.MD5(signStr);
				if(null != sign && !"".equals(sign) && !"".equals(mySign) && sign.equalsIgnoreCase(mySign)){
					boolean iResult = callback.succeedPayment(serialNumber, trade_no,
							Double.parseDouble(money), "RMB", "", ParseJson.encodeJson(paramMap));
					if (iResult)
						return SUCCESS;
					return FAILURE;
				}
				return FAILURE;
			}
			return FAILURE;
		} catch (Exception e) {
			logger.error("", e);
			return FAILURE;
		}
	}

	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
		logger.info(this.getSDKId() + " checkOrder. start!");
		ChaChaSDKInfo configInfo = (ChaChaSDKInfo) config;
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		try {
			String serialNumber = pOrder.getOrderId();
			String appid = configInfo.getAppid();
//			String t = jsonData.getExtraKey("pay_time");
			String t = String.valueOf(System.currentTimeMillis() / 1000);
			String server_key = configInfo.getServerKey();
			String sign = MD5Util.MD5(appid + serialNumber + t + server_key);
			String url = configInfo.getCheckOrderUrl();
			
			String urlStr = url + "?serialNumber=" + serialNumber + "&appid=" + appid + "&t=" + t + "&sign=" + sign;
			
			logger.debug(this.getSDKId() + " checkOrder. url:" + urlStr);
			
			String result = HttpUtil.get(urlStr);
			if(!StringUtils.isEmpty(result)){
				logger.debug(this.getSDKId() + " checkOrder. result:" + result);
				String[] restlts = result.split(","); 
				if("success".equals(restlts[0]) && "1".equals(restlts[1])){
					ret.success(pOrder.getSdkOrderId(), pOrder.getOrderAmount(), "RMB", "", ParseJson.encodeJson(result));
					return ret;
				}
			}
			ret.fail();
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			return ret;
		}
	}

}
