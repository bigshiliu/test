package com.hutong.supersdk.mysql.dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态bean描述
 * 如果想让某些bean可以动态加载，定义动态加载对象并继承此抽象类，
 * 之后调用getXml()接口既可返回spring配置字符串
 */
public abstract class ADynamicBean {
	
	protected String beanName;
	protected List<String> dataSources;
	
	public ADynamicBean(String beanName) {
		this.beanName = beanName;
	}
	
	public String getBeanName() {
		return beanName;
	}
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	public List<String> getDataSources() {
		if(dataSources == null) {
			dataSources = new ArrayList<String>();
		}
		return dataSources;
	}
	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}
	public void addDataSource(String dataSourceBeanName) {
		this.getDataSources().add(dataSourceBeanName);
	}
	/**
	 * 获取bean 的xml描述
	 * @return
	 */
	public abstract String getBeanXml();
	
	/**
	 * 生成完整的xml字符串
	 * @return
	 */
	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n")
			.append("<beans xmlns=\"http://www.springframework.org/schema/beans\"")
			.append("		xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tx=\"http://www.springframework.org/schema/tx\"")
			.append("		xmlns:context=\"http://www.springframework.org/schema/context\" xmlns:p=\"http://www.springframework.org/schema/p\"")
			.append("		xsi:schemaLocation=\"")
			.append("	    http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-3.0.xsd")
			.append("	    http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd")
			.append("	    http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx-3.0.xsd\" >\n")
			.append(getBeanXml())
			.append("</beans>");
		return buf.toString();
	}
}