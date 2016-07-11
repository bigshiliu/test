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
import com.hutong.supersdk.sdk.modeltools.sinatools.SinaToolsLoginRet;
import com.hutong.supersdk.sdk.modeltools.sinatools.SinaToolsPayRet;
import com.hutong.supersdk.sdk.modeltools.sinatools.SinaToolsSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andSinaTools")
public class AndSinaTools implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndSinaTools";

	private static final Log logger = LogFactory.getLog(AndSinaTools.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return SinaToolsSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		/**
		 * 四组随机密钥
		 * a6fc596f-88f2-4d67-8c0b-39e4a9e2d7ca 
         * e3608ffd-3e40-4879-87d2-a8efd61c04a1 
         * 7794ad7e-4d9d-4871-b509-6cbd7e03613c 
         * e3d19141-e776-46d4-a9f3-1b2743f5b1b0
		 */
		logger.debug(this.getSDKId() + " verifyUser. start!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            SinaToolsSDKInfo configInfo = (SinaToolsSDKInfo) config;
            String tag = input.getExtraKey("tag");
			String tagid = input.getExtraKey("tagid");
			String appid = configInfo.getAppid();
			String version = input.getExtraKey("version");
			String imei = input.getExtraKey("imei");
			String channelkey = input.getExtraKey("channelkey");
            //TODO 重新书写签名生成算法
			String r = "0";
			String secretkey = "a6fc596f-88f2-4d67-8c0b-39e4a9e2d7ca";
			
			//组装签名sign字串
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("tag", tag);
			paramMap.put("tagid", tagid);
			paramMap.put("appid", appid);
			paramMap.put("version", version);
			paramMap.put("imei", imei);
			paramMap.put("channelkey", channelkey);
			paramMap.put("r", r);
			
			logger.debug(this.getSDKId() + " verifyUser. paramMap:" + paramMap.toString());
			
			String sign = MD5Util.MD5(SignUtil.createSignString(paramMap, null, true, "") + secretkey);
			
			logger.debug(this.getSDKId() + " verifyUser. QueryUrl sign:" + sign);
			
			String url = configInfo.getRequestUrl();
			String urlStr = url + "?tag=" + tag + "&tagid=" + tagid + 
					"&appid=" + appid + "&version=" + version + 
					"&imei=" + imei + "&channelkey=" + channelkey + 
					"&r=" + r + "&sign=" + sign;
			
			logger.debug(this.getSDKId() + " verifyUser. url:" + urlStr);
			
			String result = HttpUtil.get(urlStr);
			
			logger.debug(this.getSDKId() + " verifyUser. result:" + result);
			if(!StringUtils.isEmpty(result)){
				SinaToolsLoginRet loginRet = ParseJson.getJsonContentByStr(result, SinaToolsLoginRet.class);
				if(loginRet != null && 1 == loginRet.getCode()){
					String devicetype = input.getExtraKey("devicetype");
					String openid = input.getExtraKey("openid");
					String uid = input.getSdkUid();
					String token = input.getSDKAccessToken();
					//继续使用上面的参数map,添加额外的参数
					paramMap.put("devicetype", devicetype);
					paramMap.put("openid", openid);
					paramMap.put("token", token);
					//剔除掉version字段
					paramMap.remove("version");
					
					sign = MD5Util.MD5(SignUtil.createSignString(paramMap, null, true, "") + secretkey);
					
					logger.debug(this.getSDKId() + " verifyUser. CheckUser sign:" + sign);
					
					urlStr = loginRet.getData().get("url") + "?devicetype=" + devicetype +
					"&imei=" + imei + "&r=" + r + "&tag=" + tag + 
					"&tagid=" + tagid + "&appid=" + appid + 
					"&channelkey=" + channelkey + "&openid=" + openid + 
					"&token=" + token + "&sign=" + sign;
					
					logger.debug(this.getSDKId() + " verifyUser. CheckUser url:" + urlStr);
					
					result = HttpUtil.get(urlStr);
					
					if(!StringUtils.isEmpty(result)){
						Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
						if(resultMap != null && "1".equals(String.valueOf(resultMap.get("code")))){
							ret.setSdkUid(uid);
							ret.setSdkAccessToken(token);
							return ret.success();
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        ret.setErrorMsg(this.getSDKId() + " verifyUser is Error");
        return ret.fail();
    }

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. paramMap:" + paramMap.toString());
		SinaToolsPayRet payRet = new SinaToolsPayRet();
        String mySign = "";
        try {
            SinaToolsSDKInfo configInfo = (SinaToolsSDKInfo) config;
            String status = paramMap.get("status");
			if("1".equals(status)){
				String username = paramMap.get("username");
				String cjordernum = paramMap.get("cjordernum");
				String cpordernum = paramMap.get("cpordernum");
				String paytype = paramMap.get("paytype");
				//单位为分
				int amount = Integer.parseInt(paramMap.get("amount"));
				String gameserver = paramMap.get("gameserver");
				String paytime = paramMap.get("paytime");
				String gamename = paramMap.get("gamename");
				String errdesc = paramMap.get("errdesc");
				String signkey = configInfo.getAppsecret();
				String sign = paramMap.get("sign");
				
				String mySignStr = username + "|" + cjordernum + "|" + cpordernum + "|" + 
				status + "|" + paytype + "|" + amount + "|" + gameserver + "|"	+ errdesc + "|" +
				paytime + "|" + gamename + "|" + signkey;
				
				logger.debug(this.getSDKId() + " payCallBack. mySignStr:" + mySignStr);
				
				mySign = MD5Util.MD5(mySignStr);
				
				logger.debug(this.getSDKId() + " payCallBack. mySign:" + mySign + " sign:" + sign);
				if(StringUtil.equalsStringIgnoreCase(mySign, sign)){
					boolean iResult = callback.succeedPayment(cpordernum, cjordernum, amount *100, "RMB",
                            paytype, ParseJson.encodeJson(paramMap.toString()));
					if(iResult){
						payRet.setCode("200");
						payRet.setMsg("处理成功");
						payRet.setSign(mySign);
						return payRet.toString();
					}
				}
			}
			payRet.setCode("5001");
			payRet.setMsg("处理失败");
			payRet.setSign(mySign);
			return payRet;
		} catch (Exception e) {
			logger.error("", e);
			payRet.setCode("5001");
			payRet.setMsg("处理失败");
			payRet.setSign(mySign);
			return payRet;
		}
	}

}
