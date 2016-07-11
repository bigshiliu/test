package com.hutong.supersdk.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.IPayService;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.iservice.app.IPayAppService;
import com.hutong.supersdk.iservice.notice.ICheckOrderService;
import com.hutong.supersdk.iservice.notice.IPayNoticeService;
import com.hutong.supersdk.iservice.server.IPayServerService;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPayCallBackSDK;
import com.hutong.supersdk.sdk.IPaySuccessHandler;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.service.common.ServiceCommon;
import com.hutong.supersdk.service.modeltools.CheckOrderReq;
import com.hutong.supersdk.service.modeltools.PaymentReq;
import com.hutong.supersdk.service.modeltools.PaymentRes;
import com.hutong.supersdk.service.modeltools.QueryOrdersReq;
import com.hutong.supersdk.service.modeltools.QueryOrdersRes;
import com.hutong.supersdk.util.SDKConfigUtil;

@Service
public class PayService implements IPayServerService, IPayAppService, IPayService {

	private static final int MAX_ORDER_QUERY_NUM = 50;

	private static final Log logger = LogFactory.getLog(PayService.class);

	@Autowired
	private PaymentOrderDao paymentOrderDao;

	@Autowired
	private SDKConfigDao sdkConfigDao;

	@Autowired
	private AppConfigDao appConfigDao;

	@Autowired
	private ICheckOrderService checkOrderService;

	@Autowired
	private IPaySuccessHandler paySuccessHandler;

	@Autowired
	private IPayNoticeService payNoticeService;

	@Override
	@Transactional(rollbackFor = Exception.class, value = "instTx")
	public JsonResObj createOrder(JsonReqObj jsonData) throws SuperSDKException {
		PaymentReq req = new PaymentReq(jsonData);
		if (!req.checkData()) {
			logger.error("CreateOrder Parameter Not Incomplete. param = " + ParseJson.encodeJson(jsonData));
			throw new SuperSDKException(ErrorEnum.PAY_PARAMETER_INCOMPLETE);
		}

		PaymentOrder pOrder = new PaymentOrder();
		req.setPaymentOrder(pOrder);

		SdkConfig sdkConfig = sdkConfigDao.getByPoId(pOrder.getAppId(), pOrder.getAppChannelId());
		if (sdkConfig == null) {
			logger.error("AppChannelId Error. appChannelId = " + pOrder.getAppChannelId());
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}
		paymentOrderDao.save(pOrder);

		PaymentRes res = new PaymentRes();

		IPostOrderSDK sdk = ServiceCommon.getServiceByConfigPlatform(sdkConfig, IPostOrderSDK.class);
		// 如果该SDK需要预先向服务器提交订单，则执行提交订单操作
		if (sdk != null) {
			Object config = SDKConfigUtil.json2Config(sdkConfig.getConfigInfo(), sdk.getConfigClazz());

			PostOrderRet postRet = sdk.postOrder(pOrder, config, jsonData);

			// 在返回值中，将提交订单的返回加入到extra参数，返回给客户端
			if (postRet != null && postRet.isOk())
				res.setExtra(postRet.getExtra());
			else
				throw new SuperSDKException(ErrorEnum.POST_ORDER_FAILED);
			
			//兼容旧版本
			res.getRes().getData().put(DataKeys.Common.SDK_ID, sdk.getSDKId());
		}
		//兼容旧版本
		else {			
			IPayCallBackSDK tmpSDK = ServiceCommon.getServiceByConfigPlatform(sdkConfig, IPayCallBackSDK.class);
			if (tmpSDK != null)
				res.getRes().getData().put(DataKeys.Common.SDK_ID, tmpSDK.getSDKId());
			else
				res.getRes().getData().put(DataKeys.Common.SDK_ID, "unknown");
		}
		res.setOrder(pOrder);

		// 将数据库配置的extra参数加载到返回值当中，返回给客户端
		if (sdkConfig.getExtra() != null && !sdkConfig.getExtra().isEmpty()) {
			Map<String, String> extra = SDKConfigUtil.configInfoToMap(sdkConfig.getExtra());
			if (extra != null && !extra.isEmpty()) {
				res.putAllExtra(extra);
			}
		}
		res.ok();

		return res.getRes();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, value = "instTx")
	public Object payBack(String shortAppId, String appChannelId, Map<String, String> paramMap,
			InputStream servletInputStream, String method) throws SuperSDKException {
		AppConfig appConfig = appConfigDao.getByShortId(shortAppId);

		SdkConfig sdkConfig = sdkConfigDao.getByPoId(appConfig.getAppId(), appChannelId);

		if (sdkConfig == null) {
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}

		IPayCallBackSDK sdk = ServiceCommon.getServiceByConfigPlatform(sdkConfig, IPayCallBackSDK.class);
		if (sdk == null) {
			throw new SuperSDKException(ErrorEnum.SERVICE_NOT_FOUND);
		}

		Object config = SDKConfigUtil.json2Config(sdkConfig.getConfigInfo(), sdk.getConfigClazz());

		return sdk.payCallBack(paySuccessHandler, paramMap, servletInputStream, method, config);
	}

