package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.htc.HTCSDKInfo;
import com.hutong.supersdk.sdk.utils.htc.RSASignature;

@Component("andHTCSDK")
public class AndHTCSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final String SDK_ID = "AndHTC";

	private final static Log logger = LogFactory.getLog(AndHTCSDK.class);

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return HTCSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. start! paramMap:" + paramMap.toString());
		String success = "success";
		String fail = "fail";
		try {
			HTCSDKInfo configInfo = (HTCSDKInfo) config;
			String inputStr = new String(IOUtils.toByteArray(servletInputStream),"utf-8");
			paramMap = this.changeToParamters(inputStr);
			logger.debug("paramMap=" + paramMap.toString());
			
			String sign = URLDecoder.decode(paramMap.get("sign"), "utf-8");
			String order = URLDecoder.decode(paramMap.get("order"), "utf-8");
			String rsaKey = configInfo.getRsaKey();
			logger.debug(this.getSDKId() + " payCallBack. order:" + order);
			logger.debug(this.getSDKId() + " payCallBack. sign:" + sign);
			logger.debug(this.getSDKId() + " payCallBack. rsaKey:" + rsaKey);
			/**
			 *  进行RSA验签
			 *  Spring已经处理过urlDecode,直接将order信息传入
			 */
			boolean isOk = RSASignature.doCheck(order, sign, rsaKey);
			if (isOk) {
				//讲JSON格式的订单信息进行处理
				Map resultMap = ParseJson.getJsonContentByStr(order, Map.class);
				logger.debug(this.getSDKId() + " payCallBack. resultMap:" + resultMap);
				if (resultMap != null && 1 == Integer.parseInt(String.valueOf(resultMap.get("result_code")))) {
					// 付款成功金额，单位人民币分
					double realAmount = (Double.parseDouble(String.valueOf(resultMap.get("real_amount"))))/100;
					// cp自身的订单号
					String cpOrderId = String.valueOf(resultMap.get("game_order_id"));
					// jolo订单
					String joloOrderId = String.valueOf(resultMap.get("jolo_order_id"));
					
					boolean iResult = callback.succeedPayment(cpOrderId, joloOrderId, realAmount,
							"RMB", "", ParseJson.encodeJson(paramMap));
					if(iResult)
						return success;
				}
			}
			return fail;
		} catch (Exception e) {
			logger.error("", e);
			return fail;
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		ret.setSdkUid(input.getSdkUid());
		return ret.success();
	}

	private Map<String, String> changeToParamters(String payContent) {
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isNotBlank(payContent)) {
			String[] paramertes = payContent.split("&");
			for (String parameter : paramertes) {
				String[] p = parameter.split("=");
				map.put(p[0], p[1].replaceAll("\"", ""));
			}
		}
		return map;
	}
}
