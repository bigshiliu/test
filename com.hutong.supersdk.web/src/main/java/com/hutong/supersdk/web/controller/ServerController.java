package com.hutong.supersdk.web.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.server.IServerService;
import com.hutong.supersdk.service.common.ServiceInvoker;

@Controller
public class ServerController extends ABaseController {
	
	private static final Log logger = LogFactory.getLog(ServerController.class);
	
	public ServerController(){
		logger.info("new ServerController");
	}

	/**
	 * SuperSDK Web组件与App组件的调用入
	 * @param servicePrefix 调用的Service的完整名称，由Common组件中接口类的注释@ServiceName标记
	 * @param methodName 调用的Service中对应方法的完整名称
	 * @param request HTTP请求
	 * @return
	 */
	@RequestMapping("/web/{servicePrefix}/{methodName}")
	@ResponseBody
	public Object doService(@PathVariable final String servicePrefix, @PathVariable final String methodName,
							final HttpServletRequest request, HttpServletResponse response) {
		return super.doServiceWithException(servicePrefix, methodName, request, response);
	}

	@Override
	protected JsonResObj doing(String servicePrefix, String methodName, Map<String, String> paramMap)
			throws Exception {
		String serviceName = this.getServiceName(servicePrefix);

		//获取SuperSDK Service管理器中的IAppService的具体实现类
		IServerService iService = SuperSDK.getInstance().getServerService(serviceName);
		if(null == iService)
			throw new SuperSDKException(ErrorEnum.SERVICE_NOT_FOUND.errorCode);

		//通过反射方式调用具体的对应方
		Object returnValue = ServiceInvoker.invokeService(iService, serviceName, methodName, paramMap);
		return (JsonResObj) returnValue;
	}

	@Override
	protected String getServiceName(String servicePrefix) {
		return servicePrefix + "ServerService";
	}
}
