package com.hutong.supersdk.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.iservice.ITimerTask;
import com.hutong.supersdk.iservice.notice.ICheckOrderService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.CheckOrderQueueDao;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.CheckOrderQueue;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.service.common.ServiceCommon;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.util.SDKConfigUtil;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutionException;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;

@Service
public class CheckOrderService implements ICheckOrderService, ITimerTask {
	
	private static final Log logger = LogFactory.getLog(CheckOrderService.class);
	
	@Autowired
	private AppConfigDao appConfigDao;
	
	@Autowired
	private CheckOrderQueueDao coqDao;
	
	@Autowired
	private SDKConfigDao sdkConfigDao;
	
	@Autowired
	private PaymentOrderDao paymentOrderDao;
	
	
	public static final String CURRENT_QUEUE = "CURRENT";
	public static final Integer CURRENT_QUEUE_CHECK_TIMES = 3;
	private BlockingQueue<CheckOrderQueue> currentQueue = new LinkedBlockingQueue<CheckOrderQueue>();
	//活动线程的自动检查订单的睡眠时间
	private static final Integer CURRENT_QUEUE_SLEEP_TIME = 5000;
	
	public static final String FIRST_QUEUE = "FIRST";
	public static final Integer FIRST_QUEUE_NOTICE_TIMES = 6;
	private BlockingQueue<CheckOrderQueue> firstQueue = new LinkedBlockingQueue<CheckOrderQueue>();
	
	public static final String SECOND_QUEUE = "SECOND";
	public static final Integer SECOND_QUEUE_NOTICE_TIMES = 10;
	private BlockingQueue<CheckOrderQueue> secondQueue = new LinkedBlockingQueue<CheckOrderQueue>();
	
	public static final String FAILED_QUEUE = "FAILED";
	
	public static final Integer MAX_NOTICE_THREAD = 10;
	
	private static final String TYPE_CURRENT_QUEUE = "current";
	private static final String TYPE_FIRST_QUEUE = "first";
	private static final String TYPE_SECOND_QUEUE = "second";
	
	/**
	 * 系统启动后创建，持久化进行订单的检查任务
	 */
	private CurrentQueueThead curQueThread = null;
	
	@Override
	public void queueToCheck(final PaymentOrder pOrder) {
		CheckOrderQueue coQue = new CheckOrderQueue(pOrder);
		coqDao.save(coQue);
		this.queue(coQue);
	}
	
	@Override
	public void doTimerTask(String type) {
		if (type.equalsIgnoreCase(TYPE_CURRENT_QUEUE)) {
			this.currentQueueCheck();
		}
		else if (type.equalsIgnoreCase(TYPE_FIRST_QUEUE)) {
			this.firstQueueCheck();
		}
		else if (type.equalsIgnoreCase(TYPE_SECOND_QUEUE)) {
			this.secondQueueCheck();
		}
	}
	
	private void firstQueueCheck() {
		logger.debug("[CheckOrder Queue]CheckOrderFirst Queue Start.");
		loadQueue(this.firstQueue, FIRST_QUEUE);
		
		this.checkQueue(this.firstQueue);
		logger.debug("[CheckOrder Queue]CheckOrderFirst Queue End.");
	}
	
	private void secondQueueCheck() {
		logger.debug("[CheckOrder Queue]CheckOrderSecond Queue Start.");
		loadQueue(this.secondQueue, SECOND_QUEUE);
		
		this.checkQueue(this.secondQueue);
		logger.debug("[CheckOrder Queue]CheckOrderSecond Queue End.");
	}
	
	private void currentQueueCheck() {
		if (this.curQueThread == null)
			this.curQueThread = new CurrentQueueThead(this);
		
		if (!this.curQueThread.isAlive()) {
			logger.debug("[CheckOrder Queue]Current CheckOrder Queue Thread Created.");
			this.curQueThread.start();
			logger.debug("[CheckOrder Queue]Current CheckOrder Queue Thread Started.");
		}
		else {
			logger.debug("[CheckOrder Queue]Current CheckOrder Queue Thread Check.");
		}
	}
	
	private void checkCurrentQueue() {
		logger.debug("[CheckOrder Queue]CheckOrderCurrent Queue Start.");
		loadQueue(this.currentQueue, CURRENT_QUEUE);
		
		this.checkQueue(this.currentQueue);
		logger.debug("[CheckOrder Queue]CheckOrderCurrent Queue End.");
	}

	private void loadQueue(BlockingQueue<CheckOrderQueue> queue, String queueType) {
		if (queue.isEmpty()) {
			List<AppConfig> appConfigList = appConfigDao.loadAll();
			for (AppConfig app : appConfigList) {
				//设置线程标志，确保数据源逻辑正确
				ThreadHelper.setAppId(app.getAppId());
				
				logger.debug("[CheckOrder Queue]" + queueType + " is Empty. Load Data From DB With AppId is " + app.getAppId() + ".");
				List<CheckOrderQueue> coqList = coqDao.getNoticesByType(queueType);
				if (coqList != null && !coqList.isEmpty()) {
					for (CheckOrderQueue q : coqList) {
						this.queue(q);
					}
				}
			}
		}
	}
	
