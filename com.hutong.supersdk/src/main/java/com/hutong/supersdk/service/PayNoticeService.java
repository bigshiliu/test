package com.hutong.supersdk.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.EncryptUtil;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.iservice.ITimerTask;
import com.hutong.supersdk.iservice.notice.IPayNoticeService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.PayNoticeQueueDao;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.PayNoticeQueue;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.service.modeltools.PayNoticeData;

import edu.emory.mathcs.backport.java.util.concurrent.Callable;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import edu.emory.mathcs.backport.java.util.concurrent.Future;

@Service
public class PayNoticeService implements IPayNoticeService, ITimerTask {
	
	private static final Log logger = LogFactory.getLog(PayNoticeService.class);
	
	public static final String PAY_RENOTICE_APP_ALL = "all";
	public static final String PAY_RENOTICE_SCOPE_ALL = "all";
	public static final String PAY_RENOTICE_ORDER_SPLITOR = ";";
	
	@Autowired
	private AppConfigDao appConfigDao;
	
	@Autowired
	private SDKConfigDao sdkConfigDao;
	
	@Autowired
	private PaymentOrderDao paymentOrderDao;
	
	@Autowired
	private PayNoticeQueueDao payNQDao;
	
	public static final String FIRST_QUEUE = "FIRST";
	public static final Integer FIRST_QUEUE_NOTICE_TIMES = 3;
	
	private BlockingQueue<PayNoticeQueue> firstQueue = new LinkedBlockingQueue<PayNoticeQueue>();
	
	public static final String SECOND_QUEUE = "SECOND";
	public static final Integer SECOND_QUEUE_NOTICE_TIMES = 10;
	private BlockingQueue<PayNoticeQueue> secondQueue = new LinkedBlockingQueue<PayNoticeQueue>();
	
	public static final String FAILED_QUEUE = "FAILED";
	public static final String NOTICE_SUCCESS = "SUCCESS";
	
	public static final Integer MAX_NOTICE_THREAD = 24;

