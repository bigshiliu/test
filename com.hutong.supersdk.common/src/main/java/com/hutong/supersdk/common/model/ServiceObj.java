package com.hutong.supersdk.common.model;

/**
 * Service对象信息，用于对Service进行反射操作
 * @author QINZH
 *
 */
public class ServiceObj {
	
	private String serviceName;
	
	private String methodName;
	
	private String[] paramNames;
	
	private Class<?> returnType;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}
}
