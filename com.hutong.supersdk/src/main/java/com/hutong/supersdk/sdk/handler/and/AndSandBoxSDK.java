package com.hutong.supersdk.sdk.handler.and;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IPostOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.PostOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.sandbox.SandBoxInfo;

@Component("andSandBoxSDK")
public class AndSandBoxSDK implements IVerifyUserSDK, IPostOrderSDK, ICheckOrderSDK {

	private static final Log logger = LogFactory.getLog(AndSandBoxSDK.class);

	private static final String SDK_ID = "AndSandBox";

	private static final String SANDBOX_TYPE = "sandbox_type";

	@Override
	public String getSDKId() {
		return SDK_ID;
	}

	@Override
	public Class<?> getConfigClazz() {
		return SandBoxInfo.class;
	}

	@Override
	public SDKCheckOrderRet checkOrder(PaymentOrder pOrder, JsonReqObj jsonData, Object config) {
		logger.debug(this.getSDKId() + " checkOrder Start.");
		SDKCheckOrderRet ret = new SDKCheckOrderRet();
		try {
			// 获取切换标识,当为true时正常返回,其他时返回失败
			String switchFlag = jsonData.getExtraKey(SANDBOX_TYPE);
			if (checkSwitch(switchFlag)) {
				return ret.success(StringUtils.isEmpty(pOrder.getSdkOrderId()) ? "sandBoxOrderId" : pOrder.getSdkOrderId(), 
						1, "RMB", "", ParseJson.encodeJson(jsonData));
			}
			logger.debug(this.getSDKId() + " postOrder Error, Request:" + ParseJson.encodeJson(jsonData));
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			return ret.fail();
		}
	}

	@Override
	public PostOrderRet postOrder(PaymentOrder order, Object config, JsonReqObj jsonReq) {
		logger.debug(this.getSDKId() + " postOrder Start.");
		PostOrderRet ret = new PostOrderRet();
		try {
			// 获取切换标识,当为true时正常返回,其他时返回失败
			String switchFlag = jsonReq.getExtraKey(SANDBOX_TYPE);
			if (checkSwitch(switchFlag)) {
				ret.setExtra("sand_box_order_id", "123456789");
				return ret.ok();
			}
			logger.debug(this.getSDKId() + " postOrder Error, Request:" + ParseJson.encodeJson(jsonReq));
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			return ret.fail();
		}
	}

	@Override
	public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
		logger.debug(this.getSDKId() + " verifyUser Start.");
		SDKVerifyRet ret = new SDKVerifyRet();
		try {
			// 获取切换标识,当为true时正常返回,其他时返回失败
			String switchFlag = input.getExtraKey(SANDBOX_TYPE);
			if (checkSwitch(switchFlag)) {
				ret.setSdkUid(StringUtils.isEmpty(input.getSdkUid()) ? "sandBoxUid" : input.getSdkUid());
				ret.setSdkAccessToken(StringUtils.isEmpty(input.getSDKAccessToken()) ? "sandBoxAccessToken"
						: input.getSDKAccessToken());
				return ret.success();
			}
			logger.debug(this.getSDKId() + " verifyUser Error, Request:" + ParseJson.encodeJson(input));
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error.");
			return ret.fail();
		} catch (Exception e) {
			logger.error("", e);
			ret.setErrorMsg(this.getSDKId() + " verifyUser Error.");
			return ret.fail();
		}
	}

	private boolean checkSwitch(String switchFlag) {
		if (StringUtils.isEmpty(switchFlag))
			return false;
		if ("true".equals(switchFlag))
			return true;
		return false;
	}
}
