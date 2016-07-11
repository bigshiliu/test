package com.hutong.supersdk.web.controller;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.web.WebUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 基类controller
 * Created by Dongxu on 2015/12/4.
 */
public abstract class ABaseController {

    private static final Log logger = LogFactory.getLog(ABaseController.class);

    public Object doServiceWithException(final String servicePrefix, final String methodName,
                                         final HttpServletRequest request, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        JsonResObj json = new JsonResObj();
        Map<String, String> paramMap = WebUtil.getParamMapByRequest(request);

        try {
            JsonResObj ret = doing(servicePrefix, methodName, paramMap);
            json = (ret != null) ? ret : json.fail();
        } catch (SuperSDKException e) {
            logger.error("SuperSDKException in " + getServiceName(servicePrefix) + " methodName:" + methodName, e);
            json.setException(e);
            json.fail();
        } catch (Exception e) {
            logger.error("Error in " + getServiceName(servicePrefix) + " methodName:" + methodName, e);
            json.setException(e);
            json.fail();
        }

        if(logger.isDebugEnabled()){
            StringBuilder sb = new StringBuilder();
            try {
                sb.append("\t").append(WebUtil.getUri(request));
                sb.append("\t@Request[");
                sb.append(System.currentTimeMillis() - startTime);
                sb.append("ms]: request IP : ").append(WebUtil.getIpAddr(request)).append(" ");
                sb.append(getServiceName(servicePrefix));
                sb.append(".");
                sb.append(methodName);
                sb.append(ParseJson.encodeJson(paramMap));
                sb.append("\t@Response: ");
                sb.append(ParseJson.encodeJson(json));
            } catch (Exception e) {
                logger.warn("Log Error",e);
            }
            logger.debug(sb.toString());
        }

        //强制要求客户端关闭此http连接
        response.setHeader("Connection", "close");
        //设置返回内容
        response.setContentType("application/json;charset=UTF-8");

        return json.ready();
    }

    protected abstract JsonResObj doing(String servicePrefix, String methodName, Map<String, String> paramMap)
            throws Exception;

    protected abstract String getServiceName(String servicePrefix);
}
