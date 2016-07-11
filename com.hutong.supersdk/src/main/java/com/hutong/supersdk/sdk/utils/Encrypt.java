package com.hutong.supersdk.sdk.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class Encrypt {

	private static final String MAC_NAME = "HmacSHA1";
	private static final String ENCODING = "UTF-8";
	
	public static String HmacSHA1Encrypt( String encryptText, String encryptKey ) throws Exception{
		byte[] data = encryptKey.getBytes(ENCODING);
		// 根据给定的字节数组构造一个密钥, 第二参数指定一个密钥算法的名称
		SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
		
		Mac mac = Mac.getInstance(MAC_NAME);
		mac.init(secretKey);
		byte[] text = encryptText.getBytes(ENCODING);
		byte[] digest = mac.doFinal(text);
		StringBuilder sBuilder = bytesToHexString(digest);
		
		return sBuilder.toString();
	}
	
	public static StringBuilder bytesToHexString( byte[] bytesArray ){
		if ( bytesArray == null ){
			return null ;
		}
		StringBuilder sBuilder = new StringBuilder();
		for ( byte b : bytesArray ){
			String hv = String.format ("%02x", b);
			sBuilder.append( hv );
		}
		return sBuilder;
	}
	
	/**
	  * @Description:計算sha1值.<P>  
	  * @return sha1值
	  * @date  2012-05-17  
	  */
	public static String getSha1(String text) {
		java.security.MessageDigest md = null;
		try {
			md = java.security.MessageDigest.getInstance("sha-1");
			byte[] byteText = text.getBytes();
			md.update(byteText);
			byte[] sha1 = md.digest();
			return byte2hex(sha1);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	 /**
	  * @Description計算二進制數據.<P> 
	  * @date  2012-05-17 
	  */
	private static String byte2hex(byte[] b) {// 二行制转字符串
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}
}
