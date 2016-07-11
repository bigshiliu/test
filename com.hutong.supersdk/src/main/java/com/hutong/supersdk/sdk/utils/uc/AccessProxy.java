package com.hutong.supersdk.sdk.utils.uc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * 服务访问代理类。
 */
@SuppressWarnings("deprecation")
public class AccessProxy {
	private static Log Logger = LogFactory.getLog(AccessProxy.class);
	private HttpClient httpclient;
	private static List<IP> ipList = new ArrayList<IP>(); // 静态IP列表集合
	private List<IP> currentIPList;
	private static boolean isDomainAvailable = true;// 初始化成true；
	public static String localCacheURL = null;// 本地缓存可用的IP地址
	private static int connectTimeOut = ConfigHelper.getConnectTimeOut();
	private static int socketTimeOut = ConfigHelper.getSocketTimeOut();
	// sdk server的接口地址
	private static String serverHost = ConfigHelper.getServerHost();


	public AccessProxy() {
		currentIPList = new ArrayList<IP>();
		for (IP ip : ipList) {
			IP _ip = new IP();
			_ip.setIp(ip.getIp());
			_ip.setIsp(ip.getIsp());
			_ip.setPort(ip.getPort());
			currentIPList.add(_ip);
		}
	}

	public String assemblyParameters(String service, Map<String,Object> data,String apiKey,String gameId) throws Exception {

		// 分配给游戏合作商的接入密钥,请做好安全保密
//		String apiKey = "bf08b4398f31933c733fe646af83f535";// 
//		String gameId = "550764";// 
/**
apiKey：bf08b4398f31933c733fe646af83f535
cpId：48508
gameId：550764
 */
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", System.currentTimeMillis());// 当前系统时间
		params.put("service", service);

		Map<String,Object> game = new HashMap<String, Object>();
		game.put("gameId", gameId);
		
		params.put("game", game);
		params.put("data", data);
		params.put("encrypt", "md5");
		/*
		 * 签名规则=签名内容+apiKey 假定apiKey=202cb962234w4ers2aaa,sid=abcdefg123456 那么签名原文sid=abcdefg123456202cb962234w4ers2aaa
		 * 签名结果6e9c3c1e7d99293dfc0c81442f9a9984
		 */
		String signSource = Util.getSignData(data) + apiKey;
		// String signSource = "sid=70b37f00-5f59-4819-99ad-8bde027ade00144882"+apiKey;
		String sign = Util.getMD5Str(signSource);// MD5加密签名
		Logger.debug("[签名原文]" + signSource);
		Logger.debug("[签名结果]" + sign);
		params.put("sign", sign);
		String body = Util.encodeJson(params);// 把参数序列化成一个json字符串
		Logger.debug("[请求参数]" + body);
		return body;
	}

