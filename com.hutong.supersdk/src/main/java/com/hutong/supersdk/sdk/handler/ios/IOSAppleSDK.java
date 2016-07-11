package com.hutong.supersdk.sdk.handler.ios;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.HttpUtil;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.handler.and.AndWuYouSDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.apple.AppleResponse;
import com.hutong.supersdk.sdk.modeltools.apple.IOSAppleSDKConfig;

@Component("iOSAppleSDK")
public class IOSAppleSDK extends AndWuYouSDK implements ICheckOrderSDK {

	private static final String SDK_ID = "IOSAppStore";

	private final static Log logger = LogFactory.getLog(IOSAppleSDK.class);

	@Autowired
	private PaymentOrderDao paymentOrderDao;

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return IOSAppleSDKConfig.class;
	}

	@Override
	public SDKCheckOrderRet checkOrder(final PaymentOrder pOrder, JsonReqObj jsonData,
                                       Object config) {
        return checkOrder(paymentOrderDao, pOrder, jsonData, (IOSAppleSDKConfig) config);
	}

    /**
     * 检查Apple订单的静态方法
     * @param paymentOrderDao 订单的访问DAO
     * @param pOrder 订单信息
     * @param jsonData 请求数据
     * @param config 配置信息
     * @return SDKCheckOrderRet
     */
    protected static SDKCheckOrderRet checkOrder(PaymentOrderDao paymentOrderDao, PaymentOrder pOrder, JsonReqObj jsonData, IOSAppleSDKConfig config) {
        SDKCheckOrderRet ret = new SDKCheckOrderRet();
        try {
            String receipt = jsonData.getExtraKey("receipt");
            if (StringUtils.isEmpty(receipt)) {
                return ret.fail();
            }

            Map<String, String> data = new HashMap<String, String>();
            data.put("receipt-data", receipt);
            String response = postReceipt(data, config, jsonData);
            if (response != null) {
                AppleResponse appRes = ParseJson.getJsonContentByStr(response, AppleResponse.class);
                // success
                if (appRes != null && appRes.getStatus() == 0) {
                    //检查productId
                    String productId = appRes.getReceipt().getProduct_id();
                    if (!pOrder.getAppProductId().equals(productId)) {
                        logger.error("AppStore Order Check Error. Deisre ProductId = " + pOrder.getAppProductId()
                                + " & Response = " + response);
                        return ret.fail();
                    }

                    if (!StringUtils.isEmpty(config.getBundleIds())) {
                        //检查BundleId
                        boolean legal = false;
                        String bundleId = appRes.getReceipt().getBid();
                        String[] config_item_ids = config.getBundleIds().split(";");
                        for (String id : config_item_ids) {
                            if (bundleId.equals(id)) {
                                legal = true;
                                break;
                            }
                        }
                        if (!legal) {
                            logger.error("AppStore Order Check Error. Deisre AppItemIds = " + config.getBundleIds()
                                    + " & Response = " + response);
                            return ret.fail();
                        }
                    }

                    //检查该Apple订单是否已经成功了
                    String sdkOrderId = appRes.getReceipt().getTransaction_id();
                    String channelId = jsonData.getDataKey(DataKeys.Common.CHANNEL_ID);
                    if (paymentOrderDao.isExists(channelId, PaymentOrder.PAY_STATUS_SUCCESS, sdkOrderId)) {
                        logger.error("");
                        return ret.fail();
                    }

                    //苹果不会返回实际支付金额
                    double payAmount = pOrder.getOrderAmount();
                    return ret.success(sdkOrderId, payAmount, "RMB", "", response);
                }
            }
            logger.error("iosAppleSDK pay failed!! response:" + response);
        } catch (Exception e) {
            logger.error("", e);
        }
        return ret.fail();
    }

    private static String postReceipt(Map<String, String> data, IOSAppleSDKConfig sdkConfig, JsonReqObj jsonData) {
        String is_debug = jsonData.getExtraKey("is_debug");
        String url = "true".equals(is_debug) ? sdkConfig.getTestUrl() : sdkConfig.getProductUrl();
        return HttpUtil.postJson(url, ParseJson.encodeJson(data));
	}

}
