package com.hutong.supersdk.sdk.utils.lenovo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;



public class HttpUtil {

	private static class TrustAnyTrustManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
		}
		public X509Certificate[] getAcceptedIssuers() {
			return new X509Certificate[] {};
		}
	}

	private static class TrustAnyHostnameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	}
	
	/**
	 * https GET
	 * @param url
	 * @return
	 */
	public static HttpResponseResult sendGetHttps(String url) {
		HttpResponseResult responseResult = new HttpResponseResult();
		StringBuffer str_return = new StringBuffer();
		HttpsURLConnection conn = null;
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
			URL console = new URL(url);
			conn = (HttpsURLConnection) console.openConnection();
			conn.setSSLSocketFactory(sc.getSocketFactory());
			conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
			conn.connect();

			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null)
				str_return.append(inputLine);
			in.close();
			
			responseResult.setHttpCode(conn.getResponseCode());
			responseResult.setMessage(str_return.toString());
		} catch (Exception e) {
//			e.printStackTrace();
			try {
				if (conn != null) {
					responseResult.setHttpCode(conn.getResponseCode());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseResult;
	}
	
	public static void main(String[] args) throws Exception {
	}
	
}