	/**
	 * 【对外提供的接口方法】 执行一个HTTP POST请求，返回请求响应的内容
	 * 
	 * @return 返回请求响应的内容
	 */
	public String doPost(String service,String serviceUrl, Map<String,Object> data,String apiKey,String gameId) {
		StringBuffer stringBuffer = new StringBuffer();
		HttpEntity entity = null;
		BufferedReader in = null;
		HttpResponse response = null;
		String ln;
		try {
			String body = assemblyParameters(service, data,apiKey,gameId);
			
			response = execute(serverHost, serviceUrl, body);
			entity = response.getEntity();
			in = new BufferedReader(new InputStreamReader(entity.getContent()));
			while ((ln = in.readLine()) != null) {
				stringBuffer.append(ln);
				stringBuffer.append("\r\n");
			}
			httpclient.getConnectionManager().shutdown();
		} catch (IllegalStateException e) {
			Logger.error(e.toString(), e);
		} catch (IOException e) {
			Logger.error(e.toString(), e);
		} catch (Exception e) {
			Logger.error(e.toString(), e);
		} finally {
			if (null != in) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					Logger.error(e.toString());
				}
			}
		}
		return stringBuffer.toString();
	}
	public String doPost(String service, Map<String,Object> data,String apiKey,String gameId) {
		String serviceUrl = ConfigHelper.getServiceUrl(service);
		return this.doPost(service, serviceUrl, data, apiKey, gameId);
	}

	/**
	 * 【私有方法供本类内部调用】 执行一个HTTP POST请求，返回请求响应的内容,并处理url不可用时跟换
	 * 
	 * @return 返回请求响应的内容
	 */
	private HttpResponse execute(String serverHost, String serviceUrl, String body) throws Exception {
		String url = serverHost + serviceUrl;
		Logger.debug("访问地址:" + url);
		Logger.debug("请求body:" + body);
		HttpResponse response = null;
		if (null == httpclient) {
			httpclient = new DefaultHttpClient();
			HttpParams params = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, connectTimeOut);
			HttpConnectionParams.setSoTimeout(params, socketTimeOut);

		}

		// 域名不可用并且本地缓存localCacheURL存在，则设置url=localCacheURL
		if (!isDomainAvailable && !StringUtils.isEmpty(localCacheURL)) {
			url = localCacheURL;
		}
		HttpPost httppost = new HttpPost(url);
		httppost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httppost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);// 将Expect: 100-Continue设置为关闭
		try {
			httppost.setEntity(new ByteArrayEntity(body.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			Logger.error(e.toString());
		}
		try {
			response = httpclient.execute(httppost);
			// 域名不可用，用url=IP访问成功,保存localCacheURL为该url
			if (!isDomainAvailable) {
				localCacheURL = url;
			}
		} catch (ClientProtocolException e) {
			Logger.error(e.toString());// 客户端协议异常
		} catch (IOException e) {
			// 未知主机错误||sockt连接超时||连接异常
			if (e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException) {
				if ("Read timed out".equalsIgnoreCase(e.getMessage().trim())) {
					// Read timed out 服务超时
					Logger.error(e.toString(), e);
				} else {
					// 包含【connect timed out 连接超时】
					// 访问失败，进行补救机制
					CheckDomainThread.start(); // 启动异步线程检测域名
					// 先检查域名是否恢复可用
					if (isDomainAvailable) {
						return execute(serverHost, serviceUrl, body); // 域名恢复，用回域名访问.
					} else {
						String ip = getNewIP();// 访问失败,获取ip
						if (null != ip) {
							return execute(ip, serviceUrl, body); // 继续用获得的ip访问.
						} else {
							Logger.error(e.toString(), e); // 没有可用的ip,抛出异常.
						}
					}
				}
			} else {
				Logger.error(e.toString(), e);
			}
		}
		finally {
			httppost.releaseConnection();
		}
		return response;
	}

	/**
	 * 【私有方法供内部调用】获取IP地址
	 * 
	 * @return 如果IPList有IP，则返回随机的IP，否则返回null；
	 */
	private String getNewIP() {
		if (0 == currentIPList.size()) {
			return null;
		}
		Random r = new Random();
		IP ip = currentIPList.remove(r.nextInt(currentIPList.size()));
		return getServerUrl(ip.toString());
	}

	/**
	 * 将【IP】/【IP】【PORT】组装成访问服务的URL。
	 * 
	 * @param ip ip地址或ip地址+端口
	 * @return 组装成功返回访问服务的URL地址，失败就返回null.
	 */
	private static String getServerUrl(String ip) {
		if (StringUtils.isEmpty(serverHost)) {
			return null;
		}
		String serverhead = serverHost.substring(0, serverHost.indexOf("//", 0) + 2);
		String serverSuffix;
		try {
			serverSuffix = serverHost.substring(serverHost.indexOf("/", 8), serverHost.length());
		} catch (Exception e) {
			serverSuffix = "";
		}
		return serverhead + ip + serverSuffix;
	}

	public HttpClient getHttpclient() {
		return httpclient;
	}

	public void setHttpclient(HttpClient httpclient) {
		this.httpclient = httpclient;
	}

	public static boolean isDomainAvailable() {
		return isDomainAvailable;
	}

	public static void setDomainAvailable(boolean isDomainAvailable) {
		AccessProxy.isDomainAvailable = isDomainAvailable;
	}

	public static void setIpList(List<IP> ipList) {
		AccessProxy.ipList = ipList;
	}

}
