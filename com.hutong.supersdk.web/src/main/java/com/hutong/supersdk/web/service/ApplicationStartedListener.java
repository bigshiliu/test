package com.hutong.supersdk.web.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.server.ITimerServerService;

@Service
public class ApplicationStartedListener implements ApplicationListener<ContextRefreshedEvent> {
	
	private static final Log logger = LogFactory.getLog(ApplicationStartedListener.class);

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		
		//root application context 没有parent
		if (event.getApplicationContext().getParent() == null) {
			logger.debug("Start Load Timer Tasks");
			
			//需要执行的逻辑代码，当spring容器初始化完成后就会执行该方法
			JsonResObj ret = SuperSDK.getInstance().getService(ITimerServerService.class).loadTasks();
			
			if (ret.isOk()) {
				logger.debug("Loaded Timer Tasks. ret=" + ret);
			}
			else {
				logger.error("Load Timer Tasks Failed. ret=" + ret);
			}
		}
	}

}
