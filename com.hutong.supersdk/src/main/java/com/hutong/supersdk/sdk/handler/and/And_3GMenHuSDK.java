package com.hutong.supersdk.sdk.handler.and;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools._3gmenhu.ThirdGData;
import com.hutong.supersdk.sdk.modeltools._3gmenhu._3GMenHuSDKInfo;
import com.hutong.supersdk.sdk.utils.MD5Util;

@Component("and_3GMenHuSDK")
public class And_3GMenHuSDK implements IVerifyUserSDK, IPayCallBackSDK, ICheckOrderSDK {

	private final String SDK_ID = "And3GMenHu";

	private final static Log logger = LogFactory.getLog(And_3GMenHuSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return _3GMenHuSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack. start!");
		String ok = "ok";
		String fail = "fail";
		_3GMenHuSDKInfo configInfo = (_3GMenHuSDKInfo) config;
		try {
			/**
			 * 效验字段 keyStr = 该字段的生成步骤方法如下： 1、拼接下面数据得到keystr
			 * keystr=md5key+"_"+orderid+"_"+gameid+"_"+token;
			 * 其中md5key是2324分配的参数。 2、对keystr进行MD5所得的结果就是key。
			 */
			String key = paramMap.get("key");
			String data = paramMap.get("data");
			logger.debug(this.getSDKId() + " payCallBack. data:" + data);
			ThirdGData _3GData = this.get3GData(data);
			String orderId = _3GData.getCporderid();
			String platformOId = _3GData.getOrderid();
			String md5Key = configInfo.getMd5key();
			//检查签名
			String checkKey = MD5Util.MD5(md5Key + "_" + platformOId + "_" + _3GData.getGameid() + "_" + _3GData.getToken());
			logger.debug(this.getSDKId() + " payCallBack. key:" + key + " keyStr:" + checkKey);
			if (StringUtil.equalsStringIgnoreCase(key, checkKey)) {
				if (1 == _3GData.getAccess()) {
					double paytotalfee = _3GData.getPaytotalfee();
					String paytypeid = String.valueOf(_3GData.getPaytypeid());
					boolean iResult = callback.succeedPayment(orderId, platformOId, paytotalfee, "RMB",
							paytypeid, ParseJson.encodeJson(paramMap));
					if (iResult) {
						return ok;
					}
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
		logger.info(this.getSDKId() + " verifyUser. start!");
//		SDKVerifyRet ret = new SDKVerifyRet();
//		_3GMenHuSDKInfo configInfo = (_3GMenHuSDKInfo) config;
//		try {
//			// 获取参数
//			String token = input.getSDKAccessToken();
//			String secreyKey = configInfo.getSecreyKey();
//			String gameId = input.getExtraKey("game_id");
//			// 组装sign字符串
//			String signStr = gameId + token + secreyKey;
//			// 进行MD5加密
//			String sign = MD5Util.MD5(signStr);
//			// 组装请求参数
//			Map<String, String> paramMap = new HashMap<String, String>();
//			paramMap.put("token", token);
//			paramMap.put("secret_key", secreyKey);
//			paramMap.put("game_id", gameId);
//			paramMap.put("sign", sign);
//			String url = configInfo.getRequestUrl();
//
//			String result = HttpUtil.httpPost(url, paramMap);
//			JsonResObj jsonRes = ParseJson.getJsonContentByStr(result, JsonResObj.class);
//			if (jsonRes.isStatusOk()) {
//				ret.setSdkUid(jsonRes.getData().get("uid"));
//				ret.setSdkAccessToken(token);
//				ret.success();
//				return ret;
//			} else {
//				ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
//				ret.fail();
//				return ret;
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//			ret.fail();
//			return ret;
//		}
		
		SDKVerifyRet ret = new SDKVerifyRet();
		ret.setSdkUid(input.getSdkUid());
		ret.success();
		return ret;
	}
	
	private ThirdGData get3GData(String xmlData) 
			throws ParserConfigurationException, SAXException, IOException {

		ThirdGData _3gObj = new ThirdGData();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();

		Document doc = dbBuilder.parse(new InputSource(new StringReader(xmlData)));

		String orderId = doc.getElementsByTagName("orderid").item(0).getFirstChild().getNodeValue();
		_3gObj.setOrderid(orderId);

		String gameid = doc.getElementsByTagName("gameid").item(0).getFirstChild().getNodeValue();
		_3gObj.setGameid(Integer.parseInt(gameid));

		String token = doc.getElementsByTagName("token").item(0).getFirstChild().getNodeValue();
		_3gObj.setToken(token);

		String cpid = doc.getElementsByTagName("cpid").item(0).getFirstChild().getNodeValue();
		_3gObj.setCpid(Integer.parseInt(cpid));

		String access = doc.getElementsByTagName("access").item(0).getFirstChild().getNodeValue();
		_3gObj.setAccess(Integer.parseInt(access));

		String paytotalfee = doc.getElementsByTagName("paytotalfee").item(0).getFirstChild().getNodeValue();
		_3gObj.setPaytotalfee(Float.parseFloat(paytotalfee));

		String cporderid = doc.getElementsByTagName("cporderid").item(0).getFirstChild().getNodeValue();
		_3gObj.setCporderid(cporderid);

		String stime = doc.getElementsByTagName("stime").item(0).getFirstChild().getNodeValue();
		_3gObj.setStime(stime);

		return _3gObj;
	}

	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
									   Object config) {
		logger.info(this.getSDKId() + " checkOrder. start!");
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		_3GMenHuSDKInfo configInfo = (_3GMenHuSDKInfo) config;
		try {
			String orderid = pOrder.getSdkOrderId();
			if(!StringUtils.isEmpty(orderid)){
				String md5key = configInfo.getMd5key();
				String gameid = configInfo.getAppId();
				String keyStr = md5key + "_" + orderid + "_" + gameid + "_" + jsonData.getSDKAccessToken(); 
				
				logger.debug(this.getSDKId() + " checkOrder. keyStr:" + keyStr);
				
				String md5 = MD5Util.MD5(keyStr);
				String checkOrderUrl = configInfo.getCheckOrderUrl();
				String url = checkOrderUrl + "?md5=" + md5 + "&order=" + orderid;
				
				logger.debug(this.getSDKId() + " checkOrder. url:" + url);
				
				String result = HttpUtil.get(url);
				
				logger.debug(this.getSDKId() + " checkOrder. result:" + result);
				
				if(!StringUtils.isEmpty(result)){
					ThirdGData _3GData = this.get3GData(result);
					
					logger.debug(this.getSDKId() + " checkOrder. access:" + _3GData.getAccess());
					
					if(!StringUtils.isEmpty(_3GData.getAccess()) && 1 == _3GData.getAccess()) {
						ret.success(pOrder.getSdkOrderId(), pOrder.getOrderAmount(), "RMB", pOrder.getPayType(), ParseJson.encodeJson(pOrder));
						return ret;
					}
				}
			}
			ret.fail();
			return ret;
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			return ret;
		}
	}

//	public static void main(String[] args) {
//		String data = "<?xml version=\"1.0\" encoding=\"utf-8\"?><root><orderid>1300000</orderid><gameid>2715</gameid><token>d38bf7b344c3982055022c98c489c7eb</token><cpid>2703</cpid><access>1</access><paytotalfee>100.0</paytotalfee><paytypeid>57</paytypeid><cporderid>cporderid_34f3h87</cporderid><stime>2015-06-01 06:01:02</stime></root>";
//	}
}
