package com.hutong.supersdk.sdk.handler.ios;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.config.dao.SuperSDKUserDao;
import com.hutong.supersdk.mysql.config.model.SuperSDKUser;
import com.hutong.supersdk.mysql.inst.dao.PaymentOrderDao;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;
import com.hutong.supersdk.sdk.ICheckOrderSDK;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.handler.and.AndQiHu360SDK;
import com.hutong.supersdk.sdk.modeltools.SDKCheckOrderRet;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.sdk.modeltools.apple.IOSAppleSDKConfig;
import com.hutong.supersdk.sdk.modeltools.qihu360.QiHu360IOSSDKInfo;
import com.hutong.supersdk.sdk.modeltools.qihu360.QiHu360SDKInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("iOSQiHu360SDK")
public class IOSQiHu360SDK implements IVerifyUserSDK, ICheckOrderSDK {

    /**
     * 目前360越狱渠道登陆验证同安卓,支付则使用AppStore支付
     */

    private static final Log logger = LogFactory.getLog(IOSQiHu360SDK.class);

    private static final String SDK_ID = "IOSQiHu360";

    private static final String QUICK_LOGIN = "quick_login";
    private static final String BIND_USER = "bind_user";
    private static final String DEVICE_ID = "device_id";
    private static final String OPERATE = "operate";

    @Autowired
    private SuperSDKUserDao superSDKUserDao;

    @Autowired
    private PaymentOrderDao paymentOrderDao;

    @Override
    public String getSDKId() {
        return SDK_ID;
    }

    @Override
    public Class<?> getConfigClazz() {
        return QiHu360IOSSDKInfo.class;
    }

    @Override
    public SDKCheckOrderRet checkOrder(PaymentOrder pOrder, JsonReqObj jsonData, Object config) {
        QiHu360IOSSDKInfo configInfo = (QiHu360IOSSDKInfo) config;
        IOSAppleSDKConfig appleSDKConfig = new IOSAppleSDKConfig();
        BeanUtils.copyProperties(configInfo, appleSDKConfig);
        return IOSAppleSDK.checkOrder(paymentOrderDao, pOrder, jsonData, appleSDKConfig);
    }

    @Override
    public SDKVerifyRet verifyUser(JsonReqObj input, Object config) {
        SDKVerifyRet ret = new SDKVerifyRet();
        try {
            String operate = input.getExtraKey(OPERATE);
            if (QUICK_LOGIN.equals(operate)) {
                return quickLogin(input);
            } else if (BIND_USER.equals(operate)) {
                ret = get360Verify(input, (QiHu360IOSSDKInfo) config);
                //TODO 先验证拿到SDKUid,再绑定对应的SuperSDKUid
                if (ret.isSuccess()) {
                    if (!StringUtils.isEmpty(ret.getSdkUid()))
                        return bindUser(input, ret);
                }
                logger.error(getSDKId() + " Bind User Error, SDKUid is Empty.");
                return ret.fail();
            } else {
                return get360Verify(input, (QiHu360IOSSDKInfo) config);
            }
        } catch (Exception e) {
            logger.error("", e);
            return ret.fail();
        }
    }

    private SDKVerifyRet get360Verify(JsonReqObj input, QiHu360IOSSDKInfo config) {
        //登陆延签操作
        QiHu360SDKInfo qiHu360SDKInfo = new QiHu360SDKInfo();
        BeanUtils.copyProperties(config, qiHu360SDKInfo);
        return AndQiHu360SDK.verifyQiHuUser(input, qiHu360SDKInfo);
    }

    private SDKVerifyRet bindUser(JsonReqObj input, SDKVerifyRet ret) {
        /**
         * 绑定操作
         * 必要参数 deviceId appId SuperSDKUid
         * 将SuperSDKUSER表中,SuperSDKUid对应SDKUid(此时值应该为DeviceId)替换为真实的DeviceId
         * 并将DeviceId保存到Extra中
         */
        String device_id = input.getExtraKey(DEVICE_ID);
        if (StringUtils.isEmpty(device_id) || StringUtils.isEmpty(input.getUid()) || StringUtils.isEmpty(ret.getSdkUid())) {
            return ret.fail();
        }
        Map<String, String> extraMap = new HashMap<String, String>();
        extraMap.put("device_id", device_id);
        String extraStr = ParseJson.encodeJson(extraMap);
        SuperSDKUser superSDKUser = superSDKUserDao.getBySdkUserInfo(getSDKId(), device_id);
        if (null == superSDKUser) {
            ret.setUserExtra(extraStr);
            return ret.success();
        }
        superSDKUser.setSdkUid(ret.getSdkUid());
        superSDKUser.setExtra(extraStr);
        superSDKUserDao.saveOrUpdateOnFlush(superSDKUser);
        ret.setSdkUid(superSDKUser.getSdkUid());
        ret.setSdkAccessToken("bindUserAccessToken");
        return ret.success();
    }

    private SDKVerifyRet quickLogin(JsonReqObj input) {
        SDKVerifyRet ret = new SDKVerifyRet();
        /**
         * 快速登陆操作
         * 必要参数 deviceId appId
         * 查询deviceId是否存在对应SuperSDKUid
         * 1.如果存在,返回成功
         * 2.如果不存在,创建对应SuperSDKUid App数据,返回成功
         */
        String device_id = input.getExtraKey(DEVICE_ID);
        if (StringUtils.isEmpty(device_id))
            return ret.fail();
        ret.setSdkUid(device_id);
        ret.setSdkAccessToken("deviceAccessToken");
        return ret.success();
    }
}
