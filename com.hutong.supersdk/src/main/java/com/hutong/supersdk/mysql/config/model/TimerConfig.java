package com.hutong.supersdk.mysql.config.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hutong.supersdk.mysql.ABaseDomain;

@Entity
@Table( name = "T_TIMER_CONFIG" )
public class TimerConfig extends ABaseDomain {
	
	public static final String CONCURRENT_ON = "on";
	
	private static final long serialVersionUID = 1L;
	
	private static final int ENABLE_FLAG_ENABLED = 1;
	@SuppressWarnings("unused")
	private static final int ENABLE_FLAG_DISABLED = 0;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id")
	private int id;
	
	@Column(name = "Group_Name")
	private String groupName;
	
	@Column(name = "Name")
	private String name;
	
	@Column(name = "Class_Name")
	private String className;
	
	@Column(name = "Type")
	private String type;
	
	@Column(name = "Concurrent")
	private String concurrent;
	
	@Column(name = "Cron_Expression")
	private String cronExpression;
	
	@Column(name = "Time_Zone")
	private String timeZone;
	
	@Column(name = "Enable_Flag")
	private int enableFlag;
	
	public boolean isEnable() {
		return ENABLE_FLAG_ENABLED == this.enableFlag;
	}
	
	public boolean isConcurrent() {
		return this.concurrent.equalsIgnoreCase(CONCURRENT_ON);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String group) {
		this.groupName = group;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConcurrent() {
		return concurrent;
	}

	public void setConcurrent(String concurrent) {
		this.concurrent = concurrent;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String expression) {
		this.cronExpression = expression;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public int getEnableFlag() {
		return enableFlag;
	}

	public void setEnableFlag(int enableFlag) {
		this.enableFlag = enableFlag;
	}
}
