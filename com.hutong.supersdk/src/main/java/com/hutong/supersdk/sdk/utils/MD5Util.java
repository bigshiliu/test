package com.hutong.supersdk.sdk.utils;

import java.security.MessageDigest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MD5Util {
	private static final Log logger = LogFactory.getLog(MD5Util.class);
	
	public static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
        //noinspection Duplicates
        try {
			byte[] btInput = s.getBytes("UTF-8");
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
            for (byte byte0 : md) {
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
			return new String(str).toLowerCase();
		} catch (Exception e) {
			logger.error("",e);
			return "";
		}
	}

	/**
	 * 和系统配置的MD5KEY组合以后生成md5值，结果取中间8-24位
	 * 
	 * @param s
	 * @return
	 */
//	public static final String HCMD5(String s) {
//		String md5Key = SystemConfigStatic.SYS_MD5_KEY;
//		return MD5(s + md5Key).substring(8, 24);
//	}
	/**
	 * 返回16为MD5码
	 * @param s string to encrypt
	 * @return md5 result
	 */
	public static String MD5_16(String s) {
		return MD5(s).substring(8, 24);
	}
}
