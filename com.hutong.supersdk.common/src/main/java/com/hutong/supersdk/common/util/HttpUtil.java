package com.hutong.supersdk.common.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class HttpUtil {

    private static final int HTTP_REQUEST_TIME_OUT = 8000;
    private static final Log logger = LogFactory.getLog(HttpUtil.class);

    /**
     * 以POST方式提交订单，忽略cookie
     *
     * @param url      url
     * @param paramMap params
     * @return http response
     */
    public static String postForm(String url, Map<String, String> paramMap) {
        return postForm(url, paramMap, null);
    }

    /**
     * 以POST方式提交订单
     *
     * @param url      url
     * @param paramMap params
     * @param cookie   cookie
     * @return http response
     */
    public static String postForm(String url, Map<String, String> paramMap, String cookie) {

        NameValuePair[] valueArray = getNameValuePairs(paramMap);

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);

        handleWithCookie(cookie, client, method);
        handleCommonMethod(method);

        method.setRequestBody(valueArray);

        return execute(client, method);
    }


    /**
     * 以POST方式提交json字符串，忽略cookie
     *
     * @param url  url
     * @param json json
     * @return http response
     */
    public static String postJson(String url, String json) {
        return postJson(url, json, null);
    }

    /**
     * 以POST方式提交json字符串
     *
     * @param url    url
     * @param json   json
     * @param cookie cookie
     * @return http response
     */
    public static String postJson(String url, String json, String cookie) {

        HttpClient client = new HttpClient();
        PostMethod method = new PostMethod(url);

        handleWithCookie(cookie, client, method);
        handleCommonMethod(method);

        try {
            method.setRequestEntity(new StringRequestEntity(json, "application/json", "utf-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }

        return execute(client, method);
    }

    /**
     * 以post方式提交plaintext
     *
    * @param host 主机名
    * @param port 端口号
    * @param url  url
    * @param text text
    * @return http response
    */
    public static String postText(String host, int port, String url, String text) {

        HttpClient client = new HttpClient();
        HostConfiguration hostconf = new HostConfiguration();
        hostconf.setHost(host, port);
        client.setHostConfiguration(hostconf);

        PostMethod method = new PostMethod(url);



        try {
            method.setRequestEntity(new StringRequestEntity(text, "text/plain", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }

        return execute(client, method);
    }

    /**
     * 通过GET方式发送HTTP请求,params为请求参数,将会以key1=value1&key2=value2...的方式追加在url后面
     * @param url
     * @param params
     * @return
     */
    public static String getWithParams(String url, Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append(url);
            if (null != params && !params.isEmpty()) {
                sb.append("?");
                for (String key : params.keySet()) {
                    if (sb.length() > 0) {
                        sb.append("&");
                    }
                    sb.append(key);
                    sb.append("=");
                    sb.append(URLEncoder.encode(params.get(key), "UTF-8"));
                    sb.append("&");
                }
            }
            return get(sb.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("HttpUtil URLEncoder Error, Params:" + ParseJson.encodeJson(params));
            return "";
        }
    }

    /**
     * 通过GET方式发送HTTP请求，忽略cookie
     *
     * @param url url
     * @return http response
     */
    public static String get(String url) {
        return get(url, null);
    }

    /**
     * 通过GET方式发送HTTP请求
     *
     * @param url    url
     * @param cookie cookie
     * @return http response
     */
    public static String get(String url, String cookie) {
        return get(url, cookie, null);
    }

    /**
     * 通过GET方式发送HTTP请求，忽略cookie, 可添加http头信息
     *
     * @param url       url
     * @param cookie    cookie
     * @param headerMap map data to header
     * @return http response
     */
    public static String get(String url, String cookie, Map<String, String> headerMap) {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);

        handleWithCookie(cookie, client, method);
        handleCommonMethod(method);

        if (headerMap != null)
            for (Map.Entry<String, String> entry : headerMap.entrySet())
                method.addRequestHeader(entry.getKey(), entry.getValue());

        return execute(client, method);
    }

    private static String execute(HttpClient client, HttpMethod method) {
        String response = null;
        try {
            int status = client.executeMethod(method);
            if (status == HttpStatus.SC_OK)
                response = method.getResponseBodyAsString();
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            method.releaseConnection();
        }
        return response;
    }

    private static NameValuePair[] getNameValuePairs(Map<String, String> paramMap) {
        List<NameValuePair> valueList = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : paramMap.entrySet())
            valueList.add(new NameValuePair(entry.getKey(), entry.getValue()));
        return valueList.toArray(new NameValuePair[valueList.size()]);
    }

    private static void handleWithCookie(String cookie, HttpClient client, HttpMethod method) {
        if (cookie != null && !cookie.isEmpty()) {
            client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            method.setRequestHeader("Cookie", cookie);
        } else
            client.getParams().setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
    }

    private static void handleCommonMethod(HttpMethod method) {
        method.getParams().setContentCharset("UTF-8");
        method.getParams().setSoTimeout(HTTP_REQUEST_TIME_OUT);
        method.setRequestHeader("Connection", "close");
    }
}
