/**
 * 
 */
package com.hutong.supersdk.sdk.utils.aiyouxi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Description TODO
 * 
 * @ClassName HttpClientUtil
 * 
 * @Copyright 炫彩互动
 * 
 * @Project openAPI
 * 
 * @Author dubin
 * 
 * @Create Date 014-5-15
 * 
 * @Modified by noregularne
 * 
 * @Modified Date
 */
public class HttpClientUtil {

    private static final Log log = LogFactory.getLog(HttpClientUtil.class);

    private static HttpClient httpClient = HttpClientFactory.getInstance().getHttpClient();

    public static String doPost(String reqURL, Map<String, String> params) throws Exception {

        String responseContent = "";
        PostMethod postMethod = new PostMethod(reqURL);

        postMethod.getParams().setCredentialCharset("UTF-8");
        postMethod.getParams().setHttpElementCharset("UTF-8");
        postMethod.getParams().setContentCharset("UTF-8");

        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; text/html; charset=UTF-8");
        postMethod.addRequestHeader("User-Agent", "Mozilla/4.0");

        // 判断是否包含参数
        if (null != params && params.size() > 0) {
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                formParams.add(new NameValuePair(entry.getKey(), entry.getValue()));
            }

            postMethod.setRequestBody(formParams.toArray(new NameValuePair[formParams.size()]));
        }
        
        BufferedReader reader = null;
        
        try {
            int statusCode = httpClient.executeMethod(postMethod);
            
            reader = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            responseContent = sb.toString();
            
            if (log.isDebugEnabled()) {
                log.debug("请求地址: " + postMethod.getURI());
                log.debug("请求方式: POST");
                log.debug("请求参数: " + (params == null ? "" : params.toString()));
                log.debug("响应码: " + postMethod.getStatusLine());
                log.debug("响应长度: " + postMethod.getResponseContentLength());
                log.debug("响应内容: " + responseContent);
            }

            if ((statusCode == 301) || (statusCode == 302)) {
                Header locationHeader = postMethod.getResponseHeader("location");
                String location = null;
                if (locationHeader != null) {
                    location = locationHeader.getValue();
                    log.info("The page was redirected to:" + location);
                    responseContent = HttpClientUtil.doPost(location, null);
                } else {
                    log.warn("Location field value is null.");
                }
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            if(reader!=null)
            {
                reader.close();
            }
            postMethod.releaseConnection();
        }

        return responseContent;
    }

    public static String doGet(String reqURL) throws Exception {

        String responseContent = "";

        GetMethod getMethod = new GetMethod(reqURL);
        getMethod.getParams().setCredentialCharset("UTF-8");
        getMethod.getParams().setHttpElementCharset("UTF-8");
        getMethod.getParams().setContentCharset("UTF-8");

        getMethod.addRequestHeader("User-Agent", "Mozilla/4.0");
        
        BufferedReader reader = null;
        try {
            httpClient.executeMethod(getMethod);
            
            reader = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), "utf-8"));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            responseContent = sb.toString();

            if (log.isDebugEnabled()) {
                log.debug("请求地址: " + getMethod.getURI());
                log.debug("请求方式: GET");
                log.debug("响应码: " + getMethod.getStatusLine());
                log.debug("响应长度: " + getMethod.getResponseContentLength());
                log.debug("响应内容: " + responseContent);
            }

        } catch (Exception ex) {
            throw ex;
        } finally {
            if(reader!=null)
            {
                reader.close();
            }
            getMethod.releaseConnection();
        }

        return responseContent;
    }
}