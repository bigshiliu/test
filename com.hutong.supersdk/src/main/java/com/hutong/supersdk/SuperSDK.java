package com.hutong.supersdk;

import java.util.HashMap;
import java.util.Map;


import com.hutong.supersdk.iservice.desktop.IDesktopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import com.hutong.supersdk.common.iservice.IClientService;
import com.hutong.supersdk.common.iservice.IService;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.iservice.app.IAppService;
import com.hutong.supersdk.iservice.server.IServerService;
import com.hutong.supersdk.mysql.dynamic.DynamicSourceUtils;
import com.hutong.supersdk.sdk.ISDK;

/**
 * 用户中心主类
 * @author Dongxu
 *
 */
public class SuperSDK {
	private static final Logger log = LoggerFactory.getLogger(SuperSDK.class);
	
	private static SuperSDK instance;//用户中心实例
	private static boolean isInit = false;
	
	private static final Object lock = new Object();
	
	/**
	 * spring容器
	 */
	private static AbstractXmlApplicationContext coreContext = null;
	
	/**
	 * 所有SDK_ID,handleBean映射Map
	 */
	private static Map<String, String> handleBeanMap = new HashMap<String, String>();
	
	/**
	 * Client组件类名对应的执行Service Map
	 */
	private static Map<String, IClientService> servicesMap = new HashMap<String, IClientService>();
	
	/**
	 * Server端组件类名对应的执行Service Map
	 */
	private static Map<String, IServerService> serverServicesMap = new HashMap<String, IServerService>();
	
	/**
	 * App端组件类名对应的执行Service Map
	 */
	private static Map<String, IAppService> appServicesMap = new HashMap<String, IAppService>();

	/**
	 * Desktop端组件类名称的执行Service map
	 */
	private static Map<String, IDesktopService> desktopServiceMap = new HashMap<String, IDesktopService>();

	/**
	 * 获取SuperSDK实例
	 * @return
	 */
	public static SuperSDK getInstance(){
		if(null == instance){
			synchronized (lock) {
				if(null == instance){
					log.info("SuperSDK create instance...");
					SuperSDK temp = new SuperSDK();
					temp.init();
					instance = temp;
				}
			}
		}
		return instance;
	}

	/**
	 * 初始化SuperSDK
	 */
	private void init() {
		if(!isInit) {
			log.info("SuperSDK init...");
			//初始化spring容器
			coreContext = new ClassPathXmlApplicationContext(Config.SPRING_XML_PATH);
			initServices();
			isInit = true;
			//初始化数据源
			DynamicSourceUtils.initDataSources(coreContext);
			log.info("SuperSDK init done!");
		}
	}
	
	/**
	 * 初始化SuperSDK的Service管理器
	 */
	private void initServices() {
		//初始化Client类型的Service
		Map<String, IClientService> map = coreContext.getBeansOfType(IClientService.class);
		for (String beanName : map.keySet()) {
			ServiceName name = getServiceNameByInterface(map.get(beanName),IClientService.class);
			servicesMap.put(name.value(), map.get(beanName));
		}
		
		//初始化Server类型的Service
		Map<String, IServerService> serverMap = coreContext.getBeansOfType(IServerService.class);
		for (String beanName : serverMap.keySet()) {
			ServiceName name = getServiceNameByInterface(serverMap.get(beanName),IServerService.class);
			serverServicesMap.put(name.value(), serverMap.get(beanName));
		}
		
		//初始化AppService类型的Service
		Map<String, IAppService> appMap = coreContext.getBeansOfType(IAppService.class);
		for (String beanName : appMap.keySet()) {
			ServiceName name = getServiceNameByInterface(appMap.get(beanName), IAppService.class);
			appServicesMap.put(name.value(), appMap.get(beanName));
		}

		//初始化DesktopService类型的Service
		Map<String, IDesktopService> desktopMap = coreContext.getBeansOfType(IDesktopService.class);
		for (String beanName : desktopMap.keySet()) {
			ServiceName name = getServiceNameByInterface(desktopMap.get(beanName), IDesktopService.class);
			desktopServiceMap.put(name.value(), desktopMap.get(beanName));
		}

		//初始化ISDK类型的Class,获得所有继承ISDK的子类,初始化时添加相应的初始化参数
		Map<String, ISDK> sdkMap = coreContext.getBeansOfType(ISDK.class);
		for(String handleBeanName : sdkMap.keySet()) {
			ISDK sdk = (ISDK) coreContext.getBean(handleBeanName);
			handleBeanMap.put(sdk.getSDKId(), handleBeanName);
		}
	}
	
	/**
	 * 根据接口类型获取注释的ServiceName
	 * @param o 类对象
	 * @param iServiceClazz 接口类型
	 * @return
	 */
	private static ServiceName getServiceNameByInterface(Object o, Class<? extends IService> iServiceClazz){
		Class<?>[] clazzes = ClassUtils.getAllInterfaces(o);
		for (int j = 0; j < clazzes.length; j++) {
			Class<?> clazz = clazzes[j];
			if(ClassUtils.isAssignable(iServiceClazz,clazz)){
				ServiceName name = AnnotationUtils.findAnnotation(clazz, ServiceName.class);
				return name;
			}
		}
		return null;
	}

	/**
	 * 根据接口名称获取IClientService的实现对象
	 * @param serviceName 类名
	 * @return
	 */
	public IClientService getClientService(String serviceName) {
        return servicesMap.get(serviceName);
	}

    /**
     * 根据接口名称获取IDesktopService的实现对象
     * @param appServiceName
     * @return
     */
	public IAppService getAppService(String appServiceName) {
		return appServicesMap.get(appServiceName);
	}
	
	/**
	 * 根据类名获取IserverService的具体实现类
	 * @param serviceName 类名
	 * @return
	 */
	public IServerService getServerService(String serviceName) {
		return serverServicesMap.get(serviceName);
	}

    /**
     * 根据类名获取IDesktopService的具体实现类
     * @param serviceName 类名
     * @return
     */
    public IDesktopService getDesktopService(String serviceName) {
        return desktopServiceMap.get(serviceName);
    }
	
	/**
	 * 根据类的类型获取IServerService的具体实现类
	 * @param clazz 类类型
	 * @return
	 */
	public <T> T getService(Class<T> clazz) {
        return coreContext.getBean(clazz);
	}
	
	/**
	 * 根据sdkId获取具体handleBean
	 * @param sdkId
	 * @return
	 */
	public String getHandleBean(String sdkId) {
		return handleBeanMap.get(sdkId);
	}

	/**
	 * 根据对象名称获取对象
	 * @param serviceName 对象名
	 * @return
	 */
	public Object getService(String serviceName) {
		return coreContext.getBean(serviceName);
	}
}
