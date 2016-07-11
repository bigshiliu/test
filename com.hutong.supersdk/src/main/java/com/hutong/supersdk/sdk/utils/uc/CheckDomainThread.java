package com.hutong.supersdk.sdk.utils.uc;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * 检查域名的异步线程类
 */
@SuppressWarnings("deprecation")
public class CheckDomainThread implements Runnable {
	private static  Log Logger = LogFactory.getLog(CheckDomainThread.class);
	private static ScheduledExecutorService scheduler = null; 
	//sdk server的接口地址
	private static String serverHost = ConfigHelper.getServerHost();
	private static long checkTime = ConfigHelper.getCheckTime();
	private static int connectTimeOut = ConfigHelper.getConnectTimeOut();
	private static int socketTimeOut = ConfigHelper.getSocketTimeOut();

	
	@Override
	@SuppressWarnings("resource")
	public void run() {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpParams params = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, connectTimeOut); //检测的连接最大时间
		HttpConnectionParams.setSoTimeout(params, socketTimeOut);
		HttpPost httppost = new HttpPost(serverHost);

   	    try {
			HttpResponse response = httpclient.execute(httppost);
			//判断域名是否恢复可用
			if(200<=response.getStatusLine().getStatusCode()&&response.getStatusLine().getStatusCode()<300){
				//域名可用,关闭异步线程
				Logger.debug("域名恢复可用--"+serverHost);
				close();
			}
		} catch (ClientProtocolException e) {
			Logger.error("客户端协议异常");
		} catch (IOException e) {
			if(e instanceof UnknownHostException || e instanceof SocketTimeoutException || e instanceof ConnectException){
				Logger.error("域名仍不可用");
			}
		}
	}
	
    /**
     * 如果没有启动异步线程就启动
     */
    public static void start(){
		if(null==scheduler||scheduler.isShutdown()){
    	    //域名不可用，启动异步线程
			Logger.debug("域名不可用，启动异步线程");
    	    AccessProxy.setDomainAvailable(false);
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleAtFixedRate(new CheckDomainThread(), 0, checkTime, TimeUnit.SECONDS); 
		}
    }
	
	/**
	 * 关闭异步线程
	 */
	public static void close(){
		if(null!=scheduler&&!scheduler.isShutdown()){
			//域名恢复可用，关闭异步线程.
			Logger.debug("域名可用，关闭异步线程");
			AccessProxy.setDomainAvailable(true);
			scheduler.shutdown();
		}
	}

}