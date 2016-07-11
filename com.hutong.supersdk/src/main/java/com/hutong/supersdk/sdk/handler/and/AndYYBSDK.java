package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.MD5Util;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.yyb.YYBSDKInfo;
import com.hutong.supersdk.sdk.utils.yyb.SnsSigCheck;
import com.hutong.supersdk.service.common.ThreadHelper;

@Component("andYYBSDK")
public class AndYYBSDK implements IVerifyUserSDK, IPostOrderSDK, ICheckOrderSDK, IPayCallBackSDK {

	private static final int BALANCE_EXCHANGE_RATE = 10;

	public static final String SDK_ID = "AndYingYongBao";

	private final static Log logger = LogFactory.getLog(AndYYBSDK.class);

	private static final String PAY_TYPE_BALANCE = "balance";
	private static final String PAY_TYPE_GOODS = "goods";

	private static final String KEY_PAY_TYPE = "buy_type";

	@Autowired
	private PaymentOrderDao paymentOrderDao;

	@Autowired
	private AppConfigDao appConfigDao;

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return YYBSDKInfo.class;
	}

	/**
	 * 用户登陆
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		YYBSDKInfo configInfo = (YYBSDKInfo) config;
		
		configInfo =  convertYYBSDKInfo(input, configInfo);
		
		String platform = input.getExtraKey("platform");
		String uid = input.getSdkUid();
		String token = input.getSDKAccessToken();
		ret.setSdkUid(uid);
		ret.setSdkAccessToken(token);
		if ("QQ".equals(platform)) {
			String qqAppId = configInfo.getQqAppId();
			String qqToken = input.getExtraKey("QQAccessToken");
			String ip = input.getExtraKey("clientIP");
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("appid", qqAppId);
			reqMap.put("openid", uid);
			reqMap.put("openkey", qqToken);
			reqMap.put("userip", ip);
			String url = configInfo.getQqRequestUrl();
			try {
				int timestamp = (int) (System.currentTimeMillis() / 1000);
				String qqAppkey = String.valueOf(configInfo.getQqAppKey());
				String sig = MD5Util.MD5(qqAppkey + timestamp);
				String sendUrl = url + "?timestamp=" + timestamp + "&appid=" + qqAppId + "&sig=" + sig + "&openid" + uid
						+ "&encode=1";
				String result = HttpUtil.postJson(sendUrl, ParseJson.encodeJson(reqMap));
				Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				if (resultMap == null || !resultMap.containsKey("ret") || Integer.parseInt(resultMap.get("ret").toString()) != 0) {
					logger.error(this.getSDKId() + " Login Error. result =" + result);
					ret.setErrorMsg(this.getSDKId() + " Login Error.");
					ret.fail();
				} else {
					ret.success();
				}
			} catch (Exception e) {
				logger.error("", e);
				ret.setErrorMsg(this.getSDKId() + " Login Error.");
				ret.fail();
			}
		} else if ("WX".equals(platform)) {
			String wxAppId = configInfo.getWxAppId();
			String wxAccessToken = input.getExtraKey("WXAccessToken");
			String openId = input.getExtraKey("uid");
			Map<String, String> reqMap = new HashMap<String, String>();
			reqMap.put("appid", wxAppId);
			reqMap.put("accessToken", wxAccessToken);
			reqMap.put("openid", openId);
			String url = configInfo.getWxRequestUrl();
			try {
				int timestamp = (int) (System.currentTimeMillis() / 1000);
				String wxAppKey = configInfo.getWxAppKey();
				String sig = MD5Util.MD5(wxAppKey + timestamp);
				String sendUrl = url + "?timestamp=" + timestamp + "&appid=" + wxAppId + "&sig=" + sig + "&openid="
						+ uid + "&encode=1";
				String result = HttpUtil.postJson(sendUrl, ParseJson.encodeJson(reqMap));
				Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
				if (resultMap == null || !resultMap.containsKey("ret") || Integer.parseInt(resultMap.get("ret").toString()) != 0) {
					logger.error(this.getSDKId() + " Login Error. result =" + result);
					ret.setErrorMsg(this.getSDKId() + " Login Error.");
					ret.fail();
				} else {
					String refreshToken = String.valueOf(resultMap.get("refreshToken"));
					ret.setSdkRefreshToken(refreshToken);
					ret.success();
				}
			} catch (Exception e) {
				logger.error("", e);
				ret.setErrorMsg(this.getSDKId() + " Login Error." + e.getMessage());
				ret.fail();
			}
		} else {
			logger.error("Platfrom error. platform=" + platform);
			ret.setErrorMsg(this.getSDKId() + " Platfrom error.");
			ret.fail();
		}
		return ret;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {

		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		try {
			YYBSDKInfo configInfo = (YYBSDKInfo) config;
			
			configInfo =  convertYYBSDKInfo(jsonData, configInfo);
			
			// 应用宝虚拟币兑换率
			int _BALANCE_EXCHANGE_RATE = AndYYBSDK.BALANCE_EXCHANGE_RATE;
			String balanceExchangeRate = configInfo.getBalanceExchangeRate();
			if (!StringUtils.isEmpty(balanceExchangeRate)) {
				_BALANCE_EXCHANGE_RATE = Integer.valueOf(balanceExchangeRate);
			}
			// 查询游戏币
			Map getResultMap = this.getBalance(jsonData, config);
			// 游戏币个数（包含了赠送游戏币）
			int balance = Integer.parseInt(getResultMap.get("balance").toString());
			int decBalance;
			// 判断游戏币余额是否充足
			if (0 == balance) {
				logger.error("YYB Check Order Failed. Balance is not enough. result=" + getResultMap.toString());
				ret.fail();
				return ret;
			} else if (1.0 * balance < _BALANCE_EXCHANGE_RATE * pOrder.getOrderAmount()) {
				decBalance = balance;
			} else {
				decBalance = (int) (_BALANCE_EXCHANGE_RATE * pOrder.getOrderAmount());
			}
			// 扣除游戏币
			Map payResultMap = this.payBalance(decBalance, jsonData, configInfo);
			logger.debug(SDK_ID + " checkOrder, payResult:" + payResultMap.get("ret") + ". payResultMap:"
					+ payResultMap.toString());
			if (0 == Integer.parseInt(payResultMap.get("ret").toString())) {
				String billno = payResultMap.get("billno").toString();
				double payAmount = 1.0 * decBalance / _BALANCE_EXCHANGE_RATE;
                return ret.success(billno, payAmount, "RMB", "",
                            ParseJson.encodeJson(jsonData));
			} else if (Integer.parseInt(payResultMap.get("ret").toString()) == 1004) {
				// 余额不足
				logger.error("YYB Check Order Failed. Balance is not enough. result=" + payResultMap.toString());
				return ret.fail();
			} else {
				// 其他错误
				logger.error("YYB Check Order Failed. result=" + payResultMap.toString());
				return ret.fail();
			}
		} catch (Exception e) {
			logger.error("YYB Check Order Failed", e);
			return ret.fail();
		}
	}

	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
		PostOrderRet ret = new PostOrderRet();
		
		/**
		 *  buy_type 如果为1 是购买Q币 如果为2是购买道具
		 *  如果是购买道具,则直接返回SuperSDKOrder信息
		 */
		String buyType = jsonReq.getExtraKey(KEY_PAY_TYPE);
		logger.debug(this.getSDKId() + " postOrder. buyType" + buyType);
		if (null != buyType && PAY_TYPE_BALANCE.equals(buyType)) {
			try {
				ret.setExtra( postOrderBalance(order, config, jsonReq) );
				return ret;
			} catch (Exception e) {
				logger.error("Post Order Error.", e);
				return ret.fail();
			}
		}
		else if (null != buyType && PAY_TYPE_GOODS.equals(buyType)) {			
			return ret.ok();
		}
		return ret.ok();
	}
	
	/**
	 * 道具购买方式的支付消息回调
	 */
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(paramMap);
		
		YYBSDKInfo configInfo = (YYBSDKInfo) config;
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		int resultCode = 0; // 无错返回
		String resultMsg = "success";// 无错返回
		
		try {
			logger.debug("in pay result payResultTencentYingyongbao");
			/**
			 * 在道具寄售系统中，购买道具的用户openid。
			 * 与APP通信的用户key，跳转到应用首页后，URL后会带该参数。由平台直接传给应用，应用原样传给平台即可。
			 * 根据APPID以及QQ号码生成，即不同的appid下，同一个QQ号生成的OpenID是不一样的。
			 */
//			String openid = paramMap.get("openid");
			/**
			 * 应用的唯一ID。可以通过appid查找APP基本信息。
			 */
//			String appid = paramMap.get("appid");
			/**
			 * linux时间戳。
			 * 注意开发者的机器时间与腾讯计费开放平台的时间相差不能超过15分钟。
			 */
//			String ts = paramMap.get("ts");
			/**
			 * 物品购买信息。
			 * （1）接收标准格式为ID*price*num，回传时ID为必传项。批量购买套餐物品则用“;”分隔，字符串中不能包含"|"特殊字符。
			 * （2）ID表示物品ID，price表示单价（以Q点为单位，单价最少不能少于2Q点，1Q币=10Q点。单价的制定需遵循道具定价规范），num表示购买数量。
			 * 示例：批量购买套餐，套餐中包含物品1和物品2。物品1的ID为G001，该物品的单价为10Q点，购买数量为1；物品2的ID为G008，该物品的单价为8Q点，购买数量为2，则payitem为：G001*10*1;G008*8*2 。
			 */
//			String payitem = paramMap.get("payitem");
			/**
			 * 应用调用v3/pay/exchange_goods接口成功返回的交易token。
			 * 注意，交易token的有效期为15分钟，必须在获取到token后的15分钟内传递该token，否则将会返回token不存在的错误。
			 */
//			String token = paramMap.get("token");
			/**
			 * 支付流水号（64个字符长度。该字段和openid合起来是唯一的）。
			 */
			String billno = paramMap.get("billno");
			/**
			 * 协议版本号，由于基于V3版OpenAPI，这里一定返回“v3”。
			 */
//			String version = paramMap.get("version");
			/**
			 * 在支付营销分区配置说明页面，配置的分区ID即为这里的“zoneid”。
			 * 如果应用不分区，则为0。
			 * 回调发货的时候，根据这里填写的zoneid实现分区发货。
			 * 注：2013年后接入的寄售应用，此参数将作为分区发货的重要参数，如果因为参数传错或为空造成的收入损失，由开发商自行承担。
			 */
//			String zoneid = paramMap.get("zoneid");
			/**
			 * 发货类型，这里请传入3。
			 * 0表示道具购买，1表示营销活动中的道具赠送，2表示交叉营销任务集市中的奖励发放，3表示道具寄售系统中的道具转移。
			 */
//			String providetype = paramMap.get("providetype");
			/**
			 * Q点/Q币消耗金额或财付通游戏子账户的扣款金额。道具寄售目前暂不支持Q点/Q币/财付通游戏子账户，因此该参数的值为0。
			 * 以0.1Q点为单位
			 */
			String amt = StringUtils.isEmpty(paramMap.get("amt")) ? "0" : paramMap.get("amt");
			/**
			 * 扣取的游戏币总数，单位为Q点。可以为空，若传递空值或不传本参数则表示未使用游戏币。
			 * 允许游戏币、Q点、抵扣券三者混合支付，或只有其中某一种进行支付的情况。用户购买道具时，系统会优先扣除用户账户上的游戏币，游戏币余额不足时，使用Q点支付，Q点不足时使用Q币/财付通游戏子账户。
			 * 游戏币由平台赠送或由好友打赏，平台赠送的游戏币不纳入结算，即不参与分成；好友打赏的游戏币按消耗量参与结算（详见：货币体系与支付场景）。
			 */
			String payamt_coins = StringUtils.isEmpty(paramMap.get("payamt_coins")) ? "0" : paramMap.get("payamt_coins");
			/**
			 * 扣取的抵用券总金额，单位为Q点。可以为空，若传递空值或不传本参数则表示未使用抵扣券。
			 * 允许游戏币、Q点、抵扣券三者混合支付，或只有其中某一种进行支付的情况。用户购买道具时，可以选择使用抵扣券进行一部分的抵扣，剩余部分使用游戏币/Q点。
			 * 平台默认所有上线支付的应用均支持抵扣券。自2012年7月1日起，金券银券消耗将和Q点消耗一起纳入收益计算（详见：货币体系与支付场景）。
			 */
			String pubacct_payamt_coins = StringUtils.isEmpty(paramMap.get("pubacct_payamt_coins")) ? "0" : paramMap.get("pubacct_payamt_coins");
			/**
			 * 请求串的签名，由需要签名的参数生成。
			 * （1）签名方法请见文档：腾讯开放平台第三方应用签名参数sig的说明。
			 * （2）按照上述文档进行签名生成时，需注意回调协议里多加了一个步骤： 
			 * 在构造源串的第3步“将排序后的参数(key=value)用&拼接起来，并进行URL编码”之前，需对value先进行一次编码 （编码规则为：除了 0~9 a~z A~Z !*() 之外其他字符按其ASCII码的十六进制加%进行表示，例如“-”编码为“%2D”）。
			 * （3）以每笔交易接收到的参数为准，接收到的所有参数除sig以外都要参与签名。为方便平台后续对协议进行扩展，请不要将参与签名的参数写死。（4）所有参数都是string型，进行签名时必须使用原始接收到的string型值。 开发商出于本地记账等目的，对接收到的某些参数值先转为数值型再转为string型，导致字符串部分被截断，从而导致签名出错。如果要进行本地记账等逻辑，建议用另外的变量来保存转换后的数值。
			 */
			String sig = paramMap.get("sig");
			/**
			 * 透传参数：orderid*支付账号方式*支付方式
			 * 例如：9a24ffa4d4374de2837a4946a9960d5a*wechat*wechat
			 */
			String appmeta = paramMap.get("appmeta");
			
			String orderId = appmeta.split("\\*")[0];

			String secret = configInfo.getMidasAppKey() + "&";
			
			String callBackUrlPath = getPayBackUrl(orderId);
			
			boolean sigCheck = SnsSigCheck.verifySig(method, callBackUrlPath, (HashMap<String, String>)paramMap, secret, sig);
	
			if (!sigCheck) {
				resultCode = 4;
				resultMsg = "Sig Check Failed";
			}
	
			if (resultCode == 0) {
				//amt以0.1Q点为单位
				double dAmt = 1.0 * Integer.parseInt(amt) / 10;
				//以Q点为单位
				double dPayamt_coins = 1.0 * Integer.parseInt(payamt_coins);
				//以Q点为单位
				double dPubacct_payamt_coins = 1.0 * Integer.parseInt(pubacct_payamt_coins);
				//1Q点=0.1Q币，1Q币=1元
				double costMoney = (dAmt + dPayamt_coins + dPubacct_payamt_coins) / 10;
				boolean iResult = callback.succeedPayment(orderId, billno, costMoney, "RMB", "", ParseJson.encodeJson(paramMap));
				
				if (iResult) {
					resultCode = 0;
					resultMsg = "success";
				}
				else {
					resultCode = 1;
					resultMsg = "failed";
				}
			}
		}
		catch (Exception e) {
			logger.error("Check Order Error.", e);
			resultCode = 1;
			resultMsg = "Internal Error.";
		}
		resultMap.put("ret", resultCode);
		resultMap.put("msg", resultMsg);
		return resultMap;
	}
	
	/**
	 * 游戏币购买方式的的postOrder操作
	 * @param order order
	 * @param config config
	 * @param jsonReq jsonReq
	 * @return Map
	 * @throws Exception TODO
	 */
	@SuppressWarnings("rawtypes")
	private Map<String, String> postOrderBalance(PaymentOrder order, Object config, JsonReqObj jsonReq)
			throws Exception {
		YYBSDKInfo configInfo = (YYBSDKInfo) config;
		
		configInfo =  convertYYBSDKInfo(jsonReq, configInfo);
		
		// 应用宝虚拟币兑换率
		int _BALANCE_EXCHANGE_RATE = AndYYBSDK.BALANCE_EXCHANGE_RATE;
		if (null != configInfo.getBalanceExchangeRate() && !"".equals(configInfo.getBalanceExchangeRate())) {
			_BALANCE_EXCHANGE_RATE = Integer.valueOf(configInfo.getBalanceExchangeRate());
		}
		Map<String, String> retMap = new HashMap<String, String>();
        // 查询游戏币
        Map getResultMap = this.getBalance(jsonReq, config);
        String balance = getResultMap.get("balance").toString();
        // 判断用户应用宝余额是否充足
        String ret = "0";// 余额充足
        // PayAmount 订单实际支付金额
        if (1.0 * Integer.parseInt(balance) < (_BALANCE_EXCHANGE_RATE * order.getOrderAmount())) {
            ret = "1";// 余额不足
        }
        // 返回给客户端的结果
        retMap.put("ret", ret);
        retMap.put("balance", balance);
        retMap.put("price", String.valueOf(Math.round(_BALANCE_EXCHANGE_RATE * order.getOrderAmount())));
        return retMap;
	}

	/**
	 * 查询游戏币
	 * 
	 * @param jsonData
	 *            请求参数
	 * @param config
	 *            配置参数
	 * @return Map
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private Map getBalance(JsonReqObj jsonData, Object config) throws Exception {
		YYBSDKInfo configInfo = (YYBSDKInfo) config;
        String platform = String.valueOf(jsonData.getExtraKey(("platform")));

        String cookie = getCookie(platform, "/mpay/get_balance_m");

        HashMap<String, String> sendParams = getCommonParamMap(jsonData, config, platform);

        String secret = String.valueOf(configInfo.getQqAppKey()) + "&";
        String sig = SnsSigCheck.makeSig("get", "/mpay/get_balance_m", sendParams, secret);
        sendParams.put("sig", sig);

        String sendStr = generateSendStr(sendParams);

        String url = String.valueOf(configInfo.getBalanceUrl());
        String sendUrl = url + "?" + sendStr;
        String result = HttpUtil.get(sendUrl, cookie);
        logger.debug("YYB GetBalance SendUrl=" + sendUrl + " cookie=" + cookie + " result=" + result);
        Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
        if (resultMap == null || !resultMap.containsKey("ret") || Integer.parseInt(resultMap.get("ret").toString()) != 0) {
            logger.error("YYB GetBalance Error. result=" + result);
            throw new Exception("YYB GetBalance Error.");
        } else {
            return resultMap;
        }
	}

	/**
	 * 遍历Map得到string串
	 * 
	 * @param sendParams sendParams
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	private String generateSendStr(HashMap<String, String> sendParams) throws UnsupportedEncodingException {
		StringBuilder sendStrBuf = new StringBuilder("");
		for (Entry<String, String> entry : sendParams.entrySet()) {
			sendStrBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"))
					.append("&");
		}
		// 删除最后一个&
		sendStrBuf.deleteCharAt(sendStrBuf.length() - 1);
        return sendStrBuf.toString();
	}

	/**
	 * 支付虚拟游戏币
	 * 
	 * @param decBalance
	 *            游戏币数量
	 * @param jsonData
	 *            请求参数
	 * @param config
	 *            配置参数
	 * @return Map
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private Map payBalance(int decBalance, JsonReqObj jsonData, Object config) throws Exception {
        YYBSDKInfo configInfo = (YYBSDKInfo) config;
        String platform = String.valueOf(jsonData.getExtraKey("platform"));

        String cookie = getCookie(platform, "/mpay/pay_m");

        HashMap<String, String> sendParams = getCommonParamMap(jsonData, config, platform);
        sendParams.put("amt", String.valueOf(decBalance));

        String secret = String.valueOf(configInfo.getQqAppKey()) + "&";
        String sig = SnsSigCheck.makeSig("get", "/mpay/pay_m", sendParams, secret);
        sendParams.put("sig", sig);

        String sendStr = generateSendStr(sendParams);

        String url = String.valueOf(configInfo.getPayBalanceUrl());
        String sendUrl = url + "?" + sendStr;
        String result = HttpUtil.get(sendUrl, cookie);
        logger.debug("YYB PayBalance SendUrl=" + sendUrl + " cookie=" + cookie + " result=" + result);
        Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
        if (resultMap == null || !resultMap.containsKey("ret")) {
            logger.error("YYB PayBalance Error. result=" + result);
            throw new Exception("YYB PayBalance Error.");
        } else {
            return resultMap;
        }
	}

	/**
	 * 取消游戏币支付
	 * 
	 * @param decBalance
	 *            游戏币数量
	 * @param req
	 *            请求参数
	 * @param config
	 *            配置参数
	 * @throws Exception
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private void cancelPay(int decBalance, JsonReqObj req, Object config) throws Exception {
		YYBSDKInfo configInfo = (YYBSDKInfo) config;

        String platform = String.valueOf(req.getExtraKey("platform"));

        String cookie = getCookie(platform, "/mpay/cancel_pay_m");

        HashMap<String, String> sendParams = getCommonParamMap(req, config, platform);
        sendParams.put("amt", String.valueOf(decBalance));

        String secret = String.valueOf(configInfo.getQqAppKey()) + "&";
        String sig = SnsSigCheck.makeSig("get", "/mpay/cancel_pay_m", sendParams, secret);
        sendParams.put("sig", sig);

        String sendStr = generateSendStr(sendParams);

        String url = String.valueOf(configInfo.getCancelBalanceUrl());
        String sendUrl = url + "?" + sendStr;
        String result = HttpUtil.get(sendUrl, cookie);
        logger.debug("YYB CancelPayBalance SendUrl=" + sendUrl + " cookie=" + cookie + " result=" + result);
        Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
        if (resultMap == null || !resultMap.containsKey("ret") || Integer.parseInt(resultMap.get("ret").toString()) != 0) {
            logger.error("YYB CancelPayBalance Error. result=" + result);
            throw new Exception("YYB CancelPayBalance Error.");
        }
	}

	private HashMap<String, String> getCommonParamMap(JsonReqObj req, Object config, String platform) {
		YYBSDKInfo configInfo = (YYBSDKInfo) config;
		
		configInfo =  convertYYBSDKInfo(req, configInfo);
		
		String openid = String.valueOf(req.getExtraKey("uid"));
		String openkey = platform.equals("QQ") ? String.valueOf(req.getExtraKey("QQAccessToken"))
				: String.valueOf(req.getExtraKey("WXAccessToken"));
		String pay_token = String.valueOf(req.getExtraKey("payToken"));
		String appid = String.valueOf(configInfo.getQqAppId());
		int ts = (int) (System.currentTimeMillis() / 1000);
		String pf = String.valueOf(req.getExtraKey("pf"));
		String pfkey = String.valueOf(req.getExtraKey("pfKey"));
		int zoneid = 1;
		String format = "json";

		HashMap<String, String> sendParams = new HashMap<String, String>();
		sendParams.put("openid", openid);
		sendParams.put("openkey", openkey);
		sendParams.put("pay_token", pay_token);
		sendParams.put("appid", appid);
		sendParams.put("ts", String.valueOf(ts));
		sendParams.put("pf", pf);
		sendParams.put("pfkey", pfkey);
		sendParams.put("zoneid", String.valueOf(zoneid));
		sendParams.put("format", format);
		return sendParams;
	}

	/**
	 * Cookie
	 * 
	 * @param platform platform
	 * @param uriPath uriPath
	 * @return String
	 * @throws UnsupportedEncodingException
	 */
	private String getCookie(String platform, String uriPath) throws UnsupportedEncodingException {
		// 计算cookie
		String session_id = platform.equals("QQ") ? "openid" : "hy_gameid";
		String session_type = platform.equals("QQ") ? "kp_actoken" : "wc_actoken";
        return "session_id=" + URLEncoder.encode(session_id, "utf-8") + ";session_type="
                + URLEncoder.encode(session_type, "utf-8") + ";org_loc=" + URLEncoder.encode(uriPath, "utf-8") + ";";
	}
	
	/**
	 * 根据SuperSDK订单号获取支付回调地址
	 * @param orderId orderId
	 * @return 支付回调地址(不包含域名)
	 */
	public String getPayBackUrl(String orderId){
		logger.debug("PathUtil getPayBackUrl. orderId:" + orderId);
		//保存当前AppId
		String localAppId = ThreadHelper.getAppId();
		//根据订单号获取渠道ID
		PaymentOrder paymentOrder = paymentOrderDao.queryByOrderId(orderId);
		if(null == paymentOrder)
			return "";
		String channelId = paymentOrder.getAppChannelId();
		if(StringUtils.isEmpty(channelId))
			return "";
		ThreadHelper.setAppId(null);
		//根据AppId获取shortId
		AppConfig appConfig = appConfigDao.findById(localAppId);
		if(null == appConfig)
			return "";
		String shortId = appConfig.getShortAppId();
		if(StringUtils.isEmpty(shortId))
			return "";
		//操作完毕,将数据源切换到原始状态 
		ThreadHelper.setAppId(localAppId);
		logger.debug("PathUtil getPayBackUrl. payBackUrl:" + "supersdk-web/payback/" + shortId + "/" + channelId);
		return "/supersdk-web/payback/" + shortId + "/" + channelId;
	}
	
	public YYBSDKInfo convertYYBSDKInfo(JsonReqObj jsonData, YYBSDKInfo yybsdkInfo){
		if(!StringUtils.isEmpty(jsonData.getExtraKey("IS_DEBUG")) && "true".equals(jsonData.getExtraKey("IS_DEBUG"))){
			yybsdkInfo.setQqRequestUrl(yybsdkInfo.getDeBugQqRequestUrl());
			yybsdkInfo.setWxRequestUrl(yybsdkInfo.getDeBugWxRequestUrl());
			yybsdkInfo.setBalanceUrl(yybsdkInfo.getDeBugBalanceUrl());
			yybsdkInfo.setPayBalanceUrl(yybsdkInfo.getDeBugPayBalanceUrl());
			yybsdkInfo.setCancelBalanceUrl(yybsdkInfo.getDeBugCancelBalanceUrl());
		}
		return yybsdkInfo;
	}
}
