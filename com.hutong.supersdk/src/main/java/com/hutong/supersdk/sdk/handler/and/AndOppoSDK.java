package com.hutong.supersdk.sdk.handler.and;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.oppo.OppoBriefUser;
import com.hutong.supersdk.sdk.modeltools.oppo.OppoSDKInfo;
import com.nearme.oauth.model.AccessToken;
import com.nearme.oauth.open.AccountAgent;

@Component("andOppoSDK")
public class AndOppoSDK implements IPayCallBackSDK, IVerifyUserSDK {

	public static final String SDK_ID = "AndOppo";

	private static final Log logger = LogFactory.getLog(AndOppoSDK.class);

	private static final String VERSION_200 = "2.0.0";

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return OppoSDKInfo.class;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		String sdkVersion = input.getExtraKey("sdk_version");
		if(VERSION_200.equals(sdkVersion)){
			return verifyUser200(input, config);
		}else{
			SDKVerifyRet ret = new SDKVerifyRet();
            try {
                String oauthToken = input.getSDKAccessToken();
                String oauthTokenSecret = input.getExtraKey("token_secret");

                String gcUserInfo = AccountAgent.getInstance().getGCUserInfo(new AccessToken(oauthToken, oauthTokenSecret));

                Map resMap = ParseJson.getJsonContentByStr(gcUserInfo, Map.class);

                if (resMap != null) {
                    String userStr = ParseJson.encodeJson(resMap.get("BriefUser"));
                    OppoBriefUser user = ParseJson.getJsonContentByStr(userStr, OppoBriefUser.class);

                    if (null == user) {
                        ret.setErrorMsg(this.getSDKId() + " Login Error. ");
                        return ret.fail();
                    }

                    ret.setSdkUid(user.getId());
                    ret.setSdkAccessToken(oauthToken);
                    return ret.success();
                }
                else {
                    ret.setErrorMsg("gcUserInfo error.");
                }
            } catch (Exception e) {
                logger.error("", e);
                ret.setErrorMsg(e.getMessage());
            }
            return ret.fail();
        }
	}

	@SuppressWarnings("rawtypes")
	private SDKVerifyRet verifyUser200(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " veriftUser 2.0.0 start.");
		SDKVerifyRet ret = new SDKVerifyRet();
        try {
            String ssoid = input.getSdkUid();
            String token = input.getSDKAccessToken();
            OppoSDKInfo configInfo = (OppoSDKInfo) config;

            String urlStr = configInfo.getRequestUrl() + "?fileId=" + ssoid + "&token=" + URLEncoder.encode(token,"UTF-8");

            Map<String, String> headerMap = new HashMap<String, String>();
            Random rand = new Random();
            int randomNum = rand.nextInt(100);
            String param = generateBaseString(new Timestamp(System.currentTimeMillis()).toString(), randomNum+"", token, configInfo);
            headerMap.put("param", param);
            headerMap.put("oauthSignature", URLEncoder.encode(generateSign(param, configInfo.getAppSecret()), "UTF-8"));

            String result = HttpUtil.get(urlStr, null, headerMap);
            Map resultMap = ParseJson.getJsonContentByStr(result, Map.class);
            if(resultMap != null && "200".equals(String.valueOf(resultMap.get("resultCode"))) && ssoid.equals(String.valueOf(resultMap.get("ssoid")))){
                ret.setSdkUid(String.valueOf(resultMap.get("ssoid")));
                ret.setSdkAccessToken(token);
                return ret.success();
            }
            ret.setErrorMsg(this.getSDKId() + " Login Error. ");
        } catch (Exception e) {
            logger.error("", e);
            ret.setErrorMsg(e.getMessage());
        }
        return ret.fail();
    }

	@Override
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, InputStream servletInputStream,
			String method, Object config) {
        logger.debug(paramMap.toString());
        try {
            OppoSDKInfo configInfo = (OppoSDKInfo) config;
			String platformOrderId = paramMap.get("notifyId");
			String orderId = paramMap.get("partnerOrder");
			String price = paramMap.get("price");

			String key = configInfo.getPubKey();
			StringBuffer sb = new StringBuffer();
			sb.append("notifyId=").append(paramMap.get("notifyId"));
			sb.append("&partnerOrder=").append(paramMap.get("partnerOrder"));
			sb.append("&productName=").append(paramMap.get("productName"));
			sb.append("&productDesc=").append(paramMap.get("productDesc"));
			sb.append("&price=").append(paramMap.get("price"));
			sb.append("&count=").append(paramMap.get("count"));
			sb.append("&attach=").append(paramMap.get("attach"));

			String content = sb.toString();
			String sign = paramMap.get("sign");

			sb = new StringBuffer();
			sb.append("notifyId=").append(paramMap.get("notifyId"));
			sb.append("&partnerOrder=").append(paramMap.get("partnerOrder"));
			sb.append("&productName=").append(ISOtoUTF8(paramMap.get("productName")));
			sb.append("&productDesc=").append(ISOtoUTF8(paramMap.get("productDesc")));
			sb.append("&price=").append(paramMap.get("price"));
			sb.append("&count=").append(paramMap.get("count"));
			sb.append("&attach=").append(paramMap.get("attach"));

			String utf8_content = sb.toString();

			if (!checkOppoSign(content, key, sign) && !checkOppoSign(utf8_content, key, sign)) {
				logger.debug("Sign Check Failed");
				return getRet("FAIL", "Sign Check Failed");
			}

			float payAmount = Float.valueOf(price) / 100f;
			String currencyType = "RMB";
			String payType = "";
			boolean iResult = callback.succeedPayment(orderId, platformOrderId, payAmount,
					currencyType, payType, ParseJson.encodeJson(paramMap));
			if (iResult)
				return getRet("OK", "");
		} catch (Exception e) {
			logger.error("", e);
		}
        return getRet("FAIL", "Internal Error.");
    }

	private String getRet(String result, String resultMsg) {
		return "result=" + result + "&resultMsg=" + resultMsg;
	}

	private boolean checkOppoSign(String content, String publicKey, String sign) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] encodedKey = Base64.decodeBase64(publicKey.getBytes());
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

			Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initVerify(pubKey);
			signature.update(content.getBytes("UTF-8"));
			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception ex) {
			logger.error("", ex);
			return false;
		}
	}

	public static String ISOtoUTF8(String isoString) {
		return new String(isoString.getBytes(Charset.forName("ISO-8859-1")));
	}

	public static final String OAUTH_CONSUMER_KEY = "oauthConsumerKey";
	public static final String OAUTH_TOKEN = "oauthToken";
	public static final String OAUTH_SIGNATURE_METHOD = "oauthSignatureMethod";
	public static final String OAUTH_SIGNATURE = "oauthSignature";
	public static final String OAUTH_TIMESTAMP = "oauthTimestamp";
	public static final String OAUTH_NONCE = "oauthNonce";
	public static final String OAUTH_VERSION = "oauthVersion";
	public static final String CONST_SIGNATURE_METHOD = "HMACSHA1";
	public static final String CONST_OAUTH_VERSION = "1.0";
	public static String generateBaseString(String timestamp, String nonce, String tokenKey, OppoSDKInfo configInfo) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(OAUTH_CONSUMER_KEY)
                    .append("=").append(URLEncoder.encode(configInfo.getAppKey(), "UTF-8"))
                    .append("&").append(OAUTH_TOKEN).append("=").append(URLEncoder.encode(tokenKey, "UTF-8"))
                    .append("&").append(OAUTH_SIGNATURE_METHOD).append("=").append(URLEncoder.encode(CONST_SIGNATURE_METHOD, "UTF-8"))
                    .append("&").append(OAUTH_TIMESTAMP).append("=").append(URLEncoder.encode(timestamp, "UTF-8"))
                    .append("&").append(OAUTH_NONCE).append("=").append(URLEncoder.encode(nonce, "UTF-8"))
                    .append("&").append(OAUTH_VERSION).append("=").append(URLEncoder.encode(CONST_OAUTH_VERSION, "UTF-8"))
                    .append("&");

		} catch (UnsupportedEncodingException e1) {
			logger.error("", e1);
		}
		return sb.toString();
	}

	public static String generateSign(String baseStr, String appSecret){
		byte[] byteHMAC = null;
		try {
			Mac mac = Mac.getInstance("HmacSHA1");
			String oauthSignature = appSecret + "&";
            SecretKeySpec spec = new SecretKeySpec(oauthSignature.getBytes(),"HmacSHA1");
			mac.init(spec);
			byteHMAC = mac.doFinal(baseStr.getBytes());
		} catch (Exception e) {
			logger.error("", e);
		}
		return new String(base64Encode(byteHMAC));
	}

	public static char[] base64Encode(byte[] data) {
		final char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
				.toCharArray();
		char[] out = new char[((data.length + 2) / 3) * 4];
		for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
			boolean quad = false;
			boolean trip = false;
			int val = (0xFF & (int) data[i]);
			val <<= 8;
			if ((i + 1) < data.length) {
				val |= (0xFF & (int) data[i + 1]);
				trip = true;
			}
			val <<= 8;
			if ((i + 2) < data.length) {
				val |= (0xFF & (int) data[i + 2]);
				quad = true;
			}
			out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
			val >>= 6;
			out[index + 1] = alphabet[val & 0x3F];
			val >>= 6;
			out[index] = alphabet[val & 0x3F];
		}
		return out;
	}

}
