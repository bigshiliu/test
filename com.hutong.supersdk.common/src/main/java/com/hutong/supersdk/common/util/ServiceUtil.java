package com.hutong.supersdk.common.util;

import java.lang.reflect.Method;

import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.ServiceObj;

/**
 * Service工具类
 * @author QINZH 
 *
 */
public class ServiceUtil {
	
	/**
	 * 获取接口定义方法的参数名
	 * @param method Java方法
	 * @return
	 */
	private static String[] getServiceParamNames(Method method){
		ServiceParam parameterAnnotations = method.getAnnotation(ServiceParam.class);
		if(null != parameterAnnotations){
			return parameterAnnotations.value();
		}
		return new String[0];
	}

	/**
	 * 获取包含该接口方法的ServiceName
	 * @param method Java方法
	 * @return
	 */
	private static String getServiceName(Method method) {
		ServiceName serviceName = method.getDeclaringClass().getAnnotation(ServiceName.class);
		if(null != serviceName){
			return serviceName.value();
		}
		return null;
	}

	/**
	 * 根据method获取Service对象信息
	 * @param proxy
	 * @param method Java方法
	 * @return
	 */
	public static ServiceObj getServiceObj(Object proxy, Method method) {
		ServiceObj serviceObj = new ServiceObj();		
		serviceObj.setServiceName(getServiceName(method));
		serviceObj.setMethodName(method.getName());		
		serviceObj.setParamNames(getServiceParamNames(method));		
		serviceObj.setReturnType(method.getReturnType());		
		return serviceObj;
	}
}
