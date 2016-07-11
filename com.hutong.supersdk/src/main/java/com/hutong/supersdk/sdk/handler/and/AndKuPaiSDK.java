package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.kupai.KuPaiPayBackInfo;
import com.hutong.supersdk.sdk.modeltools.kupai.KuPaiSDKInfo;
import com.hutong.supersdk.sdk.utils.kupai.Base64;
import com.hutong.supersdk.sdk.utils.kupai.MD5;
import com.hutong.supersdk.sdk.utils.kupai.RSAUtil;
import com.hutong.supersdk.sdk.utils.kupai.SignHelper;

@Component("andKuPaiSDK")
public class AndKuPaiSDK implements IPayCallBackSDK, IVerifyUserSDK, IPostOrderSDK {

	private static final String SDK_ID = "AndKuPai";

	private final static Log logger = LogFactory.getLog(AndKuPaiSDK.class);

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    private static final String VERSION_127 = "1.2.7";

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return KuPaiSDKInfo.class;
	}

	@SuppressWarnings({"rawtypes" })
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			KuPaiSDKInfo configInfo = (KuPaiSDKInfo) config;
			String code = input.getSDKAccessToken();
			String grantType = configInfo.getGrant_type();
			String clientId = configInfo.getClient_id();
			String clientSecret = configInfo.getClient_secret();
			// 自动化打包暂时不支持同一参数传递两次.由于client_secret和redirect_uri相同,所以取同一值
			// String redirectUri = configInfo.getRedirect_uri();
			String url = configInfo.getRequest_url();

			String result = HttpUtil.get(url + "?grant_type=" + grantType + "&client_id=" + clientId + "&client_secret="
					+ clientSecret + "&code=" + code + "&redirect_uri=" + clientSecret);
			logger.debug(result);

			Map map = ParseJson.getJsonContentByStr(result, Map.class);

			if (map == null || map.get("openid") == null || map.get("access_token") == null || map.get("refresh_token") == null) {
				logger.debug(this.getSDKId() + " verifyUser Login Error. result:" + result);
				ret.setErrorMsg(this.getSDKId() + " verifyUser Login Error.");
				return ret.fail();
			}
			ret.setSdkUid(map.get("openid").toString());
			ret.setSdkAccessToken(map.get("access_token").toString());
			ret.setSdkRefreshToken(map.get("refresh_token").toString());
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Login Error.");
			return ret.fail();
		}
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap.toString());
		try {
			KuPaiSDKInfo configInfo = (KuPaiSDKInfo) config;
			String pubKey = configInfo.getPub_key();
			String transdata = paramMap.get("transdata");
			String sign = paramMap.get("sign");
			KuPaiPayBackInfo payBackInfo = ParseJson.getJsonContentByStr(transdata, KuPaiPayBackInfo.class);
            if(payBackInfo != null && !StringUtils.isEmpty(payBackInfo.getCpprivate()) && VERSION_127.equals(payBackInfo.getCpprivate()))
                return payCallBack127(callback, paramMap, pubKey, transdata, sign, payBackInfo);
            else
                return payCallBack(callback, paramMap, pubKey, transdata, sign, payBackInfo);
		} catch (Exception ex) {
			logger.error("", ex);
		}
        return FAILURE;
    }

    private String payCallBack127(IPaySuccessHandler callback, Map<String, String> paramMap, String pubKey, String transdata,
                                  String sign, KuPaiPayBackInfo payBackInfo) {
        if (!validSign127(transdata, sign, pubKey, "UTF-8"))
            return FAILURE;

        if (0 != Integer.parseInt(payBackInfo.getResult()))
            return FAILURE;

        String transId = payBackInfo.getTransid();
        transId = StringUtils.isEmpty(transId) ? "KuPai order id is empty" : transId;

        String orderId = payBackInfo.getCporderid();

        double amount = Double.parseDouble(payBackInfo.getMoney());
        String payWay = String.valueOf(payBackInfo.getPaytype());
        boolean iResult = callback.succeedPayment(orderId, transId, amount, "RMB", payWay,
                ParseJson.encodeJson(paramMap));
        if (iResult)
            return SUCCESS;
        return FAILURE;
    }

    private String payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, String pubKey, String transdata,
                               String sign, KuPaiPayBackInfo payBackInfo) {
        if (!validSign(transdata, sign, pubKey))
            return FAILURE;

        if (0 != Integer.parseInt(payBackInfo.getResult()))
            return FAILURE;

        String transId = payBackInfo.getTransid();
        transId = StringUtils.isEmpty(transId) ? "KuPai order id is empty" : transId;

        String orderId = payBackInfo.getExorderno();

        double amount = Double.parseDouble(payBackInfo.getMoney()) / 100;
        String payWay = String.valueOf(payBackInfo.getPaytype());
        boolean iResult = callback.succeedPayment(orderId, transId, amount, "RMB", payWay,
                ParseJson.encodeJson(paramMap));
        if (iResult)
            return SUCCESS;
        return FAILURE;
    }

    @Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
		PostOrderRet ret = new PostOrderRet();
        if (VERSION_127.equals(jsonReq.getExtraKey("version"))) {
            try {
                KuPaiSDKInfo configInfo = (KuPaiSDKInfo) config;
                return postOrder127(order, jsonReq, ret, configInfo);
            }
            catch (Exception ex) {
                return ret.fail();
            }
        }
        else {
            return ret.ok();
        }
	}

    @SuppressWarnings("rawtypes")
	private PostOrderRet postOrder127(PaymentOrder order, JsonReqObj jsonReq, PostOrderRet ret, KuPaiSDKInfo configInfo) {
        try {
            // 应用编号
            String appid = configInfo.getClient_id();
            // 商品编号
            int waresid = Integer.parseInt(jsonReq.getDataKey(DataKeys.Payment.PRODUCT_ID));
            // 商品名称
            String waresname = jsonReq.getDataKey(DataKeys.Payment.PRODUCT_NAME);
            // SuperSDK订单号
            String cporderid = order.getOrderId();
            // 支付金额
            float price = Float.parseFloat(jsonReq.getDataKey(DataKeys.Payment.ORDER_AMOUNT));
            // 货币类型
            String currency = "RMB";
            // SuperSDK用户id
            String appuserid = jsonReq.getUid();
            // 用户私有信息
            String cpprivateinfo = jsonReq.getExtraKey("version");
            // 加密私钥
            String privateKey = configInfo.getPri_key();
            // 请求参数
            String transdata = "{\"appid\":\"" + appid + "\",\"waresid\":" + waresid + ",\"cporderid\":\"" + cporderid
                    + "\",\"waresname\":\"" + waresname + "\",\"price\":" + price + ",\"currency\":\"" + currency
                    + "\",\"appuserid\":\"" + appuserid + "\",\"cpprivateinfo\":\"" + cpprivateinfo + "\"}";
            String sign = SignHelper.sign(transdata, privateKey);
            String signtype = "RSA";
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("transdata", transdata);
            paramMap.put("sign", sign);
            paramMap.put("signtype", signtype);

            String postOrderRes = HttpUtil.postForm(configInfo.getPostOrdetUrl(), paramMap);
            if (StringUtils.isEmpty(postOrderRes)) {
                return ret.fail();
            }
            Map<String, String> resMap = StringUtil.stringFormFormat(URLDecoder.decode(postOrderRes, "UTF-8"));
            if (resMap == null || 0 == resMap.size())
                return ret.fail();

            transdata = String.valueOf(resMap.get("transdata"));
            Map tranMap = ParseJson.getJsonContentByStr(transdata, Map.class);
            if (tranMap == null || StringUtils.isEmpty(tranMap.get("transid"))) {
                logger.error(this.getClass().getSimpleName() + " Post Order Error. ErrorCode:" + (tranMap != null ? tranMap.get("code") : null)
                        + " ErrorMsg:" + (tranMap != null ? tranMap.get("errmsg") : null));
                return ret.fail();
            }
            // 交易流水号
            ret.setExtra("transid", String.valueOf(tranMap.get("transid")));
            return ret.ok();
        } catch (Exception e) {
            logger.error("", e);
        }
        return ret.fail();
    }

    /**
	 * 
	 * @param transdata
	 *            同步过来的transdata数据
	 * @param sign
	 *            同步过来的sign数据
	 * @param key
	 *            应用的密钥(商户可从商户自服务系统获取)
	 * @return 验证签名结果 true:验证通过 false:验证失败
	 */
	public static boolean validSign(String transdata, String sign, String key) {
		try {
			String md5Str = MD5.md5Digest(transdata);
			String decodeBaseStr = Base64.decode(key);
			String[] decodeBaseVec = decodeBaseStr.replace('+', '#').split("#");
			String privateKey = decodeBaseVec[0];
			String modkey = decodeBaseVec[1];
			String reqMd5 = RSAUtil.decrypt(sign, new BigInteger(privateKey), new BigInteger(modkey));
			if (md5Str.equals(reqMd5))
				return true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}
	
	public static boolean validSign127(String content, String sign, String iapp_pub_key, String input_charset)
	{
		try 
		{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        byte[] encodedKey = com.hutong.supersdk.sdk.utils.Base64.decode(iapp_pub_key);
	        PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
			java.security.Signature signature = java.security.Signature.getInstance("MD5WithRSA");
			signature.initVerify(pubKey);
			signature.update( content.getBytes(input_charset) );
			return signature.verify( com.hutong.supersdk.sdk.utils.Base64.decode(sign) );
		} 
		catch (Exception e) {
            logger.error("", e);
		}
		return false;
	}
}