	private void checkQueue(BlockingQueue<CheckOrderQueue> queue) {
		ExecutorService exeService = Executors.newFixedThreadPool(MAX_NOTICE_THREAD);
		
		List<CheckOrderQueue> failedQueue = new ArrayList<CheckOrderQueue>();
		
		while (!queue.isEmpty()) {
			CheckOrderQueue pQue;
			try {
				pQue = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			
			if (pQue != null) {
				ThreadHelper.setAppId(pQue.getAppId());
				
				final CheckOrderQueue finalPQue = pQue;
				
				Future future = exeService.submit(new Callable() {
					
					@Override
					public Object call() throws Exception {
						//设置线程标志，确保数据源逻辑正确
						ThreadHelper.setAppId(finalPQue.getAppId());
						
						if (!check(finalPQue)) {
							return Boolean.FALSE;
						}
						return Boolean.TRUE;
					}
				});
				
				Object obj;
				try {
					obj = future.get();
				} catch (InterruptedException e) {					
					obj = null;
					e.printStackTrace();
				} 
				catch (ExecutionException e) {
					obj = null;
					e.printStackTrace();
				}
				
				Boolean ret = Boolean.FALSE;
				if (obj != null) {
					ret = (Boolean) obj;
				}
				if (ret) {
					coqDao.remove(pQue);
				}
				else {
					pQue.check();
					coqDao.update(pQue);
					failedQueue.add(pQue);
				}
			}
		}
		
		exeService.shutdown();
		
		for (CheckOrderQueue pq : failedQueue) {
			this.queue(pq);
		}
	}
	
	@Transactional(rollbackFor = Exception.class, value = "instTx")
	private boolean check(CheckOrderQueue coQue) {
		PaymentOrder pOrder = paymentOrderDao.get(coQue.getOrderId());
		if (pOrder == null) {
			logger.error(ErrorEnum.ORDER_ID_NOT_FOUND.toString() + " orderId=" + coQue.getOrderId());
			return false;
		}
		
		//根据channelID获取SDK配置信息
		SdkConfig sdkConfig = sdkConfigDao.getByPoId(pOrder.getAppId(), pOrder.getAppChannelId());
		if (null == sdkConfig) {
			logger.error(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND.toString() + " appId=" + pOrder.getAppId() + " channelId=" + pOrder.getAppChannelId());
			return false;
		}
		
		if (!pOrder.isPaid()) {
			SDKCheckOrderRet ret;
			//根据SDK配置信息,找到对应的,继承了ICheckOrderSDK接口的实现类
			ICheckOrderSDK sdk = ServiceCommon.getServiceByConfigPlatform(sdkConfig, ICheckOrderSDK.class);
			if (sdk != null) {
				Object config = SDKConfigUtil.json2Config(sdkConfig.getConfigInfo(), sdk.getConfigClazz());
				ret = sdk.checkOrder(pOrder, null, config);
				
				if (ret.isAddCheckQueue()) {
					logger.info("[CheckOrder Queue]OrderId:" + pOrder.getOrderId() + " check failed.");
					return false;
				}
				else {
					logger.info("[CheckOrder Queue]OrderId:" + pOrder.getOrderId() + " check success.");
					return true;
				}
			}
			logger.info("[CheckOrder Queue]OrderId:" + pOrder.getOrderId() + " check failed, because sdk is null.");
			return false;
		}
		else {
			logger.info("[CheckOrder Queue]OrderId:" + pOrder.getOrderId() + " check success.");
			return true;
		}
	}

	private void queue(CheckOrderQueue coQue) {
		if (coQue.getQueueType().equals(CURRENT_QUEUE)) {
			if (!this.currentQueue.add(coQue)) {
				logger.error("[CheckOrder Queue]OrderId:" + coQue.getOrderId() + " add to current_queue failed.");
			}
		}
		else if (coQue.getQueueType().equals(FIRST_QUEUE)) {
			if (!this.firstQueue.add(coQue)) {
				logger.error("[CheckOrder Queue]OrderId:" + coQue.getOrderId() + " add to five_minutes_queue failed.");
			}
		}
		else if (coQue.getQueueType().equals(SECOND_QUEUE)) {
			if (!this.secondQueue.add(coQue)) {
				logger.error("[CheckOrder Queue]OrderId:" + coQue.getOrderId() + " add to one_hour_queue failed.");
			}
		}
	}
	
	private static class CurrentQueueThead extends Thread {
		private CheckOrderService checkOrderService;
		
		public CurrentQueueThead(CheckOrderService checkOrderService) {
			super("Current_Queue_Thread");
			
			this.checkOrderService = checkOrderService;
		}
		
		@Override
		public void run() {
			//noinspection InfiniteLoopStatement
			while (true) {
				this.checkOrderService.checkCurrentQueue();
				try {
					Thread.sleep(CURRENT_QUEUE_SLEEP_TIME);
				} catch (InterruptedException e) {
					logger.error("[CheckOrder Queue]CurrentQueueThread Interrupted.", e);
				}
			}
		}
	}
}
