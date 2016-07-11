package com.hutong.supersdk.sdk.utils.youmiouwan;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.net.URLDecoder;

public class UmipayHelper {

	// UmipayHelper的构造方法
	public UmipayHelper() {

	}

	// 获得md5值
	public String getMD5Str(String str) {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			messageDigest.reset();

			messageDigest.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("NoSuchAlgorithmException caught!");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		byte[] byteArray = messageDigest.digest();

		StringBuffer md5StrBuff = new StringBuffer();

		for (int i = 0; i < byteArray.length; i++) {
			if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
				md5StrBuff.append("0").append(
						Integer.toHexString(0xFF & byteArray[i]));
			else
				md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
		}

		return md5StrBuff.toString();
	}

	public String ksort(Map<String, String> map) {
		List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(
				map.entrySet());

		// 对HashMap中的key 进行排序
		Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
			public int compare(Map.Entry<String, String> o1,
					Map.Entry<String, String> o2) {
				return (o1.getKey()).toString().compareTo(
						o2.getKey().toString());
			}
		});
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < infoIds.size(); i++) {
			// String id = infoIds.get(i).toString();
			if (infoIds.get(i).toString().indexOf("sign=") != -1) {
				continue;
			}
			builder.append(infoIds.get(i).toString());
		}
		return builder.toString();
	}

	public String getUrlSign(Map<String, String> params, String serverSecret) {

		// 待签名字符串
		String str = "";
		// 先将参数以其参数名的字典序升序进行排序
		// 为key/value对生成一个key=value格式的字符串，并拼接到待签名字符串后面
		str += ksort(params);
		// System.out.println(str);
		// 将签名密钥拼接到签名字符串最后面
		str += serverSecret;
		// 通过md5算法为签名字符串生成一个md5(小写)
		return getMD5Str(str);
	}

	/*
	 * 验证充值回调是否通过校验
	 */
	public boolean verifyUrlSign(Map<String, String> params, String serverSecret) {
		if (!params.containsKey("sign"))
			return false;
		String sign = params.get("sign").toString();
		String sign2 = new UmipayHelper().getUrlSign(params, serverSecret);
		// System.out.println("sign2=" + sign2);
		return sign.equals(sign2);
	}

	/*
	 * 验证充值登录的SIGN
	 */
	public boolean verifyLoginSign(String uid, String timestamp, String sign,
			String serverSecret) {
		String signStr = uid + "&" + timestamp + "&" + serverSecret;
		String sign2 = new UmipayHelper().getMD5Str(signStr);
		//System.out.println("sign2=" + sign2);
		return sign.equals(sign2);
	}

	public Map<String, String> getURLMap(String url) {
		Map<String, String> params = new HashMap<String, String>();
		String data = url.substring(url.indexOf(".php?") + 5);
		String[] array = data.split("&");
		for (int i = 0; i < array.length; i++) {
			try {
				params.put(array[i].substring(0, array[i].indexOf("=")), URLDecoder
						.decode(array[i].substring(array[i].indexOf("=") + 1), "utf-8"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return params;
	}

	public static void main(String[] args) {
//		UmipayHelper helper = new UmipayHelper();
		/*String url = "http://localhost:8080/UmipayServer/NotifyDemoServlet?serverId=&callbackInfo=1000%E9%87%91%E5%B8%81&openId=9a623b123b11eccc&orderId=m20130829090222517&orderStatus=1&payType=ALIPAY&amount=0.01&remark=&sign=0f876b2050c5f564d33316a30af0ac5c";
		Map<String, String> params = helper.getURLMap(url);
		String serverSecret = "1234567890abcdef";
		if (helper.verifyUrlSign(params, serverSecret)) {
			System.out.println("签名验证成功");
		} else {
			System.out.println("签名验证失败");
		}*/
	}
}

