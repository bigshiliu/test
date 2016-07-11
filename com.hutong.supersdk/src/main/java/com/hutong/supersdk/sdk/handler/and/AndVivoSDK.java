package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.vivo.VivoSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;
import com.hutong.supersdk.sdk.utils.UrlUtil;
import com.hutong.supersdk.sdk.utils.vivo.VivoSignUtils;

@Component("andVivoSDK")
public class AndVivoSDK implements IPayCallBackSDK, IVerifyUserSDK, IPostOrderSDK, ICheckOrderSDK {

	public static final String SDK_ID = "AndVIVO";

	private final static Log logger = LogFactory.getLog(AndVivoSDK.class);

	@Autowired
	private AppConfigDao appConfigDao;
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return VivoSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			VivoSDKInfo configInfo = (VivoSDKInfo) config;
			String platformSessionId = input.getSDKAccessToken();
			String url = configInfo.getRequestUrl();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("access_token", platformSessionId);
			String result = HttpUtil.postForm(url, paramMap);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap != null && resultMap.containsKey("uid")) {
				ret.setSdkUid(String.valueOf(resultMap.get("uid")));
				ret.setSdkAccessToken(platformSessionId);
				return ret.success();
			}
			logger.error(this.getSDKId() + " Login Error result: " + result);
		} catch (Exception e) {
			logger.error("Vivo Verify User Error.", e);
		}
		ret.setErrorMsg(this.getSDKId() + " Login Error.");
		return ret.fail();
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        String success = "success";
        String failure = "failure";
        try {
            VivoSDKInfo configInfo = (VivoSDKInfo) config;
			boolean isSuccess = true;
			String tradeStatus = paramMap.get("tradeStatus");
			if (!"0000".equals(tradeStatus)) {
				logger.info("Vivo pay failed!! tradeStatus:" + tradeStatus);
				isSuccess = false;
			}
			String respMsg = paramMap.get("respMsg");
			String utf8RespMsg = ISOtoUTF8(respMsg);
			Map<String, String> para = new HashMap<String, String>();
			para.put("respCode", paramMap.get("respCode"));
			para.put("respMsg", respMsg);
			para.put("signMethod", paramMap.get("signMethod"));
			para.put("signature", paramMap.get("signature"));
			para.put("tradeType", paramMap.get("tradeType"));
			para.put("tradeStatus", paramMap.get("tradeStatus"));
			para.put("cpId", paramMap.get("cpId"));
			para.put("appId", paramMap.get("appId"));
			para.put("uid", paramMap.get("uid"));
			para.put("cpOrderNumber", paramMap.get("cpOrderNumber"));
			para.put("orderNumber", paramMap.get("orderNumber"));
			para.put("orderAmount", paramMap.get("orderAmount"));
			para.put("extInfo", paramMap.get("extInfo"));
			para.put("payTime", paramMap.get("payTime"));

			Map<String, String> otherPara = new HashMap<String, String>();
			otherPara.put("respCode", paramMap.get("respCode"));
			otherPara.put("respMsg", utf8RespMsg);
			otherPara.put("signMethod", paramMap.get("signMethod"));
			otherPara.put("signature", paramMap.get("signature"));
			otherPara.put("tradeType", paramMap.get("tradeType"));
			otherPara.put("tradeStatus", paramMap.get("tradeStatus"));
			otherPara.put("cpId", paramMap.get("cpId"));
			otherPara.put("appId", paramMap.get("appId"));
			otherPara.put("uid", paramMap.get("uid"));
			otherPara.put("cpOrderNumber", paramMap.get("cpOrderNumber"));
			otherPara.put("orderNumber", paramMap.get("orderNumber"));
			otherPara.put("orderAmount", paramMap.get("orderAmount"));
			otherPara.put("extInfo", paramMap.get("extInfo"));
			otherPara.put("payTime", paramMap.get("payTime"));
			
			String key = configInfo.getAppSecret();
			if (!VivoSignUtils.verifySignature(para, key) && !VivoSignUtils.verifySignature(otherPara, key)) {
				logger.debug("Vivo sign is error!");
				return failure;
			}
			String platformOrder = paramMap.get("orderNumber");
            platformOrder = StringUtils.isEmpty(platformOrder) ? "Vivo order id is empty" : platformOrder;

			String orderId = paramMap.get("cpOrderNumber");
			// 取得充值人民币
			float CostMoney = Float.parseFloat(paramMap.get("orderAmount")) / 100;
			boolean iResult;
			if(isSuccess){
                String payWay = "";
                String currencyType = "RMB";
                iResult = callback.succeedPayment(orderId, platformOrder, CostMoney, currencyType, payWay,
						ParseJson.encodeJson(paramMap));
			}else{
				iResult = callback.failPayment(orderId, platformOrder);
			}
			if (iResult)
				return success;
        } catch (Exception e) {
            logger.error("",e);
        }
        return failure;
    }

	@SuppressWarnings({"rawtypes" })
	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
        PostOrderRet postRet = new PostOrderRet();
        try {
            VivoSDKInfo configInfo = (VivoSDKInfo) config;
            Map<String, String>	sendMsgMap = new HashMap<String, String>();
            DateFormat formatterDate = new SimpleDateFormat("yyyyMMddHHmmss");
            String submit_time= formatterDate.format(System.currentTimeMillis());
            //业务参数
            sendMsgMap.put("orderTime", submit_time);
            sendMsgMap.put("orderAmount", String.valueOf((int)(order.getOrderAmount()*100)));
            sendMsgMap.put("orderTitle", jsonReq.getDataKey(DataKeys.Payment.PRODUCT_NAME));
            sendMsgMap.put("orderDesc", jsonReq.getDataKey(DataKeys.Payment.PRODUCT_NAME));
            //商户参数
            sendMsgMap.put("cpId", configInfo.getCpId());
            sendMsgMap.put("appId", configInfo.getAppId());
            sendMsgMap.put("cpOrderNumber", order.getOrderId());
            // superSDKConfig.properties名称
            AppConfig appConfig = appConfigDao.findById(jsonReq.getAppId());
            if(null == appConfig)
                return postRet.fail();
            // 获取回调地址
            String payBackUrl = UrlUtil.getSDKPayBackUrl(appConfig.getShortAppId(), jsonReq.getAppChannelId());
            if(StringUtils.isEmpty(payBackUrl))
                return postRet.fail();
            sendMsgMap.put("notifyUrl", payBackUrl);
            //公共参数
            sendMsgMap.put("version", configInfo.getVersion());
            sendMsgMap.put("signMethod", configInfo.getSignMethod());
            String sign = VivoSignUtils.getVivoSign(sendMsgMap, configInfo.getAppSecret());
            logger.debug("vivo createOrder signend=" + sign);
            sendMsgMap.put("signature", sign);
            String returnStr = HttpUtil.postForm(configInfo.getCreateOrderUrl(), sendMsgMap);
            Map returnMap = ParseJson.getJsonContentByStr(returnStr, Map.class);
            if (returnMap != null && "200".equals(String.valueOf(returnMap.get("respCode")))) {
                postRet.setExtra("orderNumber", String.valueOf(returnMap.get("orderNumber")));
                postRet.setExtra("accessKey", String.valueOf(returnMap.get("accessKey")));
                return postRet.ok();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return postRet.fail();
    }
	
	private static String ISOtoUTF8(String isoString){
		return new String(isoString.getBytes(Charset.forName("ISO-8859-1")));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
        try {
            VivoSDKInfo configInfo = (VivoSDKInfo) config;

			//接口版本号
			String version = "1.0.0";
			//签名方法
			String signMethod = "MD5";
			//CPID,Vivo分发的参数
			String cpId = configInfo.getCpId();
			//SuperSDK订单号
			String cpOrderNumber = pOrder.getOrderId();
			//Vivo订单号
			String orderNumber = jsonData.getExtraKey("transNo");
			//交易金额
			String orderAmount = String.valueOf((int)(pOrder.getOrderAmount() * 100));
			//请求地址
			String checkOrderUrl = configInfo.getCheckOrderUrl();
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("version", version);
			paramMap.put("signMethod", signMethod);
			paramMap.put("cpId", cpId);
			paramMap.put("cpOrderNumber", cpOrderNumber);
			paramMap.put("orderNumber", orderNumber);
			paramMap.put("orderAmount", orderAmount);
			
			//签名信息
			String signature = SignUtil.createSignData(SignUtil.convert(paramMap), new String[]{"signMethod","signature"}, false, "&");
			signature = signature + "&" + MD5Util.MD5(configInfo.getAppSecret());
			logger.debug(this.getSDKId() + " checkOrder. paramMap:" + paramMap.toString() + " signature:" + signature);
			paramMap.put("signature", MD5Util.MD5(signature));
			
			String result = HttpUtil.postForm(checkOrderUrl, paramMap);
			logger.debug(this.getSDKId() + " checkOrder. result:" + result);
            Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
            if(resultMap != null && "200".equals(String.valueOf(resultMap.get("respCode")))
                    && "0000".equals(String.valueOf(resultMap.get("tradeStatus")))) {
                return ret.success(orderNumber, pOrder.getOrderAmount() * 100, "RMB", "", ParseJson.encodeJson(resultMap));
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        return ret.fail();
    }
}
