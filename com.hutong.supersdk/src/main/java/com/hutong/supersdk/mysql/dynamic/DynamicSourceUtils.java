package com.hutong.supersdk.mysql.dynamic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractXmlApplicationContext;

import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.dao.DbDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.config.model.Db;

public class DynamicSourceUtils {

	private static final Logger logger = LoggerFactory.getLogger(DynamicSourceUtils.class);

	public static final String DB_NAME_PRE = "SDB_";
	
	private static Map<Object, Object> dbSources = new HashMap<Object, Object>();
	
	private static Map<Object, Object> instSources = new HashMap<Object, Object>();

	public static void initDataSources(AbstractXmlApplicationContext coreContext) {
		// 初始化数据源时,默认加载所有数据源
		DynamicSourceUtils.addDataSources(coreContext);
	}

	public static boolean addDataSources(AbstractXmlApplicationContext coreContext) {
		DbDao dbDao = coreContext.getBean(DbDao.class);
		List<Db> dbs = new ArrayList<Db>();
		dbs = dbDao.loadAll();
		for (Db db : dbs) {

			String dataSourceBeanName = getInstDataSourceBeanName(db.getDbId());

			// 注册动态数据源配置
			DataSourceDynamicBean dataSourceDynamicBean = new DataSourceDynamicBean(dataSourceBeanName);
			dataSourceDynamicBean.setDriverClassName(db.getDbDrive());
			dataSourceDynamicBean.setUrl(db.getDbUrl());
			dataSourceDynamicBean.setUsername(db.getDbUsername());
			dataSourceDynamicBean.setPassword(db.getDbPassword());

			// 将spring容器转成动态可配置型
			ConfigurableApplicationContext configApplicationContext = (ConfigurableApplicationContext) coreContext;
			// 获取Ioc容器中的bean定义注册表
			XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(
					(BeanDefinitionRegistry) configApplicationContext.getBeanFactory());
			reader.setResourceLoader(configApplicationContext);
			reader.setEntityResolver(new ResourceEntityResolver(configApplicationContext));
			// 将新的bean加载到spring 容器中
			reader.loadBeanDefinitions(new DynamicResource(dataSourceDynamicBean));

			// db相应信息注册进此容器管理
			Object dataSource = configApplicationContext.containsBean(dataSourceBeanName)
					? configApplicationContext.getBean(dataSourceBeanName) : null;

			dbSources.put(dataSourceBeanName, dataSource);
			logger.info("Put DataSource : " + dataSourceBeanName.toString() + dataSource.toString() + " url:"
					+ db.getDbUrl());
		}

		AppConfigDao appConfigDao = coreContext.getBean(AppConfigDao.class);
		List<AppConfig> appConfigs = new ArrayList<AppConfig>();
		appConfigs = appConfigDao.loadAll();
		for (AppConfig appConfig : appConfigs) {
			Object dataSource = dbSources.get(getInstDataSourceBeanName(appConfig.getDbId()));
			instSources.put(appConfig.getAppId(), dataSource);
			logger.info("Put AppId: " + appConfig.getAppId() + " DbId : " + appConfig.getDbId() + " DataSource : "
					+ dataSource.toString());
		}

		DynamicDataSource ds = (DynamicDataSource) coreContext.getBean("instSource");
		// 更新spring容器中dynamicDataSource中的targetDataSources属性
		ds.setTargetDataSources(instSources);
		ds.afterPropertiesSet();
		logger.info("Map dynamicDataSource Over!");
		return true;
	}

	public static String getInstDataSourceBeanName(String dbId) {
		return DB_NAME_PRE + dbId;
	}
}
