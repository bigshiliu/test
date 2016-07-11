package com.hutong.supersdk.sdk.handler.and;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;
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
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.jinli.JinLiSDKInfo;
import com.hutong.supersdk.sdk.utils.jinli.JLRSASignature;
import com.hutong.supersdk.sdk.utils.jinli.JinLiRSASignature;

@Component("andJinLiSDK")
public class AndJinLiSDK implements IVerifyUserSDK, IPayCallBackSDK, IPostOrderSDK {

	private static final String SDK_ID = "AndJinLi";

	private final static Log logger = LogFactory.getLog(AndJinLiSDK.class);

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return JinLiSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj input) {
		PostOrderRet postRet = new PostOrderRet();
		try {
			JinLiSDKInfo configInfo = (JinLiSDKInfo) config;
			// 包装发送给金立的格式信息
			Map<String, String> sendMsgMap = new HashMap<String, String>();
			sendMsgMap.put("player_id", input.getSdkUid());
			sendMsgMap.put("api_key", configInfo.getApiKey());
			DecimalFormat df = new DecimalFormat("######0.00");
			String deal_price = df.format(order.getOrderAmount());
			sendMsgMap.put("deal_price", deal_price);
			sendMsgMap.put("deliver_type", "1");
			sendMsgMap.put("notify_url", configInfo.getNotifyUrl());
			sendMsgMap.put("out_order_no", order.getOrderId());
			sendMsgMap.put("subject", order.getAppProductName());
			DateFormat formatterDate = new SimpleDateFormat("yyyyMMddHHmmss");
			String submit_time = formatterDate.format(System.currentTimeMillis());
			sendMsgMap.put("submit_time", submit_time);
			String expire_time = formatterDate.format(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 5));
			sendMsgMap.put("expire_time", expire_time);
			sendMsgMap.put("total_fee", deal_price);
			String signStr = sendMsgMap.get("api_key") + sendMsgMap.get("deal_price")
                    + sendMsgMap.get("deliver_type") + sendMsgMap.get("expire_time")
                    + sendMsgMap.get("notify_url") + sendMsgMap.get("out_order_no")
                    + sendMsgMap.get("subject") + sendMsgMap.get("submit_time")
                    + sendMsgMap.get("total_fee");
			logger.debug("JinLi createOrder signStr=" + signStr);
            String sign = JLRSASignature.sign(signStr, configInfo.getPrivateKey(), CharEncoding.UTF_8);
			logger.debug("JinLi createOrder sign=" + sign);
			sendMsgMap.put("sign", sign);
			String returnStr = HttpUtil.postJson(configInfo.getCreateorderurl(), ParseJson.encodeJson(sendMsgMap));
			logger.debug("JinLi createOrder return=" + returnStr);

            Map retMap = ParseJson.getJsonContentByStr(returnStr, Map.class);
			if (retMap != null && "200010000".equals(String.valueOf(retMap.get("status")))) {
                String time = StringUtils.isEmpty(retMap.get("submit_time")) ? submit_time : String.valueOf(retMap.get("submit_time"));
                postRet.setExtra("submit_time", time);
                return postRet.ok();
            }
		} catch (Exception e) {
			logger.error("", e);
		}
        return postRet.fail();
    }

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {

        String success = "success";
        String fail = "fail";
        try {
            JinLiSDKInfo configInfo = (JinLiSDKInfo) config;
			Map<String, String> map = new HashMap<String, String>();
			String sign = paramMap.get("sign");

            for (String key : paramMap.keySet())
                if (!"sign".equals(key) && !"msg".equals(key)) // sign和msg不参与签名
                    map.put(key, paramMap.get(key));
			
			StringBuilder contentBuffer = new StringBuilder();
			Set<String> signParamArray = map.keySet();
            List<String> sortedParams = new ArrayList<String>(signParamArray);
            Collections.sort(sortedParams);
            for (int i = 0; i < sortedParams.size(); i++) {
                String key = sortedParams.get(i);
                String value = map.get(key);
                contentBuffer.append(key).append("=").append(value);
                if(i < signParamArray.size() - 1){
                    contentBuffer.append("&");
                }
            }

			String content = contentBuffer.toString();
			if (StringUtils.isEmpty(sign)) {
				logger.error("JinLi sign isBlank!");
				return fail;
			}
			String publicKey = configInfo.getPublicKey();
            boolean isValid = JinLiRSASignature.doCheck(content, sign, publicKey, CharEncoding.UTF_8);
			if (!isValid) {
				logger.error("JinLi sign is wrong!");
				return fail;
			}
			String platformId = map.get("submit_time");
            platformId = StringUtils.isEmpty(platformId) ? "JinLi order id is empty" : platformId;

			String orderId = map.get("out_order_no");
			double CostMoney = Double.parseDouble(map.get("deal_price"));
			boolean iResult = callback.succeedPayment(orderId, platformId, CostMoney, "RMB", "", ParseJson.encodeJson(paramMap));
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
            String platformUserId = input.getSdkUid();
            String platformSessionId = input.getSDKAccessToken();
            if (!verify(platformSessionId, config)) {
				ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
                return ret.fail();
			}
            ret.setSdkUid(platformUserId);
            ret.setSdkAccessToken(platformSessionId);
            return ret.success();
        } catch (Exception e) {
            logger.error("", e);
            ret.setErrorMsg(this.getSDKId() + " verifyUser Error");
            return ret.fail();
        }
	}

	public boolean verify(String amigoToken, Object config) throws Exception {
		JinLiSDKInfo configInfo = (JinLiSDKInfo) config;
		// 取得平台配置信息
		// Map<String, String> confiMap = getFuncContext().platformFun
		// .getPlatFormConfigInfo(ConfigPlatformStatic.JIN_LI);

		String host = configInfo.getHost();
		String port = configInfo.getPort();
		String secretKey = configInfo.getSecretKey();
		String apiKey = configInfo.getApiKey();
		String url = configInfo.getUrl();
		String verify_url = configInfo.getVerifyUrl();

		TrustManager[] tm = {new MyX509TrustManager()};
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL sendUrl = new URL(verify_url);
        HttpsURLConnection httpURLConnection = (HttpsURLConnection) sendUrl.openConnection();
        try {
            httpURLConnection.setSSLSocketFactory(ssf);
            httpURLConnection.setDoInput(true); // true表示允许获得输入流,读取服务器响应的数据,该属性默认值为true
            httpURLConnection.setDoOutput(true); // true表示允许获得输出流,向远程服务器发送数据,该属性默认值为false
            httpURLConnection.setUseCaches(false); // 禁止缓存
            int timeout = 30000;
            httpURLConnection.setReadTimeout(timeout); // 30秒读取超时
            httpURLConnection.setConnectTimeout(timeout); // 30秒连接超时
            String method = "POST";
            httpURLConnection.setRequestMethod(method);
            httpURLConnection.setRequestProperty("Authorization",
                    builderAuthorization(host, port, secretKey, apiKey, method, url));
            OutputStream out = httpURLConnection.getOutputStream();
            out.write(amigoToken.getBytes());
            out.flush();
            out.close();
            InputStream in = httpURLConnection.getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                buffer.write(buff, 0, len);
            }
            logger.debug(String.format("validSign127 success response:%s", buffer.toString()));
            return true;
        }
        catch (Exception ex) {
            logger.error("", ex);
            return false;
        }
        finally {
            httpURLConnection.disconnect();
        }
	}

	static class CamelUtility {
		public static final int SizeOfUUID = 16;
		private static final int SizeOfLong = 8;
		private static final int BitsOfByte = 8;
		private static final int MBLShift = (SizeOfLong - 1) * BitsOfByte;

		private static final char[] HEX_CHAR_TABLE = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C',
				'D', 'E', 'F' };

		public static String uuidToString(UUID uuid) {
			long[] ll = { uuid.getMostSignificantBits(), uuid.getLeastSignificantBits() };
			StringBuilder str = new StringBuilder(SizeOfUUID * 2);
            for (long aLl : ll) {
                for (int i = MBLShift; i > 0; i -= BitsOfByte)
                    formatAsHex((byte) (aLl >>> i), str);
                formatAsHex((byte) (aLl), str);
            }
			return str.toString();
		}

		public static void formatAsHex(byte b, StringBuilder s) {
			s.append(HEX_CHAR_TABLE[(b >>> 4) & 0x0F]);
			s.append(HEX_CHAR_TABLE[b & 0x0F]);
		}

	}

	static class StringUtil {
		public static final String UTF8 = "UTF-8";
		private static final byte[] BYTEARRAY = new byte[0];

		public static boolean isNullOrEmpty(String s) {
            return s == null || s.isEmpty() || s.trim().isEmpty();
        }

		public static String randomStr() {
			return CamelUtility.uuidToString(UUID.randomUUID());
		}

		public static byte[] getBytes(String value) {
			return getBytes(value, UTF8);
		}

		public static byte[] getBytes(String value, String charset) {
			if (isNullOrEmpty(value))
				return BYTEARRAY;
			if (isNullOrEmpty(charset))
				charset = UTF8;
			try {
				return value.getBytes(charset);
			} catch (UnsupportedEncodingException e) {
				return BYTEARRAY;
			}
		}
	}

	static class CryptoUtility {

		private static final String MAC_NAME = "HmacSHA1";

		public static String macSig(String host, String port, String macKey, String timestamp, String nonce,
				String method, String uri) {
			// 1. build mac string
			// 2. hmac-sha1
			// 3. base64-encoded

            String text = timestamp + "\n" +
                    nonce + "\n" +
                    method.toUpperCase() + "\n" +
                    uri + "\n" +
                    host.toLowerCase() + "\n" +
                    port + "\n" +
                    "\n";

			byte[] ciphertext;
			try {
				ciphertext = hmacSHA1Encrypt(macKey, text);
			} catch (Throwable e) {
				logger.error("", e);
				return null;
			}
            return new String(Base64.encodeBase64(ciphertext));
		}

		public static byte[] hmacSHA1Encrypt(String encryptKey, String encryptText)
				throws InvalidKeyException, NoSuchAlgorithmException {
			Mac mac = Mac.getInstance(MAC_NAME);
			mac.init(new SecretKeySpec(StringUtil.getBytes(encryptKey), MAC_NAME));
			return mac.doFinal(StringUtil.getBytes(encryptText));
		}
	}

	private static String builderAuthorization(String host, String port, String secretKey, String apiKey, String method,
			String url) {

		Long ts = System.currentTimeMillis() / 1000;
		String nonce = StringUtil.randomStr().substring(0, 8);
		String mac = CryptoUtility.macSig(host, port, secretKey, ts.toString(), nonce, method, url);

        return "MAC " +
                String.format("id=\"%s\"", apiKey) +
                String.format(",ts=\"%s\"", ts) +
                String.format(",nonce=\"%s\"", nonce) +
                String.format(",mac=\"%s\"", mac);
	}

	static class MyX509TrustManager implements X509TrustManager {

		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

		}

		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

	}
}
