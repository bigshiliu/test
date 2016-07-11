package com.hutong.supersdk.sdk.utils.uc;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

public class UCMD5Util {
	
	/**
	 * 将Map数据组装成待签名字符串
	 * 
	 * @param params
	 *            待签名的参数列表
	 * @param notIn
	 *            不参与签名的参数名列表
	 * @return 待签名字符串。如果参数params为null，将返回null
	 */
	public static String createSignData(Map<String, Object> params, String[] notIn) {
		if (null == params) {
			return null;
		}

		StringBuilder content = new StringBuilder(200);

		// 按照key排序
		List<String> notInList = null;
		if (null != notIn) {
			notInList = Arrays.asList(notIn);
		}
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);

			if (notIn != null && notInList.contains(key))
				continue;

			String value = params.get(key) == null ? "" : params.get(key).toString();
			content.append(key).append("=").append(value);
		}

		String result = content.toString();
		return result;
	}

	/**
	 * 对字符串进行MD5签名
	 * 
	 * @param value
	 *            待MD5签名的字符串
	 * @return 生成的MD5签名字符串。如果传入null，返回null；如果签名过程中抛出异常，将返回null
	 */
	public static String hexMD5(String value) {
		if (null == value) {
			return null;
		}

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.reset();
			messageDigest.update(value.getBytes("utf8"));
			byte[] digest = messageDigest.digest();
			return byteToHexString(digest);
		} catch (NoSuchAlgorithmException e) {
			// ignore
		} catch (UnsupportedEncodingException e) {
			// ignore
		}

		return null;
	}
	
	/**
     * 将字节数组转换成十六进制字符串
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    public static String byteToHexString(byte[] bytes) {
        if (null == bytes) {
            return null;
        }
        
        return String.valueOf(Hex.encodeHex(bytes));
    }
    
    public static void main(String[] args) {
    	String sdk_id = "1111";
		String app_id = "2222";
		String channel_id = "";
		String sdk_uid = "";
		String access_token = "3333";
		String refresh_token = "";
		String time = "4444";
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("sdk_id", sdk_id);
		param.put("app_id", app_id);
		param.put("channel_id", channel_id);
		param.put("sdk_uid", sdk_uid);
		param.put("access_token", access_token);
		param.put("refresh_token", refresh_token);
		param.put("time", time);
		System.out.println(createSignData(param, null));
	}
}
