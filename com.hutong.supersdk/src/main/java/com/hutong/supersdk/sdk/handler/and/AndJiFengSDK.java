package com.hutong.supersdk.sdk.handler.and;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.jifeng.JiFengObj;
import com.hutong.supersdk.sdk.modeltools.jifeng.JiFengSDKInfo;
import com.hutong.supersdk.sdk.utils.jifeng.GfanMd5Util;

@Component("andJiFengSDK")
public class AndJiFengSDK implements IVerifyUserSDK, IPayCallBackSDK {
	
	public final String SDK_ID = "AndJiFeng";
	
	private final static Log logger = LogFactory.getLog(AndJiFengSDK.class);
	
	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return JiFengSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		logger.debug(this.getSDKId() + " payCallBack Start! paramMap:" + paramMap.toString() + "config:" + config.toString());
		try {
			JiFengSDKInfo configInfo = (JiFengSDKInfo) config;
			// 循环读取直到结束
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = servletInputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, read);
			}
			String xmlStr = new String(baos.toByteArray(), "utf-8");
            JiFengObj jiFengObj = getJiFengObj(xmlStr);
            if (StringUtils.isEmpty(xmlStr) || jiFengObj == null) {
                logger.error("xmlStr==null || xmlStr.equals(\"\") and is " + xmlStr);
                return generatePaybackResult("0", "readXMLError");
            }
			String time = String.valueOf(paramMap.get("time"));
			String ID = configInfo.getJiFengID();
			String signStr = ID + time;
			String mySign = GfanMd5Util.getMD5(signStr.getBytes(), false);
			String sign = String.valueOf(paramMap.get("sign"));
			if(!mySign.equalsIgnoreCase(sign)){
                logger.error("JiFeng sign is wrong!!! mySign="+mySign+"---sign="+sign+"---time="+time+"---ID="+ID);
                return generatePaybackResult("0", "signError");
			}
			String platformId = jiFengObj.getCreate_time();
            platformId = StringUtils.isEmpty(platformId) ? "JiFeng order id is empty" : platformId;
			String orderId = jiFengObj.getOrder_id();
			String payWay = "";
			double CostMoney = Double.parseDouble(jiFengObj.getCost())/10;
			boolean iResult = callback.succeedPayment(orderId, platformId, CostMoney, "RMB",
                    payWay, xmlStr);
			if(iResult)
				return generatePaybackResult("1", "Success");
            return generatePaybackResult("0", "Internal Error");
		} catch (Exception e) {
			logger.error("", e);
			return generatePaybackResult("0", "Exception");
		}
	}

	private static String generatePaybackResult(String errorCode, String desc) {
		return "<response><ErrorCode>" + errorCode + "</ErrorCode><ErrorDesc>" + desc + "</ErrorDesc></response>";
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
            JiFengSDKInfo configInfo = (JiFengSDKInfo) config;
            String platformSessionId = input.getSDKAccessToken();
            String url = configInfo.getRequestUrl();
            Map<String, String> paramMap = new HashMap<String, String>();
            paramMap.put("token", platformSessionId);
            String result = HttpUtil.postForm(url, paramMap);
			Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
			if (resultMap == null || !"1".equals(String.valueOf(resultMap.get("resultCode")))) {
				logger.error(this.getSDKId() + " Login Error. resultMap:" + resultMap);
				ret.setErrorMsg(this.getSDKId() + " Login Error.");
				return ret.fail();
			}
			String platformUserId = String.valueOf(resultMap.get("uid"));
			ret.setSdkUid(platformUserId);
			ret.setSdkAccessToken(platformSessionId);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
			return ret.fail();
		}
	}
	
	private JiFengObj getJiFengObj(String xmlData) {
        if (StringUtils.isEmpty(xmlData))
            return null;

		try {
            JiFengObj jiFengObj = new JiFengObj();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dbBuilder = dbFactory.newDocumentBuilder();

			Document doc = dbBuilder.parse(new InputSource(new StringReader(xmlData)));

			String order_id = doc.getElementsByTagName("order_id").item(0).getFirstChild().getNodeValue();
			jiFengObj.setOrder_id(order_id);
			String cost = doc.getElementsByTagName("cost").item(0).getFirstChild().getNodeValue();
			jiFengObj.setCost(cost);
			String appkey = doc.getElementsByTagName("appkey").item(0).getFirstChild().getNodeValue();
			jiFengObj.setAppkey(appkey);
			String create_time = doc.getElementsByTagName("create_time").item(0).getFirstChild().getNodeValue();
			jiFengObj.setCreate_time(create_time);

            return jiFengObj;
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
	}
}
