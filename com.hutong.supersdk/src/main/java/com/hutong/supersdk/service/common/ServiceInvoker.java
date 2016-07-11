package com.hutong.supersdk.service.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.hutong.supersdk.common.constant.ErrorEnum;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.IService;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;

public class ServiceInvoker {
	/**
	 * 缓存所有service的所有Method对象
	 */
	private static Map<String,Map<String,Method>> classMethodsMap = new HashMap<String,Map<String,Method>>();
	private static final Object classMethodsMapLock = new Object();
	
	public static Object invokeService(IService service, String serviceName, String methodName, Map<String, String> paramMap) 
			throws SuperSDKException {
		Map<String,Method> methodsMap = classMethodsMap.get(serviceName);
		if(null == methodsMap){
			synchronized (classMethodsMapLock) {
				methodsMap = classMethodsMap.get(serviceName);
				if (null == methodsMap) {
					Class<?> serviceInterface = getServiceInterfaceByServiceName(service, serviceName);
                    if (serviceInterface != null) {
                        methodsMap = new HashMap<String, Method>();
                        Method[] methods = serviceInterface.getMethods();
                        for (Method method : methods) {
                            String tempMethodName = method.getName();
                            methodsMap.put(tempMethodName, method);
                        }
                        classMethodsMap.put(serviceName, methodsMap);
                    }
                    else
                        throw new SuperSDKException(ErrorEnum.ERROR);
				}
			}
		}
		
		Method invokeMethod = methodsMap.get(methodName);
		if(null == invokeMethod){
			throw new SuperSDKException("Not found this method");
		}
		String[] paramNames = getServiceParamNames(invokeMethod);
		Class<?>[] paramTypes = invokeMethod.getParameterTypes();
		Object[] params = new Object[paramNames.length];
		for (int j = 0; j < paramNames.length; j++) {
			String paramName = paramNames[j];
			String param = paramMap.get(paramName);
			parseParams(paramTypes, params, j, param);
		}
		try {
			invokeMethod.setAccessible(true);
			return invokeMethod.invoke(service, params);
		} catch (IllegalAccessException e) {
			throw new SuperSDKException("JVM not allow method invoke", e);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			if(cause == null){
				throw new SuperSDKException(e);
			}else if(cause instanceof SuperSDKException){
				throw (SuperSDKException)cause;
			}else{
				throw new SuperSDKException(cause);
			}
		}
	}
	
	private static Class<?> getServiceInterfaceByServiceName(Object o, String serviceName) {
		Class<?>[] clazzs = ClassUtils.getAllInterfaces(o);
        for (Class<?> clazz : clazzs) {
            if (ClassUtils.isAssignable(IService.class, clazz)) {
                ServiceName name = AnnotationUtils.findAnnotation(clazz, ServiceName.class);
                if (name.value().equals(serviceName)) {
                    return clazz;
                }
            }
        }
		return null;
	}
	
	private static String[] getServiceParamNames(Method method){
		ServiceParam parameterAnnotations = method.getAnnotation(ServiceParam.class);
		return null != parameterAnnotations ? parameterAnnotations.value() : new String[0];
	}

	private static void parseParams(Class<?>[] paramTypes, Object[] params,
			int j, String param) {
		if (paramTypes[j].equals(JsonReqObj.class)) {
			params[j] = ParseJson.getJsonContentByStr(param, JsonReqObj.class);
		} 
		else if (paramTypes[j].equals(Map.class)) {
			params[j] = ParseJson.getJsonContentByStr(param, Map.class);
		}
		else if(paramTypes[j].equals(int.class)){
			params[j] = Integer.parseInt(param);
		}
		else if(paramTypes[j].equals(double.class)){
			params[j] = Double.parseDouble(param);
		}
		else if (paramTypes[j].equals(float.class)) {
			params[j] = Float.parseFloat(param);
		}
		else if (paramTypes[j].equals(boolean.class)) {
			params[j] = Boolean.parseBoolean(param);
		}
		else if (paramTypes[j].equals(long.class)) {
			params[j] = Long.parseLong(param);
		}
		else{
			params[j] = param;
		}
	}
}
