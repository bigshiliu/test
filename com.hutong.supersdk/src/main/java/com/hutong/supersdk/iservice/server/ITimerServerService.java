package com.hutong.supersdk.iservice.server;

import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.model.JsonResObj;

@ServiceName("timerServerService")
public interface ITimerServerService extends IServerService {
	
	public JsonResObj loadTasks();
	
}
