package com.hutong.supersdk.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

class Base64Util {

    static byte[] encode(byte[] src) {
    	return Base64.encodeBase64(src);
    }

    static byte[] encode(String src) {
        try {
			return encode(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }

    static String encodeToStr(byte[] src) {
        try {
			return new String(encode(src), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return "";
    }

    static String encodeToStr(String src) {
        try {
			return encodeToStr(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return "";
    }

    static byte[] decode(byte[] src) {
    	return Base64.decodeBase64(src);
    }

    static byte[] decode(String src) {
        try {
			return decode(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return null;
    }

    static String decodeToStr(byte[] src) {
        try {
			return new String(decode(src), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return "";
    }

    static String decodeToStr(String src) {
        try {
			return decodeToStr(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        return "";
    }
}
