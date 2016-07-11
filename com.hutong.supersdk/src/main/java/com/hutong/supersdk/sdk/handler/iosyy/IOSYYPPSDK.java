package com.hutong.supersdk.sdk.handler.iosyy;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.pp.PPData;
import com.hutong.supersdk.sdk.modeltools.pp.PPRes;
import com.hutong.supersdk.sdk.modeltools.pp.PPSDKInfo;
import com.hutong.supersdk.sdk.modeltools.pp.PPUserObj;
import com.hutong.supersdk.sdk.utils.Base64;
import com.hutong.supersdk.sdk.utils.MD5Util;
import com.hutong.supersdk.sdk.utils.pp.PPRSAEncrypt;

@Component("iOSYYPPSDK")
public class IOSYYPPSDK implements IVerifyUserSDK, IPayCallBackSDK {

	private static final String SDK_ID = "IOSYYPPTools";

	private final static Log logger = LogFactory.getLog(IOSYYPPSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return PPSDKInfo.class;
	}

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
		String success = "success";
		String fail = "fail";
		try {
			PPSDKInfo configInfo = (PPSDKInfo) config;
			String platformOrderId = paramMap.get("order_id");
			String orderId = paramMap.get("billno");
			int status = Integer.parseInt(paramMap.get("status"));
			double amount = Double.parseDouble(paramMap.get("amount"));
			String appId = configInfo.getAppId();
			String pubKey = configInfo.getPubKey();
			String sign = paramMap.get("sign");
			byte[] privateData = Base64.decode(sign);
			PPRSAEncrypt rsa = new PPRSAEncrypt();
			rsa.loadPublicKey(pubKey);
			String data = new String(rsa.decrypt(rsa.getPublicKey(), privateData));

			PPData dataObj = ParseJson.getJsonContentByStr(data, PPData.class);

			if (dataObj == null || !(dataObj.getApp_id().equals(appId) && dataObj.getOrder_id().equals(platformOrderId)
					&& dataObj.getBillno().equals(orderId) && dataObj.getAmount() == amount
					&& dataObj.getStatus() == status)) {
				logger.error("Check Order Failed. paraMap=" + paramMap.toString() + " decodeSign=" + data);
				return fail;
			}
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, amount, "RMB", "", ParseJson.encodeJson(paramMap));
			if(iResult)
				return success;
		} catch (Exception e) {
			logger.error("", e);
		}
		return fail;
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			// pp助手请求对象
			PPRes pp = new PPRes();
			// 配置参数
			PPSDKInfo configInfo = (PPSDKInfo) config;
			String gameId = configInfo.getAppId();
			String appKey = configInfo.getAppKey();
			String token = input.getSDKAccessToken();
			String service = "account.verifySession";
			String encrypt = "MD5";
			Long time = System.currentTimeMillis() / 1000;

			pp.setId(time.intValue());
			pp.setService(service);
			pp.getData().put("sid", token);
			pp.getGame().put("gameId", Integer.parseInt(gameId));
			pp.setEncrypt(encrypt);

			String sign = MD5Util.MD5("sid=" + token + appKey);
			pp.setSign(sign);

			String json = ParseJson.encodeJson(pp);

			if (json == null)
				return ret.fail();

			String urlStr = configInfo.getRequestUrl();
			byte datas[] = json.getBytes();

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("User_Agent", "25PP");
			conn.setRequestProperty("Connection", "close");
			// 长度是实体的二进制长度
			conn.setRequestProperty("Content-Length", String.valueOf(datas.length));
			conn.getOutputStream().write(datas);
			conn.getOutputStream().flush();
			conn.getOutputStream().close();

            InputStream ppServer = conn.getInputStream();

			String response = new String(readStream(ppServer));
            PPUserObj retObj = ParseJson.getJsonContentByStr(response, PPUserObj.class);
            if (retObj == null || 1 != Integer.parseInt(String.valueOf(retObj.getState().get("code")))) {
                ret.fail();
                ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
                return ret;
            }
			ret.setSdkUid(retObj.getData().get("accountId"));
			ret.setExtra("creator", retObj.getData().get("creator"));
			ret.setSdkAccessToken(token);
			return ret.success();
		} catch (Exception e) {
			logger.error("", e);
        }
        ret.setErrorMsg(this.getSDKId() + " verifyUser Error !");
        return ret.fail();
    }

	/**
	 * 读取流
	 * 
	 * @param inStream inStream
	 * @return 字节数组
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inStream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		return outSteam.toByteArray();
	}
}
