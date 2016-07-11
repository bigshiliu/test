package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLEncoder;
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
import com.hutong.supersdk.sdk.modeltools._4399._4399LoginRet;
import com.hutong.supersdk.sdk.modeltools._4399._4399PayBackRet;
import com.hutong.supersdk.sdk.modeltools._4399._4399SDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("and_4399SDK")
public class And_4399SDK implements IPayCallBackSDK, IVerifyUserSDK {
	
	private static final String SDK_ID = "And4399";
	
	private final static Log logger = LogFactory.getLog(And_4399SDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return _4399SDKInfo.class;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		_4399SDKInfo configInfo = (_4399SDKInfo) config;
		try {
			String uid = input.getSdkUid();
			String token = input.getSDKAccessToken();
			String url = configInfo.getRequestUrl();
			
			String urlStr = url + "?state=" + URLEncoder.encode(token, "utf-8") + "&uid=" + uid;
			String result = HttpUtil.get(urlStr);
			_4399LoginRet loginRet = ParseJson.getJsonContentByStr(result, _4399LoginRet.class);
			
			if(null != loginRet){
				//获取code进行判断
				String code = loginRet.getCode();
				//当code为100表示成功
				if(null != code && !"".equals(code) && "100".equals(code)){
					ret.setSdkUid(loginRet.getResult().get("uid"));
					ret.setSdkAccessToken(token);
					ret.success();
					return ret;
				}
				//失败时,将返回的ErrorMsg输出
				logger.error(this.getSDKId() + " verifyUser. Check User Error! ErrorMsg:" + loginRet.getMessage());
			}
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Check User Error!");
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " verifyUser. Check User Error!");
			return ret;
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		
		_4399SDKInfo configInfo = (_4399SDKInfo) config;
		_4399PayBackRet ret = new _4399PayBackRet();
		//4399订单号
		String orderid = paramMap.get("orderid");
		//用户id
		String uid = String.valueOf(paramMap.get("uid"));
		//充值金额
		String money = paramMap.get("money");
		//游戏币数量
		String gamemoney = String.valueOf(paramMap.get("gamemoney"));
		//服务区号
		String serverid = String.valueOf(paramMap.get("serverid"));
		//保留字段
		String mark = paramMap.get("mark");
		//游戏角色id
		String roleid = String.valueOf(paramMap.get("roleid"));
		//时间戳
		String time = String.valueOf(paramMap.get("time"));
		//secrect 通信密钥
		String secrect = configInfo.getSecrect();
		//sign
		String sign = paramMap.get("sign");
		
		//计算签名串
		String mySignStr = orderid + uid + money + gamemoney;
		if(null != serverid &&!"".equals(serverid)){
			mySignStr += serverid;
		}
		mySignStr = mySignStr + secrect + mark;
 		if(null != roleid && !"".equals(roleid)){
			mySignStr += roleid;
		}
		mySignStr += time;
		
		ret.setMoney(paramMap.get("money"));
		ret.setGamemoney(gamemoney);
		
		String mySign = MD5Util.MD5(mySignStr);
		if(StringUtil.equalsStringIgnoreCase(sign, mySign)){
			boolean iResult = callback.succeedPayment(mark, orderid, Double.parseDouble(money), "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult){
				ret.setStatus("2");
				ret.setCode("");
				ret.setMsg("回调请求成功!");
				return ParseJson.encodeJson(ret);
			}
		}
		ret.setStatus("3");
		ret.setCode("sign_error");
		ret.setMsg("验证签名失败!");
		return ParseJson.encodeJson(ret);
	}

}
