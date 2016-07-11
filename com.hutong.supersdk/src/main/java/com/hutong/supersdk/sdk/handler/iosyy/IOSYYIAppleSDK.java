package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.InputStream;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.iapple.IApplePayBackRet;
import com.hutong.supersdk.sdk.modeltools.iapple.IAppleSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("iOSYYIAppleSDK")
public class IOSYYIAppleSDK implements IPayCallBackSDK, IVerifyUserSDK {
	
	private static final String SDK_ID = "IOSYYIApple";
	
	private final static Log logger = LogFactory.getLog(IOSYYIAppleSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return IAppleSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser. Start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			IAppleSDKInfo configInfo = (IAppleSDKInfo) config;

			String user_id = input.getSdkUid();
			String session = input.getSDKAccessToken();
			String game_id = configInfo.getGame_id();
			String sercetKey = configInfo.getSercetKey();
			
			/**
			 * i苹果sign生成方式
			 * md5(md5(game_id=xxx&session=xxx&user_id=xxx) +sercetKey)
			 * 先安排字母顺序排名拼接signStr1进行md5加密,
			 * 加密结果拼接sercetKey再进行md5加密
			 */
			String signStr1 = "game_id=" + game_id + "&session=" + session + "&user_id=" + user_id;
			logger.debug(this.getSDKId() + " verifyUser. signStr1:" + signStr1);
			String sign1Md5 = MD5Util.MD5(signStr1);
			logger.debug(this.getSDKId() + " verifyUser. sign1Md5:" + sign1Md5);
			String signStr2 = sign1Md5 + sercetKey;
			logger.debug(this.getSDKId() + " verifyUser. signStr2:" + signStr2);
			String sign2Md5 = MD5Util.MD5(signStr2);
			logger.debug(this.getSDKId() + " verifyUser. _sign:" + sign2Md5);
			
			/**
			 * 组装请求连接
			 */
			String url = configInfo.getRequestUrl();
			String urlStr = url + "?user_id=" + user_id + "&session=" + session + "&game_id=" + game_id + "&_sign=" + sign2Md5;
			logger.debug(this.getSDKId() + " verifyUser. urlStr:" + urlStr);
			
			String result = HttpUtil.get(urlStr);
			logger.debug(this.getSDKId() + " verifyUser. result:" + result);

			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap != null && "1".equals(String.valueOf(resultMap.get("status")))){
				ret.setSdkUid(user_id);
				ret.setSdkAccessToken(session);
				return ret.success();
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        ret.setErrorMsg(this.getSDKId() + " verifyUser. Login Check Error!");
        return ret.fail();
    }

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. Start! paramMap:" + paramMap.toString());
		IApplePayBackRet ret = new IApplePayBackRet();
        String transaction = paramMap.get("transaction");
        try {
            IAppleSDKInfo configInfo = (IAppleSDKInfo) config;
            //i苹果订单号
            //回调状态
			if(1 != Integer.parseInt(paramMap.get("status"))){
                //返回 1 标示成功
                ret.setStatus(1);
				ret.setTranslDO(Long.parseLong(transaction));
				return ret.toString();
			}
			//回调签名
			String _sign = paramMap.get("_sign");
			logger.debug(this.getSDKId() + " payCallBack. Start! _sign:" + _sign);
			//加密key
			String sercetKet = configInfo.getSercetKey();
			//组装签名参数
			Map<String, Object> signMap = SignUtil.convert(paramMap);
			/**
			 * i苹果sign生成方式
			 * md5(md5(game_id=xxx&session=xxx&user_id=xxx) +sercetKey)
			 * 先安排字母顺序排名拼接mySignStr1进行md5加密,
			 * 加密结果拼接sercetKey再进行md5加密
			 */
			String mySignStr1 = SignUtil.createSignData(signMap, new String[]{"_sign"}, true, "&");
			String mySignStr1Md5 = MD5Util.MD5(mySignStr1);
			String mySignStr2 = mySignStr1Md5 + sercetKet;
			String mySignStr2Md5 = MD5Util.MD5(mySignStr2);

			//验证签名,大小写敏感
			if(!StringUtil.equalsStringIgnoreCase(_sign, mySignStr2Md5)){
				ret.setStatus(1);
				ret.setTranslDO(Long.parseLong(transaction));
				return ret.toString();
			}
			
			//透传参数 superSDK orderId
			String gameExtend = paramMap.get("gameExtend");
			//支付类型 
			String payType = paramMap.get("payType");
			//货币类型
			String currency = paramMap.get("currency");
			//支付金额
			double amount = Double.parseDouble(paramMap.get("amount"));
			
			//回调请求
			boolean iResult = callback.succeedPayment(gameExtend, transaction, amount, currency, payType, ParseJson.encodeJson(paramMap));
			if(iResult){
				ret.setStatus(0);
				ret.setTranslDO(Long.parseLong(transaction));
				return ret.toString();
			}else{
				ret.setStatus(1);
				ret.setTranslDO(Long.parseLong(transaction));
				return ParseJson.encodeJson(ret);
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.setStatus(1);
			ret.setTranslDO(Long.parseLong(transaction));
			return ret.toString();
		}
	}
}
