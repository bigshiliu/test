package com.hutong.supersdk.sdk.utils.qihu360;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class QihooUtil {
	


	/**
	 * http请求
	 * @param url
	 * @param ignoreSSL
	 * @return
	 * @throws IOException 
	 */
	public static String requestUrl(String url, boolean ignoreSSL)
			throws IOException {

		if (ignoreSSL) {
			_ignoreSSL();
		}

		HttpURLConnection conn;
		try {
			//if GET....
			//URL requestUrl = new URL(url + "?" + httpBuildQuery(data));
			URL requestUrl = new URL(url);
			conn = (HttpURLConnection) requestUrl.openConnection();
		} catch (MalformedURLException e) {
			return e.getMessage();
		}

		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setConnectTimeout(8000);

		conn.setDoInput(true);
		conn.setDoOutput(true);

		String line;
		BufferedReader bufferedReader;
		StringBuilder sb = new StringBuilder();
		InputStreamReader streamReader = null;
		try {
			streamReader = new InputStreamReader(conn.getInputStream(), "UTF-8");
		} catch (IOException e) {
			/*
			Boolean ret2 = true;
			if (ret2) {
				return e.getMessage();
			}
			*/
			streamReader = new InputStreamReader(conn.getErrorStream(), "UTF-8");
		} finally {
			if (streamReader != null) {
				bufferedReader = new BufferedReader(streamReader);
				while ((line = bufferedReader.readLine()) != null) {
					sb.append(line);
				}
			}
			conn.disconnect();
		}
		return sb.toString();
	}
	//ignoreSSL hostname verifer
	private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {

		public boolean verify(String s, SSLSession sslsession) {
			return true;
		}
	};

	/**
	 * 忽略SSL
	 */
	private static void _ignoreSSL() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
		
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

	
			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}


			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}};

			// Install the all-trusting trust manager

			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
		} catch (KeyManagementException ex) {
			Logger.getLogger(QihooUtil.class.getName()).log(Level.SEVERE, null, ex);
		} catch (NoSuchAlgorithmException ex) {
			Logger.getLogger(QihooUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * 参数编码
	 * @param data
	 * @return 
	 */
	public static String httpBuildQuery(Map<String, String> data) {
		String ret = "";
		String k, v;
		Iterator<String> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			k = iterator.next();
			v = data.get(k);
			try {
				ret += URLEncoder.encode(k, "utf8") + "=" + URLEncoder.encode(v, "utf8");
			} catch (UnsupportedEncodingException e) {
			}
			ret += "&";
		}
		return ret.substring(0, ret.length() - 1);
	}

	/**
	 * 签名计算
	 * @param params
	 * @param appSecret
	 * @return 
	 */
	public static String getSign(HashMap<String,String> params, String appSecret) {
		Object[] keys = params.keySet().toArray();
		Arrays.sort(keys);
		String k, v;

		String str = "";
		for (int i = 0; i < keys.length; i++) {
			k = (String) keys[i];
			if (k.equals("sign") || k.equals("sign_return")) {
				continue;
			}
			v = (String) params.get(k);

			if (v.equals("")) {
				continue;
			}

			str += v + "#";
		}
		return QihooUtil.md5(str + appSecret);
	}

	public static String md5(String str) {
		StringBuilder sb = new StringBuilder();
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(str.getBytes("UTF8"));
			byte bytes[] = m.digest();

			for (int i = 0; i < bytes.length; i++) {
				if ((bytes[i] & 0xff) < 0x10) {
					sb.append("0");
				}
				sb.append(Long.toString(bytes[i] & 0xff, 16));
			}
		} catch (Exception e) {
		}
		return sb.toString();
	}

}

