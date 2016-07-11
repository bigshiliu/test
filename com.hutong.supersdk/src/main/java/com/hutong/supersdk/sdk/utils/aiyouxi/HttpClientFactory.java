/**
 * 
 */
package com.hutong.supersdk.sdk.utils.aiyouxi;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

/**
 * Description TODO
 * 
 * @ClassName HttpClientPool
 * 
 * @Copyright 炫彩互动
 * 
 * @Project openAPI
 * 
 * @Author dubin
 * 
 * @Create Date 2014-5-16
 * 
 * @Modified by none
 * 
 * @Modified Date
 */
public class HttpClientFactory {

    private static HttpClientFactory httpClientFactory = null;

    private int defaultConnectionTimeout = 3000;

    private int defaultSoTimeout = 8000;

    private int defaultIdleConnTimeout = 0;

/*    private int defaultMaxConnPerHost = 3000;

    private int defaultMaxTotalConn = 10000;*/
    
    private HttpConnectionManager connectionManager;

    public static HttpClientFactory getInstance() {

        if (httpClientFactory == null) {
            synchronized (HttpClientFactory.class) {
                if (httpClientFactory == null) {
                    httpClientFactory = new HttpClientFactory();
                }
            }
        }

        return httpClientFactory;
    }

    @SuppressWarnings("deprecation")
    private HttpClientFactory() {
        
        Protocol myhttps = new Protocol("https", new MySecureProtocolSocketFactory(), 443);
        
        Protocol.registerProtocol("https", myhttps);

        this.connectionManager = new MultiThreadedHttpConnectionManager();
/*        this.connectionManager.getParams().setDefaultMaxConnectionsPerHost(this.defaultMaxConnPerHost);
        this.connectionManager.getParams().setMaxTotalConnections(this.defaultMaxTotalConn);*/
        this.connectionManager.getParams().setStaleCheckingEnabled(true);
        this.connectionManager.getParams().setConnectionTimeout(this.defaultConnectionTimeout);
        this.connectionManager.getParams().setSoTimeout(this.defaultSoTimeout);
        
        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(this.connectionManager);
        ict.setConnectionTimeout(this.defaultIdleConnTimeout);

        ict.start();
    }

    public HttpClient getHttpClient() {

        HttpClient httpclient = new HttpClient(this.connectionManager);

        return httpclient;
    }
}
