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
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.IClientService;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.service.common.ServiceInvoker;

/**
 * SuperSDK Web组件与Client组件的调用入
 * @author Dongxu
 *
 */
@Controller
public class ClientController extends ABaseController {

	private static final Log logger = LogFactory.getLog(ClientController.class);
	
	public ClientController(){
		logger.info("new ClientController");
	}

	/**
	 * SuperSDK Web组件与Client组件的调用入
	 * @param serviceName 调用的Service的完整名称，由Common组件中接口类的注释@ServiceName标记
	 * @param methodName 调用的Service中对应方法的完整名称
	 * @param request HTTP请求
	 * @return
	 */
	@RequestMapping("/client/{serviceName}/{methodName}")
	@ResponseBody
	public Object doService(@PathVariable final String serviceName, @PathVariable final String methodName,
							final HttpServletRequest request, HttpServletResponse response) {
		return super.doServiceWithException(serviceName, methodName, request, response);
	}

	@Override
	protected JsonResObj doing(String servicePrefix, String methodName, Map<String, String> paramMap) throws Exception {
		String serviceName = this.getServiceName(servicePrefix);

		//获取SuperSDK Service管理器中的IClientService的具体实现类
		IClientService iClientService = SuperSDK.getInstance().getClientService(serviceName);
		if(null == iClientService)
			throw new SuperSDKException(serviceName + " service not found");

		//通过反射方式调用具体的对应方
		Object returnValue = ServiceInvoker.invokeService(iClientService, serviceName, methodName, paramMap);
		return (JsonResObj) returnValue;
	}

	@Override
	protected String getServiceName(String serviceName) {
		return serviceName;
	}
}
