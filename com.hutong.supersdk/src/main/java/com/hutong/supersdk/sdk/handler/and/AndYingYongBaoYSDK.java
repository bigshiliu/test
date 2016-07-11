package com.hutong.supersdk.sdk.handler.and;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.MD5Util;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.*;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.yyby.YingYongBaoYSDKInfo;
import com.hutong.supersdk.sdk.utils.yyby.SnsSigCheck;
import com.hutong.supersdk.service.common.ThreadHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Component("andYingYongBaoYSDK")
public class AndYingYongBaoYSDK implements IVerifyUserSDK, IPostOrderSDK, ICheckOrderSDK, IPayCallBackSDK {

    private final static Log logger = LogFactory.getLog(AndYingYongBaoYSDK.class);

    private final static String SDK_ID = "AndYingYongBaoY";

    private final static int BALANCE_EXCHANGE_RATE = 10;

    private final static String IS_DEBUG = "is_debug";
    private final static String PLATFORM = "platform";
    private final static String PLATFORM_TYPE_QQ = "QQ";
    private final static String PLATFORM_TYPE_WX = "WX";
    private final static String CLIENT_IP = "client_ip";
    private final static String PAY_TYPE_BALANCE = "balance";
    private final static String PAY_TYPE_GOODS = "goods";
    private final static String KEY_PAY_TYPE = "buy_type";
    private final static String UID = "uid";
    private final static String QQ_ACCESS_TOKEN = "QQAccessToken";
    private final static String WX_ACCESS_TOKEN = "WXAccessToken";
    private final static String PAY_TOKEN = "payToken";
    private final static String PF = "pf";
    private final static String PF_KEY = "pfKey";

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
        return YingYongBaoYSDKInfo.class;
    }

    @Override
    public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
        SDKVerifyRet ret = new SDKVerifyRet();
        YingYongBaoYSDKInfo configInfo = (YingYongBaoYSDKInfo) config;
        configInfo = convertYSDKInfo(input, configInfo);
        String platform = input.getExtraKey(PLATFORM);
        ret.setSdkUid(input.getSdkUid());
        ret.setSdkAccessToken(input.getSDKAccessToken());

        if (PLATFORM_TYPE_QQ.equals(platform)) {
            try {
                Map<String, String> qqRequestMap = new HashMap<String, String>();
                //请求时间戳,单位为秒
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                qqRequestMap.put("timestamp", timestamp);
                //应用唯一ID
                qqRequestMap.put("appid", configInfo.getQqAppId());
                //请求sig
                qqRequestMap.put("sig", MD5Util.MD5(configInfo.getQqAppKey() + timestamp));
                //从QQ登陆获取的openId值
                qqRequestMap.put("openid", input.getSdkUid());
                //从QQ登陆获取的openKey值
                qqRequestMap.put("openkey", input.getSDKAccessToken());
                //客户端IP
                qqRequestMap.put("userip", input.getExtraKey(CLIENT_IP));
                String resultStr = HttpUtil.getWithParams(configInfo.getQqRequestUrl(), qqRequestMap);
                if (!StringUtils.isEmpty(resultStr)) {
                    Map<String, Object> resultMap = ParseJson.getJsonContentByStr(resultStr, Map.class);
                    if (null != resultMap && resultMap.containsKey("ret") && 0 == Integer.parseInt((String) resultMap.get("ret"))) {
                        ret.success();
                    }
                }
                ret.fail();
            } catch (Exception e) {
                logger.error("", e);
                ret.fail();
            }
            logger.error("");
            ret.fail();
        } else if (PLATFORM_TYPE_WX.equals(platform)) {
            try {
                Map<String, String> wxRequestMap = new HashMap<String, String>();
                //请求时间戳,单位为秒
                String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
                wxRequestMap.put("timestamp", timestamp);
                //应用唯一ID
                wxRequestMap.put("appid", configInfo.getWxRequestUrl());
                //请求sig
                wxRequestMap.put("sig", MD5Util.MD5(configInfo.getWxAppKey() + timestamp));
                //从WX登陆获取的openId值
                wxRequestMap.put("openid", input.getSdkUid());
                //从WX登陆获取的openKey值
                wxRequestMap.put("openkey", input.getSDKAccessToken());
                //客户端IP
                wxRequestMap.put("userip", input.getExtraKey(CLIENT_IP));
                String resultStr = HttpUtil.getWithParams(configInfo.getWxRequestUrl(), wxRequestMap);
                if (!StringUtils.isEmpty(resultStr)) {
                    Map<String, Object> resultMap = ParseJson.getJsonContentByStr(resultStr, Map.class);
                    if (null != resultMap && resultMap.containsKey("ret") && 0 == Integer.parseInt((String) resultMap.get("ret"))) {
                        ret.success();
                    }
                }
                ret.fail();
            } catch (Exception e) {
                logger.error("", e);
                ret.fail();
            }
            logger.error("");
            ret.fail();
        } else {
            logger.error(getSDKId() + " VerifyUser Error, Platform Error. platform:" + platform);
            ret.fail();
        }
        return ret;
    }

    @Override
    public SDKCheckOrderRet checkOrder(PaymentOrder pOrder, JsonReqObj jsonData, Object config) {
        SDKCheckOrderRet ret = new SDKCheckOrderRet();
        try {
            YingYongBaoYSDKInfo configInfo = (YingYongBaoYSDKInfo) config;
            configInfo = convertYSDKInfo(jsonData, configInfo);

            //应用宝虚拟币兑换率
            int _BALANCE_EXCHANGE_RATE = AndYingYongBaoYSDK.BALANCE_EXCHANGE_RATE;
            if (!StringUtils.isEmpty(configInfo.getBalanceExchangeRate())) {
                _BALANCE_EXCHANGE_RATE = Integer.valueOf(configInfo.getBalanceExchangeRate());
            }
            Map<String, Object> getResultMap = this.getBalance(jsonData, configInfo);
            // 游戏币个数（包含了赠送游戏币）
            int balance = Integer.parseInt(getResultMap.get("balance").toString());
            int decBalance;
            // 判断游戏币余额是否充足
            if (0 == balance) {
                logger.error(getSDKId() + " Check Order Failed. Balance is not enough. result=" + getResultMap.toString());
                ret.fail();
                return ret;
            } else if (1.0 * balance < _BALANCE_EXCHANGE_RATE * pOrder.getOrderAmount()) {
                decBalance = balance;
            } else {
                decBalance = (int) (_BALANCE_EXCHANGE_RATE * pOrder.getOrderAmount());
            }
            // 扣除游戏币
            Map payResultMap = this.payBalance(decBalance, jsonData, configInfo);
            logger.debug(getSDKId() + " checkOrder, payResult:" + payResultMap.get("ret") + ". payResultMap:"
                    + payResultMap.toString());
            if (0 == Integer.parseInt(payResultMap.get("ret").toString())) {
                String billno = payResultMap.get("billno").toString();
                double payAmount = 1.0 * decBalance / _BALANCE_EXCHANGE_RATE;
                return ret.success(billno, payAmount, "RMB", "",
                        ParseJson.encodeJson(jsonData));
            } else if (Integer.parseInt(payResultMap.get("ret").toString()) == 1004) {
                // 余额不足
                logger.error(getSDKId() + " Check Order Failed. Balance is not enough. result=" + payResultMap.toString());
                return ret.fail();
            } else {
                // 其他错误
                logger.error(getSDKId() + " Check Order Failed. result=" + payResultMap.toString());
                return ret.fail();
            }
        } catch (Exception e) {
            logger.error(getSDKId() + " Check Order Failed", e);
            return ret.fail();
        }
    }

    @Override
    public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream, String method, Object config) {
        YingYongBaoYSDKInfo configInfo = (YingYongBaoYSDKInfo) config;
        //返回Mao
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //正确返回的ret及msg
        int ret = 0;
        String msg = "OK";

        try {
            /**
             * 支付流水号（64个字符长度。该字段和openid合起来是唯一的）。
             */
            String billno = paramMap.get("billno");
            /**
             * Q点/Q币消耗金额或财付通游戏子账户的扣款金额。道具寄售目前暂不支持Q点/Q币/财付通游戏子账户，因此该参数的值为0。
             * 以0.1Q点为单位
             */
            String amt = org.springframework.util.StringUtils.isEmpty(paramMap.get("amt")) ? "0" : paramMap.get("amt");
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

            boolean sigCheck = SnsSigCheck.verifySig(method, callBackUrlPath, (HashMap<String, String>) paramMap, secret, sig);

            if (!sigCheck) {
                ret = 4;
                msg = "Sig Check Failed";
            }

            if (ret == 0) {
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
                    ret = 0;
                    msg = "OK";
                } else {
                    ret = 1;
                    msg = "FAIL";
                }
            }
        } catch (Exception e) {
            logger.error(getSDKId() + " Check Order Error.", e);
            ret = 1;
            msg = "Internal Error.";
        }
        resultMap.put("ret", ret);
        resultMap.put("msg", msg);
        return resultMap;
    }

    @Override
    public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
        PostOrderRet ret = new PostOrderRet();
        YingYongBaoYSDKInfo configInfo = (YingYongBaoYSDKInfo) config;
        /**
         * buy_type 如果是1,是购买Q币;,如果是2,是购买道具.
         * 如果是购买道具,则直接返回SuperSDKOrder信息
         */
        String butType = jsonReq.getExtraKey(KEY_PAY_TYPE);
        logger.debug(this.getSDKId() + " postOrder. buyType" + butType);
        if (null != butType && PAY_TYPE_BALANCE.equals(butType)) {
            try {
                ret.setExtra(postOrderBalance(order, configInfo, jsonReq));
                return ret;
            } catch (Exception e) {
                logger.error(getSDKId() + " Post Order Error.", e);
                return ret.fail();
            }
        } else if (null != butType && PAY_TYPE_GOODS.equals(butType)) {
            return ret.ok();
        }
        return ret.ok();
    }

    public YingYongBaoYSDKInfo convertYSDKInfo(JsonReqObj jsonData, YingYongBaoYSDKInfo yingYongBaoYSDKInfo) {
        if (!StringUtils.isEmpty(jsonData.getExtraKey(IS_DEBUG)) && "true".equals(jsonData.getExtraKey(IS_DEBUG))) {
            yingYongBaoYSDKInfo.setQqRequestUrl(yingYongBaoYSDKInfo.getDeBugQqRequestUrl());
            yingYongBaoYSDKInfo.setWxRequestUrl(yingYongBaoYSDKInfo.getDeBugWxRequestUrl());
            yingYongBaoYSDKInfo.setCancelBalanceUrl(yingYongBaoYSDKInfo.getDeBugCancelBalanceUrl());
            yingYongBaoYSDKInfo.setBalanceUrl(yingYongBaoYSDKInfo.getDeBugBalanceUrl());
            yingYongBaoYSDKInfo.setPayBalanceUrl(yingYongBaoYSDKInfo.getDeBugPayBalanceUrl());
        }
        return yingYongBaoYSDKInfo;
    }

    /**
     * 查询游戏币
     */
    private Map<String, Object> getBalance(JsonReqObj jsonData, YingYongBaoYSDKInfo configInfo) throws Exception {
        String cookie = getCookie(jsonData.getExtraKey(PLATFORM), "/mpay/get_balance_m");
        HashMap<String, String> sendParams = getCommonParamMap(jsonData, configInfo, jsonData.getExtraKey(PLATFORM));
        String secret = String.valueOf(configInfo.getQqAppKey()) + "&";
        String sig = SnsSigCheck.makeSig("get", "/mpay/get_balance_m", sendParams, secret);
        sendParams.put("sig", sig);
        String sendStr = generateSendStr(sendParams);
        String sendUrl = configInfo.getBalanceUrl() + "?" + sendStr;
        String result = HttpUtil.get(sendUrl, cookie);
        if (!StringUtils.isEmpty(result)) {
            logger.debug(getSDKId() + " GetBalance SendUrl=" + sendUrl + " cookie=" + cookie + " result=" + result);
            Map<String, Object> resultMap = ParseJson.getJsonContentByStr(result, Map.class);
            if (resultMap != null && resultMap.containsKey("ret") || Integer.parseInt(resultMap.get("ret").toString()) == 0) {
                return resultMap;
            }
        }
        logger.error(getSDKId() + " GetBalance Error. result=" + result);
        throw new Exception(getSDKId() + " GetBalance Error.");
    }

    /**
     * 获取Cookie串
     */

    private String getCookie(String platform, String uriPath) throws UnsupportedEncodingException {
        // 计算cookie
        String session_id = platform.equals(PLATFORM_TYPE_QQ) ? "openid" : "hy_gameid";
        String session_type = platform.equals(PLATFORM_TYPE_QQ) ? "kp_actoken" : "wc_actoken";
        return "session_id=" + URLEncoder.encode(session_id, "utf-8") + ";session_type="
                + URLEncoder.encode(session_type, "utf-8") + ";org_loc=" + URLEncoder.encode(uriPath, "utf-8") + ";";
    }

    /**
     * 获取公共参数
     */
    private HashMap<String, String> getCommonParamMap(JsonReqObj jsonData, YingYongBaoYSDKInfo configInfo, String platform) {
        configInfo = convertYSDKInfo(jsonData, configInfo);
        String openid = String.valueOf(jsonData.getExtraKey(UID));
        String openkey = platform.equals("QQ") ? String.valueOf(jsonData.getExtraKey(QQ_ACCESS_TOKEN))
                : String.valueOf(jsonData.getExtraKey(WX_ACCESS_TOKEN));
        String pay_token = String.valueOf(jsonData.getExtraKey(PAY_TOKEN));
        String appid = String.valueOf(configInfo.getQqAppId());
        int ts = (int) (System.currentTimeMillis() / 1000);
        String pf = String.valueOf(jsonData.getExtraKey(PF));
        String pfkey = String.valueOf(jsonData.getExtraKey(PF_KEY));
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
     * 遍历Map得到string串
     */
    private String generateSendStr(HashMap<String, String> sendParams) throws UnsupportedEncodingException {
        StringBuilder sendStrBuf = new StringBuilder("");
        for (Map.Entry<String, String> entry : sendParams.entrySet()) {
            sendStrBuf.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"))
                    .append("&");
        }
        // 删除最后一个&
        sendStrBuf.deleteCharAt(sendStrBuf.length() - 1);
        return sendStrBuf.toString();
    }

    /**
     * 支付虚拟游戏币
     */
    private Map payBalance(int decBalance, JsonReqObj jsonData, YingYongBaoYSDKInfo configInfo) throws Exception {
        String platform = String.valueOf(jsonData.getExtraKey(PLATFORM));

        String cookie = getCookie(platform, "/mpay/pay_m");

        HashMap<String, String> sendParams = getCommonParamMap(jsonData, configInfo, platform);
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
     * 游戏币购买方式的的postOrder操作
     */
    @SuppressWarnings("rawtypes")
    private Map<String, String> postOrderBalance(PaymentOrder order, YingYongBaoYSDKInfo configInfo, JsonReqObj jsonReq)
            throws Exception {

        // 应用宝虚拟币兑换率
        int _BALANCE_EXCHANGE_RATE = AndYingYongBaoYSDK.BALANCE_EXCHANGE_RATE;
        if (null != configInfo.getBalanceExchangeRate() && !"".equals(configInfo.getBalanceExchangeRate())) {
            _BALANCE_EXCHANGE_RATE = Integer.valueOf(configInfo.getBalanceExchangeRate());
        }
        Map<String, String> retMap = new HashMap<String, String>();
        // 查询游戏币
        Map getResultMap = this.getBalance(jsonReq, configInfo);
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
     * 取消游戏币支付
     */
    private void cancelPay(int decBalance, JsonReqObj req, YingYongBaoYSDKInfo configInfo) throws Exception {
        String platform = String.valueOf(req.getExtraKey(PLATFORM));
        String cookie = getCookie(platform, "/mpay/cancel_pay_m");
        HashMap<String, String> sendParams = getCommonParamMap(req, configInfo, platform);
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

    /**
     * 根据SuperSDK订单号获取支付回调地址
     *
     * @param orderId orderId
     * @return 支付回调地址(不包含域名)
     */
    public String getPayBackUrl(String orderId) {
        logger.debug("PathUtil getPayBackUrl. orderId:" + orderId);
        //保存当前AppId
        String localAppId = ThreadHelper.getAppId();
        //根据订单号获取渠道ID
        PaymentOrder paymentOrder = paymentOrderDao.queryByOrderId(orderId);
        if (null == paymentOrder)
            return "";
        String channelId = paymentOrder.getAppChannelId();
        if (org.springframework.util.StringUtils.isEmpty(channelId))
            return "";
        ThreadHelper.setAppId(null);
        //根据AppId获取shortId
        AppConfig appConfig = appConfigDao.findById(localAppId);
        if (null == appConfig)
            return "";
        String shortId = appConfig.getShortAppId();
        if (org.springframework.util.StringUtils.isEmpty(shortId))
            return "";
        //操作完毕,将数据源切换到原始状态
        ThreadHelper.setAppId(localAppId);
        logger.debug("PathUtil getPayBackUrl. payBackUrl:" + "supersdk-web/payback/" + shortId + "/" + channelId);
        return "/supersdk-web/payback/" + shortId + "/" + channelId;
    }
}
