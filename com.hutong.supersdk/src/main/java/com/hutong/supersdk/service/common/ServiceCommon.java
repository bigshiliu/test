package com.hutong.supersdk.service.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;

public class ServiceCommon {
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(ServiceCommon.class);

	public static <T> T getServiceByConfigPlatform(SdkConfig sdkConfig, Class<T> clazz) {
		return getServiceByConfigPlatform(sdkConfig, clazz, false);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getServiceByConfigPlatform(SdkConfig sdkConfig, Class<T> clazz,boolean checkNullObject) {
		if(null == sdkConfig){
			return null;
		}
		String sdkBeanName = sdkConfig.getHandleBean();
		Object bean = SuperSDK.getInstance().getService(sdkBeanName);
		if (clazz != null && clazz.isAssignableFrom(bean.getClass())) {
			return (T) bean;
		}else if(!checkNullObject){
			return null;
		}else{
			return null;
		}
	}
}
