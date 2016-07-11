package com.hutong.supersdk.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.app.IQueryAppService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.dao.SDKClientConfigDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.mysql.inst.model.SdkClientConfig;
import com.hutong.supersdk.util.PropertiesUtil;
import com.hutong.supersdk.util.SDKConfigUtil;

@Service
public class QueryService implements IQueryAppService {
	
	@Autowired
	private SDKClientConfigDao sdkClientConfigDao;
	
	@Autowired
	private PaymentOrderDao paymentOrderDao;
	
	@Autowired
	private AppConfigDao appConfigDao;

	@Override
	public JsonResObj queryConfig(JsonReqObj jsonData) throws SuperSDKException {
		String channelId = jsonData.getDataKey(DataKeys.Common.CHANNEL_ID);
		
		Map<String, String> config;
		
		SdkClientConfig sdkClientConfig = sdkClientConfigDao.getByPoId(jsonData.getAppId(), channelId);
		if (sdkClientConfig != null) {
			config = SDKConfigUtil.configInfoToMap(sdkClientConfig.getConfigInfo());
		}
		else {
			throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
		}
	
		JsonResObj ret = new JsonResObj();
		ret.setData(config);
		
		ret.ok();
		return ret;
	}

	@Override
	public JsonResObj queryOrder(JsonReqObj jsonData) throws SuperSDKException {
		String superSDKOrderId = jsonData.getDataKey(DataKeys.Common.SUPERSDK_ORDER_ID);
		
		
		PaymentOrder paymentOrder =  paymentOrderDao.findById(superSDKOrderId);
		if(null == paymentOrder){
			throw new SuperSDKException(ErrorEnum.ORDER_ID_NOT_FOUND);
		}
		JsonResObj ret = new JsonResObj();
		ret.getData().put("orderId", paymentOrder.getOrderId());
		ret.getData().put("payStatus", paymentOrder.getPayStatus());
		ret.ok();
		return ret;
	}

	@Override
	public JsonResObj queryCallBackUrl(JsonReqObj jsonData) throws SuperSDKException {
		if(StringUtils.isEmpty(jsonData.getAppId()))
			throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);
		if(StringUtils.isEmpty(jsonData.getAppChannelId()))
			throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);
		AppConfig appConfig = appConfigDao.findById(jsonData.getAppId());
		if(null == appConfig)
			throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);
		String superSDKUrl = PropertiesUtil.findValueByKey("superSDKConfig", "superSDK_url");
		JsonResObj ret = new JsonResObj();
		ret.getData().put(DataKeys.Common.CALL_BACK_URL, superSDKUrl + "supersdk-web/payback/" + appConfig.getShortAppId() + "/" + jsonData.getAppChannelId());
		ret.ok();
		return ret;
	}

}
