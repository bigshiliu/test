package com.hutong.supersdk.client.service;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.hutong.supersdk.client.SuperSDKClient;
import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.model.ServiceObj;
import com.hutong.supersdk.common.util.EncryptUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.ServiceUtil;

/**
 * Http代理类
 * @author QINZH
 *
 */
public class HttpService implements InvocationHandler  {
	
	private static final Log log = LogFactory.getLog(HttpService.class);

	/**
	 * Http Client 端
	 */
	CloseableHttpClient client ;
	
	/**
	 * SuperSDK Url 地址
	 */
	private String url;
	
	/**
	 * 接入的AppId
	 */
	private String appId;

	/**
	 * SuperSDK Client端的签名密钥
	 */
	private String encryptKey;
	
	/**
	 * SuperSDK Client端支付密钥
	 */
	private String paymentSecret;
	
	/**
	 * 最大创建的Http连接数
	 */
	private int maxTotalConnect = 100;
	
	private int maxPerRouteConnect = 100;
	
	/**
	 * 连接池
	 */
	private PoolingHttpClientConnectionManager cm;
	
	public HttpService() {
	}
	
	/**
	 * 创建Client Http连接
	 * @param forceUpdate
	 */
	public void buildClient(boolean forceUpdate){
		if(forceUpdate || null == client){
			if(null != cm){
				cm.close();
			}
			cm = new PoolingHttpClientConnectionManager();
			cm.setMaxTotal(maxTotalConnect);
			cm.setDefaultMaxPerRoute(maxPerRouteConnect);
			if(null != client){
				try {
					client.close();
				} catch (IOException e) {
				}
			}
			client = HttpClients.custom()
	                .setConnectionManager(cm)
	                .build();
		}
	}
	
	public void buildClient(){
		buildClient(false);
	}

	/**
	 * 代理接口，反射执行
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) 
			throws SuperSDKException {
		CloseableHttpResponse response = null;
		ServiceObj serviceObj = ServiceUtil.getServiceObj(proxy,method);
		String methodName = serviceObj.getMethodName();
		String serviceName = serviceObj.getServiceName();
		String[] paramNames = serviceObj.getParamNames();
		Class<?> returnType = serviceObj.getReturnType();
		String responseBody = null;
		try {
			//用户中心Client对象
			SuperSDKClient superSDKClient = SuperSDKClient.getInstance();
			
			Boolean isDebug = superSDKClient.isDebug();
			
			URI uri = new URI("http://" + url + "/client/" +serviceName + "/" + methodName);
			RequestBuilder rb = RequestBuilder.post()
					.setUri(uri);
			Map<String, String> paramMap = new HashMap<String, String>();
			
			StringBuffer logMsg = new StringBuffer("[Call SuperSDK] -> ")
				.append(uri.toString()).append("?");		
			
			//目前来说，所有接口仅有一个参数，参数类型为JsonReqObj
			String paramName = paramNames[0];
			Object value = args[0];
			
			JsonReqObj req = (JsonReqObj) value;
			
			//设置AppId 从SuperSDKCLient中获取
			req.setKeyData(DataKeys.Common.APP_ID, superSDKClient.getAppId());
			
			String sign = EncryptUtil.generateSign(req.getData(), this.encryptKey);
			req.setSign(sign);
			
			paramMap.put(paramName, serializeParamValue(req));
			
			if(isDebug){
				paramMap.put(DataKeys.DEBUG, isDebug.toString());
			}
			
			List<NameValuePair> formParams = new ArrayList<NameValuePair>();
			for (Entry<String, String> entry : paramMap.entrySet()) {
				logMsg.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
				formParams.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
			}
			
			UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams,"UTF-8");
			rb.setEntity(postEntity);
			HttpUriRequest request = rb.build();
			log.info(logMsg.toString());
			long time = System.currentTimeMillis();
			
			//返回结果
			response = client.execute(request);
			if(log.isDebugEnabled()){
				log.debug("http time:" + (System.currentTimeMillis() - time) + "ms");
			}
			
			int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                responseBody = entity != null ? EntityUtils.toString(entity) : null;
                JsonResObj json = ParseJson.getJsonContentByStr(responseBody, JsonResObj.class);
                
                if(log.isDebugEnabled()){
                	log.debug("\t\t\t\t\tserver time:" + (System.currentTimeMillis() - time) + "ms");
                }
                
                if (returnType.equals(JsonResObj.class)) {
                	return json;
                }
                else {
                	log.error("Interface Return Type Error. Return Type Must be JsonResObj.class");
                	return null;
                }
            } else {
                throw new SuperSDKException("Unexpected response status: " + status);
            }
		}catch(SuperSDKException e){
			throw e;
		}catch(Exception e){
			throw new SuperSDKException("responseBody :" + responseBody, e);
		}finally {
        	if(null != response){
                try {
					response.close();
				} catch (IOException e) {
					//ignore
				}
        	}
        }
	}
	
	/**
	 * 序列化请求参数
	 * @param value
	 * @return
	 */
	private static String serializeParamValue(Object value) {
		String valueStr;
		
		if (value instanceof JsonReqObj) {
			valueStr = ParseJson.encodeJson(value);
		}
		else {
			valueStr = value.toString();
		}
		return valueStr;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getEncryptKey() {
		return encryptKey;
	}

	public void setEncryptKey(String encryptKey) {
		this.encryptKey = encryptKey;
	}

	public String getPaymentSecret() {
		return paymentSecret;
	}

	public void setPaymentSecret(String paymentSecret) {
		this.paymentSecret = paymentSecret;
	}

	public int getMaxTotalConnect() {
		return maxTotalConnect;
	}

	public void setMaxTotalConnect(int maxTotalConnect) {
		this.maxTotalConnect = maxTotalConnect;
	}

	public int getMaxPerRouteConnect() {
		return maxPerRouteConnect;
	}

	public void setMaxPerRouteConnect(int maxPerRouteConnect) {
		this.maxPerRouteConnect = maxPerRouteConnect;
	}
}
