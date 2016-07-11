package com.hutong.supersdk.sdk.handler.and;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.mumayi.MMYPayResult;
import com.hutong.supersdk.sdk.modeltools.mumayi.MuMaYiSDKInfo;
import com.hutong.supersdk.sdk.utils.mumayi.Base64;

@Component("andMuMaYiSDK")
public class AndMuMaYiSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public final String SDK_ID = "AndMuMaYi";
	
	private static final Log logger = LogFactory.getLog(AndMuMaYiSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return MuMaYiSDKInfo.class;
	}
	
	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack Start !!! paramMap:" + paramMap.toString());
		String xmlStr = null;
		MuMaYiSDKInfo configInfo = (MuMaYiSDKInfo) config;
		try {
			InputStream in = servletInputStream;
			ByteArrayOutputStream baos = null;
			// 循环读取直到结束
			baos = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int read = 0;
			while ((read = in.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			xmlStr = new String(baos.toByteArray(), "utf-8");
		} catch (Exception e) {
			logger.error("", e);
		}
		logger.debug("MMYSDK: InputStream=" + xmlStr);
		MMYPayResult mmyPayResult = ParseJson.getJsonContentByStr(xmlStr, MMYPayResult.class);
		if(!"".equals(mmyPayResult.getTradeState()) && "success".equals(mmyPayResult.getTradeState())){
			boolean signResult = verify(mmyPayResult.getTradeSign(), configInfo.getAppKey(), mmyPayResult.getOrderID());
			if(signResult){
				boolean iResult = callback.succeedPayment(mmyPayResult.getProductDesc(), mmyPayResult.getOrderID(), Double.parseDouble(mmyPayResult.getProductPrice()), "RMB", mmyPayResult.getPayType(), xmlStr);
				logger.debug(this.getSDKId() + " payCallBack iResult:" + iResult);
				if(iResult){
					return "success";
				}else{
					return "fail";
				}
			}else{
				return "fail";
			}
		}
		return "fail";
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser start !!!");
		SDKVerifyRet ret = new SDKVerifyRet();
		MuMaYiSDKInfo configInfo = (MuMaYiSDKInfo) config;
		String token = input.getSDKAccessToken();
		String uId = input.getSdkUid();
		String uName = input.getExtraKey("uname");
		String session = input.getExtraKey("session");
		String url = configInfo.getRequestUrl();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("uname", uName);
		paramMap.put("uid", uId);
		paramMap.put("token", token);
		paramMap.put("session", session);
		try {
			String result = HttpUtil.postForm(url, paramMap);
			logger.debug(result);
			if(!"".equals(result) && "success".equals(result)){
				ret.setSdkAccessToken(token);
				ret.setSdkUid(uId);
				ret.setExtra("unama", uName);
				ret.success();
				return ret;
			}else if(!"".equals(result) && "fail".equals(result)){
				logger.info(this.getSDKId() + " veriftUser Error. result:" + result);
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " veriftUser Error.");
				return ret;
			}else{
				logger.info(this.getSDKId() + " veriftUser Error. result:" + result);
				ret.fail();
				ret.setErrorMsg(this.getSDKId() + " veriftUser Error.");
				return ret;
			}
		} catch (Exception e) {
			logger.error("", e);
			ret.fail();
			ret.setErrorMsg(this.getSDKId() + " veriftUser Error.");
			return ret;
		}
	}
	
	public boolean verify(String sign , String appKey , String orderId){
		if(sign.length()<14){
			return false;
		}
		String verityStr = sign.substring(0,8);   
		sign = sign.substring(8);		
		String temp = toMD5(sign);			
		if(!verityStr.equals(temp.substring(0,8))){
			return false;
		}
		String keyB =  sign.substring(0,6);
			
		String randKey = keyB+appKey;
			
		randKey = toMD5(randKey);
			
		byte[] signB = Base64.decodeFast(sign.substring(6));
		int signLength = signB.length;
		String verfic="";
		for(int i =0 ; i< signLength ; i++){
			char b = (char)(signB[i]^randKey.getBytes()[i%32]);
			verfic +=String.valueOf(b);
		}
		return verfic.equals(orderId);	
	}
	
	public static String toMD5(String plain) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(plain.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	/**
	 * @param xmlData
	 * @return
	 */
	@SuppressWarnings("unused")
	private MMYPayResult getMMYObj(String xmlData) {

		MMYPayResult mmyPayResult = new MMYPayResult();

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();

			Document doc = dbBuilder.parse(new InputSource(new StringReader(xmlData)));

			String uid = doc.getElementsByTagName("uid").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setUid(uid);
			String orderID = doc.getElementsByTagName("orderID").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setOrderID(orderID);
			String orderTime = doc.getElementsByTagName("orderTime").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setOrderTime(orderTime);
			String prodctName = doc.getElementsByTagName("prodctName").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setProdctName(prodctName);
			String productDesc = doc.getElementsByTagName("productDesc").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setProductDesc(productDesc);
			String productPrice = doc.getElementsByTagName("productPrice").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setProductPrice(productPrice);
			String tradeSign = doc.getElementsByTagName("tradeSign").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setTradeSign(tradeSign);
			String tradeState = doc.getElementsByTagName("tradeState").item(0).getFirstChild().getNodeValue();
			mmyPayResult.setTradeState(tradeState);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return mmyPayResult;
	}

}
