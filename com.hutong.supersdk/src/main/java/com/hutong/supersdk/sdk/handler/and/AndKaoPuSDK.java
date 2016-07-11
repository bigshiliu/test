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
import com.hutong.supersdk.sdk.modeltools.kaopu.KaoPuSDKInfo;
import com.hutong.supersdk.sdk.modeltools.kaopu.PayCallbackRet;
import com.hutong.supersdk.sdk.modeltools.kaopu.VerifyUserRet;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andKaoPuSDK")
public class AndKaoPuSDK implements IVerifyUserSDK, IPayCallBackSDK {

	private static final String SDK_ID = "AndKaoPuTools";

	private static final Log logger = LogFactory.getLog(AndKaoPuSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return KaoPuSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		PayCallbackRet ret = new PayCallbackRet();
        KaoPuSDKInfo configInfo = (KaoPuSDKInfo) config;
        try {
			String status = paramMap.get("status");
			if (null != status && "1".equals(status)) {
				String username = paramMap.get("username");
				String kpordernum = paramMap.get("kpordernum");
				String ywordernum = paramMap.get("ywordernum");
				String paytype = paramMap.get("paytype");
				String amount = String.valueOf(paramMap.get("amount"));
				String gameserver = String.valueOf(paramMap.get("gameserver"));
				// 充值失败错误吗,成功为空
				String errdesc = paramMap.get("errdesc");
				logger.debug(this.getSDKId() + " payCallBack ErrDesc:" + errdesc);
				String paytime = paramMap.get("paytime");
				String gamename = paramMap.get("gamename");
				String sign = paramMap.get("sign");
				String signKey = configInfo.getSecreyKey();
				// 组装签名串
				String mySignStr = username + "|" + kpordernum + "|" + ywordernum + "|" + status + "|" + paytype + "|"
						+ amount + "|" + gameserver + "|" + errdesc + "|" + paytime + "|" + gamename + "|" + signKey;
				logger.debug("mySignStr:" + mySignStr);
				String mySign = MD5Util.MD5(mySignStr);
				logger.debug("sign:" + sign + " mySign:" + mySign);
				
				double realAmount = Double.parseDouble(amount) / 100;
				if(!"".equals(sign) && !"".equals(mySign) && sign.equalsIgnoreCase(mySign)){
					boolean iResult = callback.succeedPayment(ywordernum, kpordernum, realAmount, "RMB",
							paytype, ParseJson.encodeJson(paramMap));
					if(iResult){
						ret.setCode("1000");
						ret.setMsg("处理成功");
						return ret.generateSign(configInfo.getSecreyKey());
					}else{
						ret.setCode("1005");
						ret.setMsg("系统异常");
						return ret.generateSign(configInfo.getSecreyKey());
					}
				}else{
					ret.setCode("1002");
					ret.setMsg("验签失败");
					return ret.generateSign(configInfo.getSecreyKey());
				}
			} else {
				ret.setCode("1004");
				ret.setMsg("订单失败");
				return ret.generateSign(configInfo.getSecreyKey());
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.setCode("1005");
			ret.setMsg("系统异常");
			return ret.generateSign(configInfo.getSecreyKey());
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		/**
		 * 四组随机秘钥： 18257284-7F5D-348D-AB09-299E5B7DD997
		 * 655A957D-157D-7C21-E3A7-9CAAFA835318
		 * F467CA93-D550-346D-6BCB-173995F7C83A
		 * BD32817A-99F9-2E26-5B33-15208F7B360A
		 */
		logger.debug(this.getSDKId() + " vierfyUser start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		KaoPuSDKInfo configInfo = (KaoPuSDKInfo) config;
		try {
			// 请求基本参数获取
			String devicetype = "android";
			String imei = input.getExtraKey("imei");
            //TODO 修改参数至数据库,重新修改签名算法
			String r = "2";
//			String privateKey = "18257284-7F5D-348D-AB09-299E5B7DD997";
//			String privateKey = "655A957D-157D-7C21-E3A7-9CAAFA835318";
			String privateKey = "F467CA93-D550-346D-6BCB-173995F7C83A";
//			String privateKey = "BD32817A-99F9-2E26-5B33-15208F7B360A";
			String tagid = input.getExtraKey("tagid");
			String appid = configInfo.getAppId();
			String tag = configInfo.getAppKey();
			String channelkey = input.getExtraKey("channelkey");
			String openId = input.getSdkUid();
			String token = input.getSDKAccessToken();
			String version = input.getExtraKey("version");
			// 拼接sign并进行md5加密
			String signStr = appid + channelkey + imei + r + tag + tagid + version + privateKey;
			String mySign = MD5Util.MD5(signStr);
			// 请求Url
			String url = configInfo.getRequestUrl();
			// 讲请求参数进行拼接 k1=v1&k2=v2&...
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("tag", tag);
			params.put("tagid", tagid);
			params.put("appid", appid);
			params.put("version", version);
			params.put("imei", imei);
			params.put("channelkey", channelkey);
			params.put("r", r);
			params.put("sign", mySign);
			
			logger.debug(this.getSDKId() + " vierfyUser params:" + params.toString());
			String paramStr = SignUtil.createSignData(params, null, true, "&");
			logger.debug(this.getSDKId() + " vierfyUser paramStr:" + paramStr);
			String httpGetUrl = url + "?" + paramStr;
			logger.debug(this.getSDKId() + " vierfyUser httpGetUrl:" + httpGetUrl);
			String result = HttpUtil.get(httpGetUrl);
			logger.debug(this.getSDKId() + " vierfyUser result:" + result);
			//处理返回结果,进行转换
			VerifyUserRet resultKaopu = ParseJson.getJsonContentByStr(result, VerifyUserRet.class);
			if (resultKaopu != null && null != resultKaopu.getCode() && "1".equals(resultKaopu.getCode())) {
				//进行靠谱SDK登陆验证
				
				//生成登陆延签
				String myCheSignStr = appid + channelkey + devicetype + imei + openId + r + tag + tagid + token + privateKey;
				String myCheSign = MD5Util.MD5(myCheSignStr);
				//清空参数map.设置登陆参数
				params.clear();
				params.put("devicetype", devicetype);
				params.put("imei", imei);
				params.put("r", r);
				params.put("tag", tag);
				params.put("tagid", tagid);
				params.put("appid", appid);
				params.put("channelkey", channelkey);
				params.put("openid", openId);
				params.put("token", token);
				params.put("sign", myCheSign);
				
				//延签通过后,获取登陆地址
				String checkUrl = String.valueOf(resultKaopu.getData().get("url"));
				String paramCheStr = SignUtil.createSignData(params, null, true, "&");
				String chEHttpGetUrl = checkUrl + "?" + paramCheStr;
				String cheResult = HttpUtil.get(chEHttpGetUrl);
 				Map resultMap = ParseJson.getJsonContentByStr(cheResult, Map.class);
				//获取登陆返回结果
				if(resultMap != null && "1".equals(String.valueOf(resultMap.get("code")))){
					ret.setSdkUid(openId);
					ret.setSdkAccessToken(token);
					return ret.success();
				}
                else if (resultMap != null) {
					ret.setErrorMsg(String.valueOf(resultMap.get("msg")));
					return ret.fail();
				}
                else {
                    ret.setErrorMsg("response error");
                    return ret.fail();
                }
			}
            else if (resultKaopu != null) {
				ret.setErrorMsg(String.valueOf(resultKaopu.getMsg()));
				return ret.fail();
			}
            else {
                ret.setErrorMsg("response error");
                return ret.fail();
            }
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error.");
			return ret.fail();
		}
	}

}
