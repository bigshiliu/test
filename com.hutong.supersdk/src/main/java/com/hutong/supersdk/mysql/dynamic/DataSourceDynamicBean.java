package com.hutong.supersdk.mysql.dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DataSourceDynamicBean extends ADynamicBean {
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	
	private static int timeBetweenEvictionRunsMillis = 200000;
	private static int minEvictableIdleTimeMillis = 600000;
	private static int initialSize = 50;
	private static int maxActive = 200;
	
	@Autowired
	@Qualifier("mysqlConnectionPoolInitialSize")
	private static String mysqlConnectionPoolInitialSize;
	
	@Autowired
	@Qualifier("mysqlConnectionPoolMaxActive")
	private static String mysqlConnectionPoolMaxActive;
	
	public DataSourceDynamicBean(String beanName) {
		super(beanName);
		
		if (mysqlConnectionPoolInitialSize != null && Integer.parseInt(mysqlConnectionPoolInitialSize) > initialSize) {
			initialSize = Integer.parseInt(mysqlConnectionPoolInitialSize);
		}
		if (mysqlConnectionPoolMaxActive != null && Integer.parseInt(mysqlConnectionPoolMaxActive) > maxActive) {
			maxActive = Integer.parseInt(mysqlConnectionPoolMaxActive);
		}
	}

	@Override
	public String getBeanXml() {
		StringBuffer xmlBuf = new StringBuffer();
		xmlBuf.append("<bean id=\""+super.beanName+"\" class=\"org.apache.commons.dbcp.BasicDataSource\" destroy-method=\"close\" >\n");
		xmlBuf.append("<property name=\"driverClassName\" value=\"" + this.driverClassName + "\" />\n");
		xmlBuf.append("<property name=\"url\" value=\"" + this.url + "\" />\n");
		xmlBuf.append("<property name=\"username\" value=\"" + this.username + "\" />\n");
		xmlBuf.append("<property name=\"password\" value=\"" + this.password + "\" />\n");
		xmlBuf.append("<property name=\"initialSize\" value=\"" + initialSize + "\" />\n");
		xmlBuf.append("<property name=\"maxActive\" value=\"" + maxActive + "\" />\n");
		xmlBuf.append("<property name=\"validationQuery\" value=\"SELECT COUNT(*) FROM DUAL\" />\n");
		xmlBuf.append("<property name=\"testOnBorrow\" value=\"true\" />\n");
		xmlBuf.append("<property name=\"testOnReturn\" value=\"true\" />\n");
		xmlBuf.append("<property name=\"testWhileIdle\" value=\"true\" />\n");
		xmlBuf.append("<property name=\"minEvictableIdleTimeMillis\" value=\"" + minEvictableIdleTimeMillis + "\" />\n");
		xmlBuf.append("<property name=\"timeBetweenEvictionRunsMillis\" value=\"" + timeBetweenEvictionRunsMillis + "\" />\n");
		xmlBuf.append("<property name=\"numTestsPerEvictionRun\" value=\"20\" />\n");
		xmlBuf.append("</bean>");
		
		return xmlBuf.toString();
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
