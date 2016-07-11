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
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.aibei.AiBeiObj;
import com.hutong.supersdk.sdk.modeltools.aibeipay.AiBeiPaySDKInfo;
import com.hutong.supersdk.sdk.modeltools.aibeipay.SignHelper;
import com.hutong.supersdk.sdk.utils.aibei.AiBeiSignHelper;

@Component("andAiBeiPaySDK")
public class AndAiBeiPaySDK extends AndWuYouSDK implements IPayCallBackSDK, IPostOrderSDK {
	
	private static final String SDK_ID = "AndAiBeiPay";
	
	private final static Log logger = LogFactory.getLog(AndAiBeiPaySDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return AiBeiPaySDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.info(this.getSDKId() + " payCallBack. start!");
		String success = "SUCCESS";
		String fail = "FAILURE";
		AiBeiPaySDKInfo configInfo = (AiBeiPaySDKInfo) config;
		try {
			String pubKey = String.valueOf(configInfo.getRsaPublicKey());
			String transdata = paramMap.get("transdata");
			String sign = paramMap.get("sign");
			boolean bRet = AiBeiSignHelper.verify(transdata, sign, pubKey);
			if (!bRet) {
				return fail;
			}
			AiBeiObj aiBeiObj = ParseJson.getJsonContentByStr(transdata, AiBeiObj.class);
			
			if (aiBeiObj == null || aiBeiObj.getResult() != 0) {
				return fail;
			}
			
			String transId = aiBeiObj.getTransid();
			if (transId == null || transId.equals("")) {
				transId = "AiBei order id is empty";
			}
			
			String orderId = aiBeiObj.getCporderid();
			double amount = aiBeiObj.getMoney();
			String payWay = aiBeiObj.getPaytype();
			
			boolean iResult = callback.succeedPayment(orderId, transId, amount, "RMB", payWay, ParseJson.encodeJson(paramMap));
			if(iResult){
				return success;
			}
			return fail;
		} catch (Exception e) {
			logger.error("", e);
			return fail;
		}
	}

	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
		logger.info(this.getSDKId() + " checkOrder. start!");
		AiBeiPaySDKInfo configInfo = (AiBeiPaySDKInfo) config;
		
		PostOrderRet postRet = new PostOrderRet();
		//返回Map
		try {
			//应用编号
			String appid = configInfo.getAppId();
			//商品编号
			String waresid = jsonReq.getDataKey("product_id");
			//商户订单号
			String cporderid = order.getOrderId();
			//支付金额
			String price = jsonReq.getDataKey("order_amount");
			//货币类型
			String currency = jsonReq.getDataKey("currency");
			//用户在商户应用的唯一标识
			String appuserid = jsonReq.getSdkUid();
			//支付结果通知地址
			String notifyurl = jsonReq.getDataKey("notify_url");
			
			//组装签名参数Map
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("appid", appid);
			paramMap.put("waresid", waresid);
			paramMap.put("cporderid", cporderid);
			paramMap.put("price", price);
			paramMap.put("currency", currency);
			paramMap.put("appuserid", appuserid);
			if(!StringUtils.isEmpty(notifyurl)){
				paramMap.put("notifyurl", notifyurl);
			}
			
			String privateKey = configInfo.getRsaPrivateKey();
			
			logger.debug(this.getSDKId() + " checkOrder. signStr:" + ParseJson.encodeJson(paramMap) + " rsaPrivateKey:" + privateKey);
			
			//对transdata的签名数据
			String Sign = SignHelper.sign(ParseJson.encodeJson(paramMap), privateKey);
			//签名算法类型,目前只支持RSA
			String Signtype = "RSA";
			
			paramMap.put("Sign", Sign);
			paramMap.put("Signtype", Signtype);
			
			String checkOrderUrl = configInfo.getCheckOrderUrl();
			
			//使用HTTPUtil,post请求 
			String result = HttpUtil.postForm(checkOrderUrl, paramMap);
			logger.debug(this.getSDKId() + " checkOrder. HTTPResult:" + result);

			@SuppressWarnings("unchecked")
			Map<String,Object> tempMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(tempMap != null){
				//交易流水号
				String resTransid = String.valueOf(tempMap.get("transid"));
				postRet.setExtra("resTransid", resTransid);
			}
			return postRet.fail();
		} catch (Exception e) {
			logger.error("PostOrder Error.", e);
			return postRet.fail();
		}
	}

}