	@Override
	@Transactional(rollbackFor = Exception.class, value = "instTx")
	public JsonResObj checkOrder(JsonReqObj jsonData) throws SuperSDKException {
		CheckOrderReq req = new CheckOrderReq(jsonData);

		// 检查channel id
		if (null == req.getAppChannelId()) {
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}

		// 根据channelID获取SDK配置信息
		SdkConfig sdkConfig = sdkConfigDao.getByPoId(req.getAppId(), req.getAppChannelId());
		if (null == sdkConfig) {
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}
		// 检查订单是否存在
		String orderId = req.getOrderId();
		PaymentOrder pOrder = paymentOrderDao.get(orderId);
		if (pOrder == null) {
			throw new SuperSDKException(ErrorEnum.ORDER_ID_NOT_FOUND);
		}

		PaymentRes res = new PaymentRes();
		if (!pOrder.isPaid()) {
			// 如果有SDKOrderId,则保存
			String sdkOrderId = req.getSDKOrderId();
			if (!StringUtils.isEmpty(sdkOrderId)) {
				pOrder.setSdkOrderId(sdkOrderId);
			}
			SDKCheckOrderRet ret = null;
			// 根据SDK配置信息,找到对应的,继承了ICheckOrderSDK接口的实现类
			ICheckOrderSDK sdk = ServiceCommon.getServiceByConfigPlatform(sdkConfig, ICheckOrderSDK.class);
			if (sdk != null) {
				Object config = SDKConfigUtil.json2Config(sdkConfig.getConfigInfo(), sdk.getConfigClazz());
				ret = sdk.checkOrder(pOrder, jsonData, config);

				if (ret.isAddCheckQueue()) {
                    //保存需要加入检查队列的订单，防止数据丢失
					paymentOrderDao.update(pOrder);
					checkOrderService.queueToCheck(pOrder);
				}

                if (ret.isSuccess()) {
                    if (ret.getExtra() != null && !ret.getExtra().isEmpty())
                        res.setExtra(ret.getExtra());

                    if (!paySuccessHandler.succeedPayment(pOrder, ret.getSdkOrderId(), ret.getPayAmount(),
                            ret.getCurrencyType(), ret.getPayType(), ret.getSource())) {
                        logger.error("Succeed PaymentOrder(OrderId" + pOrder.getOrderId() + ", appId=" +
                                pOrder.getAppId() + ", channelId=" + pOrder.getAppChannelId()
                                + ") Failed.");
                        throw new SuperSDKException(ErrorEnum.ERROR, "Succeed PaymentOrder Failed.");
                    }
                }
            }
		}
		// 装配返回结果
		res.setOrder(pOrder);
		return res.ok().getRes();
	}

	@Override
	public JsonResObj renotice(String appId, String noticeScope) throws SuperSDKException {
		JsonResObj ret = new JsonResObj();

		// 判空处理
		if (StringUtils.isEmpty(noticeScope)) {
			throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);
		}

		// 加载遗漏的未通知订单至通知队列
		if (noticeScope.equals(PayNoticeService.PAY_RENOTICE_SCOPE_ALL)) {
			// 加载所有APP的遗漏订单到通知队列
			if (appId.equals(PayNoticeService.PAY_RENOTICE_APP_ALL)) {
				payNoticeService.loadUnnoticedQueue();
			}
			// 加载指定APP的遗漏订单到通知队列
			else {
				payNoticeService.loadUnnoticedQueue(appId);
			}
		}
		// 重新通知指定的订单
		else {
			// 按照分隔符,分割出order数组
			String[] orders = noticeScope.split(PayNoticeService.PAY_RENOTICE_ORDER_SPLITOR);
			List<PaymentOrder> paymentOrders = paymentOrderDao.queryList(orders);

			for (PaymentOrder pOrder : paymentOrders) {
				if (pOrder.isPaid()) {
					if (payNoticeService.synchronizeNotice(pOrder)) {
						ret.setKeyData(pOrder.getOrderId(), PayNoticeService.NOTICE_SUCCESS);
					} else {
						ret.setKeyData(pOrder.getOrderId(), PayNoticeService.FAILED_QUEUE);
					}
				} else {
					ret.setKeyData(pOrder.getOrderId(), pOrder.getPayStatus());
				}
			}
		}
		return ret.ok();
	}

	@Override
	public JsonResObj queryOrders(JsonReqObj reqObj) {
		// 返回参数
		QueryOrdersRes res = new QueryOrdersRes();
		// 转换对象方便操作
		QueryOrdersReq req = new QueryOrdersReq(reqObj);
		// 检查必填参数
		if (!req.checkData()) {
			//返回结果
            return res.setError(ErrorEnum.PARAM_IS_NULL, "必传参数不全!").getRes();
		}

		// 如果订单最大查询结果不为空并且不大于默认值,则修改查询结果数量
		int queryNum = MAX_ORDER_QUERY_NUM;
		if (!StringUtils.isEmpty(req.getOrderNum()) && queryNum > Integer.parseInt(req.getOrderNum())) {
			queryNum = Integer.parseInt(req.getOrderNum());
		}

		//转换开始时间
		String startTime = null;
		String endTime = null;
		//TODO 起止时间都有可能为空，此处逻辑有问题
		if(!StringUtils.isEmpty(req.getStartTime())){
			startTime = StringUtil.timeStamp2Date(req.getStartTime());
		//如果开始时间为空,则转换截止时间
		}else if(!StringUtils.isEmpty(req.getEndTime())){
			endTime = StringUtil.timeStamp2Date(req.getEndTime());
		}
		if (!StringUtils.isEmpty(req.getStartTime())) {
			startTime = StringUtil.timeStamp2Date(req.getStartTime());
		}
        if (!StringUtils.isEmpty(req.getEndTime())) {
            endTime = StringUtil.timeStamp2Date(req.getEndTime());
        }
		
		List<PaymentOrder> list = paymentOrderDao.queryOrdersByUserId(req.getSuperSDKUid(), queryNum, startTime, endTime);
		//如果list不为空,添加到返回结果中
		if(!list.isEmpty()){
			res.ok();
			res.setOrderList(list);
		}else{
			res.setError(ErrorEnum.SEARCH_RESULT_IS_NULL, "没有查询到该UserId下支付成功订单!");
		}
		return res.getRes();
	}
}