	@Override
	public void asynchronizeNotice(final PaymentOrder pOrder) {
		Thread noticeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				ThreadHelper.setAppId(pOrder.getAppId());
				
				PayNoticeQueue pQue = new PayNoticeQueue(pOrder);
				if (!PayNoticeService.this.notice(pOrder)) {
					PayNoticeService.this.queue(pQue);
				}
				else {
					pQue.success();
				}
				payNQDao.saveOrUpdate(pQue);
			}
		});
		noticeThread.start();
	}
	
	@Override
	public boolean synchronizeNotice(final PaymentOrder pOrder) {
		PayNoticeQueue pQue = new PayNoticeQueue(pOrder);
		
		boolean ret = PayNoticeService.this.notice(pOrder);
		if (!ret) {
			PayNoticeService.this.queue(pQue);
		}
		else {
			pQue.success();
		}
		payNQDao.saveOrUpdate(pQue);
		return ret;
	}
	
	@Override
	public void loadUnnoticedQueue() {
		this.loadUnnoticedQueue(PAY_RENOTICE_APP_ALL);
	}
	
	@Override
	public void loadUnnoticedQueue(String appId) {
		logger.debug("[PayNotice Queue]Load PayNoticeQueue Data From DB.");
		List<AppConfig> apps = new ArrayList<AppConfig>();
		if (appId.equals(PAY_RENOTICE_APP_ALL)) {
			apps = appConfigDao.loadAll();
		}
		else {
			apps.add(appConfigDao.get(appId));
		}
		for (AppConfig app : apps) {
			ThreadHelper.setAppId(app.getAppId());
			
			logger.debug("[APP:" + app.getAppId() + " PayNotice Queue]FirstQueue is Empty. Load Data From DB.");
			List<PayNoticeQueue> list = payNQDao.getNoticesByType(FIRST_QUEUE);
			for (PayNoticeQueue q : list) {
				this.queue(q);
			}
			
			list = payNQDao.getNoticesByType(SECOND_QUEUE);
			for (PayNoticeQueue q : list) {
				this.queue(q);
			}
		}
		logger.debug("[PayNotice Queue]Load PayNoticeQueue Data From DB End.");
	}
	
	@Override
	public void doTimerTask(String type) {
		if (type.equalsIgnoreCase(FIRST_QUEUE)) {
			this.firstQueueNotice();
		}
		else if (type.equalsIgnoreCase(SECOND_QUEUE)) {
			this.secondQueueNotice();
		}
	}

	private void firstQueueNotice() {
		logger.debug("[PayNotice Queue]FirstQueueNotice Task Start."); 
		this.queueNotice(this.firstQueue);
		logger.debug("[PayNotice Queue]FirstQueueNotice Task End."); 
	}
	
	private void secondQueueNotice() {
		logger.debug("[PayNotice Queue]SecondQueueNotice Task Start.");
		this.queueNotice(this.secondQueue);
		logger.debug("[PayNotice Queue]SecondQueueNotice Task End.");
	}
	
	private void queueNotice(BlockingQueue<PayNoticeQueue> queue) {
		ExecutorService exeService = Executors.newFixedThreadPool(MAX_NOTICE_THREAD);
		
		List<PayNoticeQueue> failedQueue = new ArrayList<PayNoticeQueue>();
		
		while (!queue.isEmpty()) {
			PayNoticeQueue pQue;
			try {
				pQue = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			
			if (pQue != null) {
				//设置当前线程的appId，确保数据源逻辑正确
				ThreadHelper.setAppId(pQue.getAppId());
				
				final PayNoticeQueue finalPQue = pQue;
				
				Future future = exeService.submit(new Callable() {
					
					@Override
					public Object call() throws Exception {
						//设置当前线程的appId，确保数据源逻辑正确
						ThreadHelper.setAppId(finalPQue.getAppId());
						
						PaymentOrder pOrder = paymentOrderDao.get(finalPQue.getOrderId());
						if (!notice(pOrder)) {
							return Boolean.FALSE;
						}
						return Boolean.TRUE;
					}
				});
				
				Object obj;
				try {
					obj = future.get();
				} catch (Exception e) {
					obj = null;
					logger.error("", e);
				}
				
				Boolean ret = Boolean.FALSE;
				if (obj != null) {
					ret = (Boolean) obj;
				}
				if (ret) {
					pQue.success();
					payNQDao.update(pQue);
				}
				else {
					pQue.notice();
					payNQDao.update(pQue);
					failedQueue.add(pQue);
				}
			}
		}
		
		exeService.shutdown();
		
		for (PayNoticeQueue pq : failedQueue) {
			this.queue(pq);
		}
	}

	private void queue(PayNoticeQueue pQue) {
		if (pQue.getQueueType().equals(FIRST_QUEUE) && !this.firstQueue.contains(pQue)) {
			if (!firstQueue.add(pQue)) {
				logger.error("[PayNotice Queue]OrderId:" + pQue.getOrderId() + " add to one_hour_queue failed.");
			}
		}
		else if (pQue.getQueueType().equals(SECOND_QUEUE) && !this.secondQueue.contains(pQue)) {
			if (!secondQueue.add(pQue)) {
				logger.error("[PayNotice Queue]OrderId:" + pQue.getOrderId() + " add to one_hour_queue failed.");
			}
		}
	}

	private boolean notice(PaymentOrder pOrder) {
		String appId = pOrder.getAppId();
		AppConfig appConfig = appConfigDao.getByAppId(appId);
		
		if (appConfig == null) {
			return false;
		}
		
		SdkConfig sdkConfig = sdkConfigDao.getByPoId(pOrder.getAppId(), pOrder.getAppChannelId());
		
		String appNoticeUrl;
		if (sdkConfig != null && !StringUtils.isEmpty(sdkConfig.getNoticeUrl())) {
			appNoticeUrl = sdkConfig.getNoticeUrl();
		}
		else {
			appNoticeUrl = appConfig.getNoticeUrl();
		}
		
		if (StringUtils.isEmpty(appNoticeUrl)) {
			logger.error("[PayNotice Queue]OrderNotice[Fail]:" + pOrder.getOrderId() + " NoticeUrl is Empty.");
			return false;
		}
		
		PayNoticeData pNotice = new PayNoticeData();
		pNotice.setWithPOrder(pOrder);
		pNotice.ready();
		
		JsonReqObj req = pNotice.getNotice();
		
		String paymentSecret = appConfig.getPaymentSecret();
		
		String sign = EncryptUtil.generateSign(req.getData(), paymentSecret);
		req.setSign(sign);
		
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(DataKeys.JSON_DATA, ParseJson.encodeJson(req));
		
		String response = HttpUtil.postForm(appNoticeUrl, paramMap);
		if(null != response) {
			JsonResObj resObj = ParseJson.getJsonContentByStr(response, JsonResObj.class);
			if (resObj != null && resObj.isOk()) {
				logger.info("[PayNotice Queue]OrderNotice[OK]:" + pOrder.getOrderId()  + " paramMap:" + paramMap.toString() + " notice succeed. response=" + response);
				return true;
			}
		}
		logger.error("[PayNotice Queue]OrderNotice[Fail]:" + pOrder.getOrderId() + " params:" + paramMap.toString() + " notice error. response=" + response);
		return false;
	}
}
