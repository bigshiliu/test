package com.hutong.supersdk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.SuperSDK;
import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.IUserService;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.iservice.server.IUserServerService;
import com.hutong.supersdk.mysql.config.dao.AppToSSDKUserDao;
import com.hutong.supersdk.mysql.config.dao.SuperSDKUserDao;
import com.hutong.supersdk.mysql.config.model.SuperSDKUser;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;
import com.hutong.supersdk.sdk.IVerifyUserSDK;
import com.hutong.supersdk.sdk.modeltools.SDKVerifyRet;
import com.hutong.supersdk.service.common.ServiceCommon;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.service.modeltools.LoginCheckReq;
import com.hutong.supersdk.service.modeltools.LoginCheckRes;
import com.hutong.supersdk.util.SDKConfigUtil;

@Service
public class UserService implements IUserService, IUserServerService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private SuperSDKUserDao userDao;

    @Autowired
    private SDKConfigDao sdkConfigDao;

    @Autowired
    private AppToSSDKUserDao app2SUserDao;

    /**
     * 用户认证,以post方式进行提交
     *
     * @param jsonData param
     * @return 输出格式为json
     * @throws SuperSDKException
     */
    @Override
    @Transactional(rollbackFor = Exception.class, value = "configTx")
    public JsonResObj loginCheck(JsonReqObj jsonData) throws SuperSDKException {
        logger.debug("UserService loginCheck start !!!");
        //获取SDK Config对象，取得jdObj.getData().get("app_channel_id")，即为该渠道登陆处理的BeanName
        LoginCheckReq req = new LoginCheckReq(jsonData);

        //检查channel id
        if (null == req.getAppChannelId()) {
            throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
        }

        SdkConfig sdkConfig = sdkConfigDao.getByPoId(req.getAppId(), req.getAppChannelId());

        if (sdkConfig == null) {
            throw new SuperSDKException(ErrorEnum.APP_CHANNEL_ID_NOT_FOUND);
        }

        IVerifyUserSDK sdk = ServiceCommon.getServiceByConfigPlatform(sdkConfig, IVerifyUserSDK.class);
        SuperSDK.getInstance().getService(sdkConfig.getHandleBean());
        Object config = SDKConfigUtil.json2Config(sdkConfig.getConfigInfo(), sdk.getConfigClazz());

        SDKVerifyRet ret = sdk.verifyUser(jsonData, config);

        if (ret.isSuccess()) {
            //设置SDK_ID为handleBean中SDK_ID
            String sdkId = sdk.getSDKId();
            String sdkUid = ret.getSdkUid();
            String userExtra = ret.getUserExtra();

            //检查SDK验证用户后返回的sdk_uid是否合法。
            if (StringUtils.isEmpty(sdkUid))
                throw new SuperSDKException(ErrorEnum.USER_CREATE_FAILED, "SDK_UID can not be empty.");

            //保存处理返回的UserExtra,如果User对象不存在,则创建
            SuperSDKUser user = userDao.getBySdkUserInfo(sdkId, sdkUid);
            if (user == null) {
                user = new SuperSDKUser(sdkId, sdkUid);
                user.setExtra(userExtra);
                if (!userDao.create(user)) {
                    throw new SuperSDKException(ErrorEnum.USER_CREATE_FAILED);
                }
            }else if(!StringUtils.isEmpty(userExtra)){
                //如果SDK处理类设置了User的Extra,则更新User
                user.setExtra(userExtra);
                userDao.saveOrUpdate(user);
            }

            //检查SuperSDKUidToApp信息
            if (null == app2SUserDao.getBySuperSDKUidAndAppId(user.getSupersdkUid(), ThreadHelper.getAppId())) {
                app2SUserDao.save(user.getSupersdkUid(), ThreadHelper.getAppId());
            }

            LoginCheckRes res = new LoginCheckRes();
            res.setAppId(ThreadHelper.getAppId());
            res.setSdkId(sdk.getSDKId());
            res.setAppChannelId(sdkConfig.getPoId().getAppChannelId());
            res.setSuperSDKUid(user.getSupersdkUid());
            res.setWithSDKVerifyRet(ret);

            if (jsonData.containsExtraKey(DataKeys.Payment.APP_DATA)) {
                res.getRes().getExtra().put(DataKeys.Payment.APP_DATA, jsonData.getExtraKey(DataKeys.Payment.APP_DATA));
            }

            res.ok();

            return res.getRes();
        } else {
            throw new SuperSDKException(ErrorEnum.USER_VERIFY_FAILED, ret.getErrorMsg());
        }
    }
}
