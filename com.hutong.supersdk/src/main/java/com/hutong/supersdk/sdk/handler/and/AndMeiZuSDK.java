package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
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
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.meizu.MeiZuSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.SignUtil;

@Component("andMeiZuSDK")
public class AndMeiZuSDK implements IVerifyUserSDK, IPayCallBackSDK, IPostOrderSDK, ICheckOrderSDK{
	
	public static final String SDK_ID = "AndMeiZu";
	
	private final Log logger = LogFactory.getLog(AndMeiZuSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return MeiZuSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String code = "900000";
		try {
			MeiZuSDKInfo configInfo = (MeiZuSDKInfo) config;
			//判断交易状态 3:以支付
			String trade_status = paramMap.get("trade_status");
			if(!"3".equals(trade_status)){
				logger.debug(paramMap.toString());
				return code;
			}
			//获取进行签名认证的参数
			Map<String, Object> data = new HashMap<String, Object>();
			for(String key : paramMap.keySet()){
				data.put(key, paramMap.get(key));
			}
			String sign = paramMap.get("sign");
			String[] notIn = new String[]{"sign","sign_type"};
			String mySign = SignUtil.createSignData(data, notIn, false, "&");
			String appSecret = configInfo.getAppSecret();
			mySign = MD5Util.MD5((mySign + ":" + appSecret));
			logger.debug(this.getSDKId() + " payCallBack sign:" + sign + " ----- mySign:" + mySign);
			if(!StringUtil.equalsStringIgnoreCase(sign, mySign)){
				return code;
			}
			String order_id = paramMap.get("order_id");
			String cp_order_id = paramMap.get("cp_order_id");
			String total_price = paramMap.get("total_price");
			String pay_type = paramMap.get("pay_type");
			boolean iResult = callback.succeedPayment(cp_order_id, order_id, Float.valueOf(total_price),
					"RMB", pay_type, ParseJson.encodeJson(paramMap.toString()));
			if(iResult)
				return "200";
			return "120014";
		} catch (Exception e) {
			logger.error("", e);
		}
		return code;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start!!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            MeiZuSDKInfo configInfo = (MeiZuSDKInfo) config;
            String app_id = configInfo.getAppID();
            String session_id = input.getSDKAccessToken();
            String uid = input.getSdkUid();
            String ts = String.valueOf(System.currentTimeMillis());
            String sign_type = "md5";
            //获取进行签名认证的参数
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("app_id", app_id);
            data.put("session_id", session_id);
            data.put("uid", uid);
            data.put("ts", ts);
            String signStr = SignUtil.createSignData(data, null, false, "&");
            String sign = MD5Util.MD5(signStr + ":" + configInfo.getAppSecret()).toLowerCase();
            String url = configInfo.getRequestUrl();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("app_id", app_id);
            paramMap.put("session_id", session_id);
            paramMap.put("uid", uid);
            paramMap.put("ts", ts);
            paramMap.put("sign_type", sign_type);
            paramMap.put("sign", sign);
            String result = HttpUtil.postForm(url, paramMap);
			@SuppressWarnings("rawtypes")
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if(resultMap == null || !"200".equals(String.valueOf(resultMap.get("code")))){
				ret.setErrorMsg(this.getSDKId() + " Login Error. Error code :"
                        + (resultMap != null ? resultMap.get("code") : null));
				return ret.fail();
			}
			ret.setSdkUid(uid);
			ret.setSdkAccessToken(session_id);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " Login Error");
			return ret.fail();
		}
	}

	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj input) {
        PostOrderRet postRet = new PostOrderRet();
        try {
            MeiZuSDKInfo configInfo = (MeiZuSDKInfo) config;
            String uid =  input.getExtraKey("uid");
            String create_time = String.valueOf(System.currentTimeMillis());
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("app_id", configInfo.getAppID());
            data.put("uid", uid);
            data.put("cp_order_id", order.getOrderId());
            data.put("total_price", order.getOrderAmount());
            data.put("product_id", order.getAppProductId());
            data.put("buy_amount", Integer.parseInt(order.getAppProductCount()));
            data.put("create_time", Long.parseLong(create_time));
            data.put("pay_type", 0);
            data.put("product_body", "");
            data.put("product_per_price", "");
            data.put("product_subject", "");
            data.put("product_unit", "");
            data.put("user_info", "");
            String signStr = SignUtil.createSignData(data, null, true, "&") + ":" + configInfo.getAppSecret();
            logger.debug(signStr);
            String sign = MD5Util.MD5(signStr);
            postRet.setExtra("sign", sign);
            postRet.setExtra("uid", uid);
            postRet.setExtra("signType", "md5");
            postRet.setExtra("APPID", configInfo.getAppID());
            postRet.setExtra("create_time", create_time);
            return postRet.ok();
        } catch (Exception e) {
            logger.error("", e);
        }
        return postRet.fail();
    }

	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
		logger.debug(this.getSDKId() + " checkOrder. start!");
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
        try {
            MeiZuSDKInfo configInfo = (MeiZuSDKInfo) config;
            String app_id = jsonData.getAppId();
			String cp_order_id = pOrder.getOrderId();
			String ts = jsonData.getExtraKey("ts");
			String sign_type = "MD5";
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("app_id", app_id);
			paramMap.put("cp_order_id", cp_order_id);
			paramMap.put("ts", ts);
			
			String sign = MD5Util.MD5(SignUtil.createSignData(SignUtil.convert(paramMap), null, false, "&"));
			
			paramMap.put("sign_type", sign_type);
			paramMap.put("sign", sign);
			
			logger.debug(this.getSDKId() + " checkOrder. sign:" + sign);
			logger.debug(this.getSDKId() + " checkOrder. paramMap:" + paramMap.toString());
			
			String checkOrderUrl = configInfo.getCheckOrderUrl();
			
			String result = HttpUtil.postForm(checkOrderUrl, paramMap);
			
			logger.debug(this.getSDKId() + " checkOrder. result:" + result);
			
			if(!StringUtils.isEmpty(result)){
				@SuppressWarnings("rawtypes")
				Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				//交易状态，1：待支付(订单已创建) 2：支付中3：已支付4：取消订单5：未知
				if(resultMap != null && "3".equals(String.valueOf(resultMap.get("tradeStatus")))) {
                    return ret.success(String.valueOf(resultMap.get("orderId")), Double.valueOf(String.valueOf(resultMap.get("totalPrice"))), "RMB", "", ParseJson.encodeJson(result));
                }
			}
		} catch (Exception e) {
			logger.error("", e);
		}
        return ret.fail();
    }

}
