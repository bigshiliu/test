package com.hutong.supersdk.sdk.handler.and;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.common.util.StringUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.uc.UCSDKInfo;
import com.hutong.supersdk.sdk.utils.uc.PayCallbackResponse;
import com.hutong.supersdk.sdk.utils.uc.UCMD5Util;
import com.hutong.supersdk.sdk.utils.uc.Util;

@Component("andUCSDK")
public class AndUCSDK implements IPayCallBackSDK, IVerifyUserSDK {

	private static final Log logger = LogFactory.getLog(AndUCSDK.class);

	public static final String SDK_ID = "AndUC";

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return UCSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			UCSDKInfo configInfo = (UCSDKInfo) config;
			String token = input.getSDKAccessToken();
			Map<String, Object> sendMap = new HashMap<String, Object>();
			sendMap.put("id", System.currentTimeMillis());
			sendMap.put("service", "ucid.user.sidInfo");
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("sid", token);
			sendMap.put("data", data);
			Map<String, String> game = new HashMap<String, String>();
			game.put("gameId", configInfo.getGameId());
			sendMap.put("game", game);
			String apiKey = configInfo.getApiKey();
			/**
             * UC签名生成规则,请求数据 data字段 中,各字段名和其值的排名拼接加 apiKey
             */
            String signStr = UCMD5Util.createSignData(data, null);
			signStr += apiKey;
			String signMd5 = UCMD5Util.hexMD5(signStr);
			sendMap.put("sign", signMd5);

			String sendData = ParseJson.encodeJson(sendMap);
			String requestUrl;
			if(!StringUtils.isEmpty(input.getExtraKey("IS_DEBUG")) && "true".equals(input.getExtraKey("IS_DEBUG"))){
				requestUrl = configInfo.getDeBugRequestUrl();
			}else{
				requestUrl = configInfo.getRequestUrl();
			}
			String result = HttpUtil.postJson(requestUrl, sendData);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			logger.debug("resultMap:" + resultMap);
			logger.debug("sendData:" + sendData);
			logger.debug("result:" + result);
			if (resultMap != null && 1 == Integer.parseInt(String.valueOf(((Map) resultMap.get("state")).get("code")))) {
				Map dataMap = (Map) resultMap.get("data");
				ret.setSdkUid(String.valueOf(dataMap.get("accountId")));
				ret.setSdkAccessToken(token);
				ret.setSdkRefreshToken("");
				return ret.success();
			}
			logger.error("AndUCSDK verifyuser error, result:" + result);
		} catch (Exception e) {
			logger.error("", e);
		}
		ret.setErrorMsg("SDKLogin Error: UC checkUser failed! ");
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        String success = "SUCCESS";
        String failure = "FAILURE";

        BufferedReader in = null;
        try {
            UCSDKInfo configInfo = (UCSDKInfo) config;
            in = new BufferedReader(new InputStreamReader(servletInputStream, "utf-8"));
			String ln;
			StringBuilder stringBuffer = new StringBuilder();
			while ((ln = in.readLine()) != null) {
				stringBuffer.append(ln);
				stringBuffer.append("\r\n");
			}
			
			logger.debug("[接收到的参数]" + stringBuffer.toString());
			PayCallbackResponse rsp = (PayCallbackResponse) Util.decodeJson(stringBuffer.toString(),
					PayCallbackResponse.class);// 反序列化
			if (null != rsp) {
				paramMap.put("sign", rsp.getSign());
				paramMap.put("orderId", rsp.getData().getOrderId());
				paramMap.put("gameId", rsp.getData().getGameId() + "");
				paramMap.put("serverId", rsp.getData().getServerId() + "");
				paramMap.put("accountId", rsp.getData().getAccountId());
				paramMap.put("creator", rsp.getData().getCreator());
				paramMap.put("payWay", rsp.getData().getPayWay() + "");
				paramMap.put("amount", rsp.getData().getAmount());
				paramMap.put("callbackInfo", rsp.getData().getCallbackInfo());
				paramMap.put("orderStatus", rsp.getData().getOrderStatus());
				paramMap.put("failedDesc", rsp.getData().getFailedDesc());
				paramMap.put("cpOrderId", rsp.getData().getCpOrderId());
				String cpOrderId = rsp.getData().getCpOrderId();
				String apiKey = configInfo.getApiKey();

				StringBuilder sb = new StringBuilder();
				sb.append("accountId=").append(rsp.getData().getAccountId());
				sb.append("amount=").append(rsp.getData().getAmount());
				sb.append("callbackInfo=").append(rsp.getData().getCallbackInfo());

				if (!StringUtils.isEmpty(cpOrderId)) {
					sb.append("cpOrderId=").append(rsp.getData().getCpOrderId());
				}

				sb.append("creator=").append(rsp.getData().getCreator());
				sb.append("failedDesc=").append(rsp.getData().getFailedDesc());
				sb.append("gameId=").append(rsp.getData().getGameId());
				sb.append("orderId=").append(rsp.getData().getOrderId());
				sb.append("orderStatus=").append(rsp.getData().getOrderStatus());
				sb.append("payWay=").append(rsp.getData().getPayWay());
				sb.append(apiKey);
				String signSource = sb.toString();
				String sign = Util.getMD5Str(signSource);

				logger.debug("[签名原文]" + signSource);
				logger.debug("[签名结果]" + sign);

				if (StringUtil.equalsStringIgnoreCase(sign, rsp.getSign())) {
					/*
					 * 游戏服务器需要处理给玩家充值代码,由游戏合作商开发完成。
					 */
					// 取得uc的订单号
					String platformOrderId = rsp.getData().getOrderId();
					// 取得充值人民币
					double payAmount = Double.parseDouble(rsp.getData().getAmount());
					//货币类型
					String currencyType = "RMB";
					//交易类型
					String payType = "";
					if (rsp.getData().getOrderStatus().equalsIgnoreCase("F")) {
						return success;
					}
					boolean iResult = callback.succeedPayment(cpOrderId, platformOrderId, payAmount, currencyType, payType,
							stringBuffer.toString());
					if(iResult)
						return success;
				}else{
					return failure;
				}
			}else{
				logger.debug("接口返回异常");
			}
		} catch (Exception e) {
			logger.error("接收支付回调通知的参数失败", e);
		}finally{
            if (null != in)
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
        }
		return failure;
	}

}
