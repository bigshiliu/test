package com.hutong.supersdk.service;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.ITimerTask;
import com.hutong.supersdk.iservice.server.ITimerServerService;
import com.hutong.supersdk.mysql.config.dao.TimerConfigDao;
import com.hutong.supersdk.mysql.config.model.TimerConfig;

@Service
public class TimerService implements ITimerServerService {
	
	private static final Log logger = LogFactory.getLog(TimerService.class);
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
	@Autowired
	private TimerConfigDao timerConfigDao;

	@Override
	public JsonResObj loadTasks() {
		JsonResObj ret = new JsonResObj();
		
		logger.debug("Load Timer Tasks.");
		//schedulerFactoryBean 由spring创建注入
		Scheduler scheduler = schedulerFactoryBean.getScheduler();
		//这里获取任务信息数据
		List<TimerConfig> timerConfigs = timerConfigDao.loadAll();
		for (TimerConfig timerConfig : timerConfigs) {
			if (timerConfig.isEnable()){
				try {
					TriggerKey triggerKey = TriggerKey.triggerKey(timerConfig.getName(), timerConfig.getGroupName());
					//获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
					CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
					//不存在，创建一个
					if (null == trigger) {
						JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
								.withIdentity(timerConfig.getName(), timerConfig.getGroupName()).build();
						//装配jobDetail数据
						jobDetail.getJobDataMap().put(QuartzJobFactory.TARGET_OBJECT, timerConfig.getClassName());
						jobDetail.getJobDataMap().put(QuartzJobFactory.TARGET_TYPE, timerConfig.getType());
						
						//表达式调度构建器
						CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(timerConfig.getCronExpression());
						//按新的cronExpression表达式构建一个新的trigger
						trigger = TriggerBuilder.newTrigger().withIdentity(timerConfig.getName(), timerConfig.getGroupName()).withSchedule(scheduleBuilder).build();
						scheduler.scheduleJob(jobDetail, trigger);
					} else {
						//Trigger已存在，那么更新相应的定时设置
						//表达式调度构建器
						CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(timerConfig
							.getCronExpression());
						//按新的cronExpression表达式重新构建trigger
						trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
							.withSchedule(scheduleBuilder).build();
						//按新的trigger重新设置job执行
						scheduler.rescheduleJob(triggerKey, trigger);
					}
				}
				catch (SchedulerException ex) {
					logger.error("", ex);
					ret.setException(ex);
					return ret.fail();
				}
			}
		}
		
		return ret.ok();
	}
	
	@DisallowConcurrentExecution
	public static class QuartzJobFactory implements Job {
		
		private static final Log logger = LogFactory.getLog(QuartzJobFactory.class);
		
		public static final String TARGET_OBJECT = "targetObject";
		public static final String TARGET_TYPE = "type";

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			
			String className = (String) context.getMergedJobDataMap().get(TARGET_OBJECT);
			
			if(StringUtils.isEmpty(className))
	            return;
			
			Class<?> targetClazz = null;
			try {
				targetClazz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.error("", e);
				return;
			}
			
			
			String type = (String) context.getMergedJobDataMap().get(TARGET_TYPE);
			
	        Object targetBean = SuperSDK.getInstance().getService(targetClazz);
	        if (targetBean == null) {
	        	logger.error(className + " not found in SuperSDK Context.");
	        	return;
	        }
	          
	        // 判断是否是实现了MyJob接口  
	        if(!(targetBean instanceof ITimerTask))
	            return;
	          
	        // 执行相应的任务  
	        ((ITimerTask) targetBean).doTimerTask(type);
		}
	}

}
