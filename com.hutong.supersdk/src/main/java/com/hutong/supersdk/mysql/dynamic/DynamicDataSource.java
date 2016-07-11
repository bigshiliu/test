package com.hutong.supersdk.mysql.dynamic;

import java.util.Map;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.hutong.supersdk.service.common.ThreadHelper;

/**
 * 数据源路由
 * 通过determineCurrentLookupKey接口获取key
 * 通过setTargetDataSources接口设置数据源容器，该容器为K-V容器	K=serverKey  V=对应的DataSource
 * @author QINZH
 *
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	
	public DynamicDataSource() {
		logger.info("Create DynamicDataSource:" + this.toString());
	}
	
	/**
	 * 根据当前线程的AppId的值确定使用哪一个数据源
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return ThreadHelper.getAppId();
}
	
	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
	}
}
