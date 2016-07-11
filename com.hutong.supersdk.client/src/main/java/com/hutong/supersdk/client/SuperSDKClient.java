package com.hutong.supersdk.client;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.client.service.HttpService;
import com.hutong.supersdk.common.iservice.IService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * SuperSDK Client组件类
 * @author QINZH
 */
public class SuperSDKClient {
	
	private static final Log log = LogFactory.getLog(SuperSDKClient.class);
	
	private static SuperSDKClient instance;//Client端实例
	
	private Map<String,Object> serviceMap = new HashMap<String,Object>();//接口Service的管理Map
	
	private HttpService service;//HttpService代理类
	
	private boolean debug = false;
	
	private static final Object lock = new Object();
	
	private static final Object serviceMapLock = new Object(); 
	
	private SuperSDKClient(){}
	
	public static SuperSDKClient getInstance(){
		if(null == instance){
			throw new RuntimeException("call getInstance(service) first!");
		}
		return instance;
	}
	
	public static SuperSDKClient getInstance(String url, String appId, String encryptKey, String paymentSecret) {
		if (null == instance) {
			synchronized (lock) {
				if (null == instance) {
					log.info("SuperSDKClient create instance...");
					SuperSDKClient tmp = new SuperSDKClient();
					tmp.service = new HttpService();
					tmp.service.buildClient();
					tmp.service.setUrl(url);
					tmp.service.setAppId(appId);
					tmp.service.setEncryptKey(encryptKey);
					tmp.service.setPaymentSecret(paymentSecret);
					instance = tmp;
				}
			}
			log.info("SuperSDKClient create succeed...");
		}
		return instance;
	}
	
	@Deprecated
	public static SuperSDKClient getInstance(HttpService service){
		if(null == instance){
			synchronized (lock) {
				if(null == instance){
					log.info("SuperSDKClient create instance...");
					SuperSDKClient tmp = new SuperSDKClient();
					tmp.service = service;
					tmp.service.buildClient();
					instance = tmp;
				}
			}
		}
		log.info("SuperSDKClient create succeed...");
		return instance;
	}
	
	/**
	 * 根据类型获取被代理的Service
	 * @param clazz Class类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IService> T getService(Class<T> clazz) {
		String className = clazz.getSimpleName();
		Object service = serviceMap.get(clazz.getSimpleName());
		if(null == service){
			synchronized (serviceMapLock) {
				if(null == service){
					//创建被代理类对象
					service = Proxy.newProxyInstance(SuperSDKClient.class.getClassLoader(), new Class[]{clazz}, this.service); 
					serviceMap.put(className, service);
				}
			}
		}
		return (T) service;
	}
	
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public boolean isDebug() {
		return getDebug();
	}
	public boolean getDebug() {
		return debug;
	}
	
	public String getUrl() {
		return this.service.getUrl();
	}

	public String getAppId() {
		return this.service.getAppId();
	}

	public String getEncryptKey() {
		return this.service.getEncryptKey();
	}

	public String getPaymentSecret() {
		return this.service.getPaymentSecret();
	}
}
