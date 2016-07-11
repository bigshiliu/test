package com.hutong.supersdk.web.controller;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.iservice.desktop.IDesktopService;
import com.hutong.supersdk.service.common.ServiceInvoker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 为桌面应用程序提供的接口类
 * Created by Administrator on 2015/12/4.
 */
@Controller
public class DesktopController extends ABaseController {

    /**
     * SuperSDK Web组件与Client组件的调用入
     * @param servicePrefix 调用的Service的完整名称，由Common组件中接口类的注释@ServiceName标记
     * @param methodName 调用的Service中对应方法的完整名称
     * @param request HTTP请求
     * @return 返回
     */
    @RequestMapping("/desktop/{servicePrefix}/{methodName}")
    @ResponseBody
    public Object doService(@PathVariable final String servicePrefix, @PathVariable final String methodName,
                            final HttpServletRequest request, HttpServletResponse response) {
        return super.doServiceWithException(servicePrefix, methodName, request, response);
    }

    @Override
    protected JsonResObj doing(String servicePrefix, String methodName, Map<String, String> paramMap) throws Exception {
        String serviceName = this.getServiceName(servicePrefix);

        //获取SuperSDK Service管理器中的IDesktopService的具体实现类
        IDesktopService iService = SuperSDK.getInstance().getDesktopService(serviceName);
        if(null == iService)
            throw new SuperSDKException(serviceName + " service not found");

        //通过反射方式调用具体的对应方
        Object returnValue = ServiceInvoker.invokeService(iService, serviceName, methodName, paramMap);
        return (JsonResObj) returnValue;
    }

    @Override
    protected String getServiceName(String servicePrefix) {
        return servicePrefix + "DesktopService";
    }
    

}
