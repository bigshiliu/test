package com.hutong.supersdk.service;

import java.util.List;
import java.util.Random;

import com.hutong.supersdk.common.exception.SuperSDKException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.iservice.desktop.IAppDesktopService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.dao.GameOwnerDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.config.model.GameOwner;
import com.hutong.supersdk.service.modeltools.AppDesktopReq;
import com.hutong.supersdk.service.modeltools.AppDesktopRes;

/**
 * AppDesktopService<br/>
 * Created by Dongxu on 2015/12/4.
 */
@Service
public class AppDesktopService implements IAppDesktopService {


	private static int SIGN_LENGTH = 20;

    private static String SPILT = ";";

	@Autowired
	private AppConfigDao appConfigDao;

	@Autowired
	private GameOwnerDao gameOwnerDao;
	
	@Override
	public JsonResObj queryApps(JsonReqObj reqObj)
            throws Exception {
		// 请求对象转换
		AppDesktopReq req = new AppDesktopReq(reqObj);
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Platform.PLATFORM_USER_ID };
        if (!reqObj.checkParmas(checkList)) {
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");
        }

        // 返回对象
        AppDesktopRes res = new AppDesktopRes();
        GameOwner gameOwner = gameOwnerDao.findById(req.getUserId());
        // 如果为null,添加superSDK对应的platform用户
        if (null == gameOwner)
            throw new SuperSDKException(ErrorEnum.SERVER_ERROR, "SuperSDK系统内部异常,用户查询失败");

        if(StringUtils.isEmpty(gameOwner.getAppIdArray().trim())){
            res.setAppIdArray("");
        }else{
            String[] appIds = gameOwner.getAppIdArray().split(SPILT);
            //根据appIdArray,查询app信息,拼接返回信息
            List<AppConfig> appConfigList = appConfigDao.queryList(appIds);
            StringBuilder sb = new StringBuilder();
            // 组装返回对象
            for(AppConfig appConfigTemp: appConfigList){
                sb.append(appConfigTemp.getAppId())
                        .append(":")
                        .append(appConfigTemp.getAppName())
                        .append(";");
            }
            res.setAppIdArray(sb.substring(0, sb.length() - 1));
        }
        return res.ok().getRes();
    }
	
	@Override
	@Transactional(rollbackFor = Exception.class, value = "configTx")
	public JsonResObj createApp(JsonReqObj reqObj)
            throws Exception {
		// 请求对象转换
		AppDesktopReq req = new AppDesktopReq(reqObj);
		// 返回对象
		AppDesktopRes res = new AppDesktopRes();
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID, DataKeys.Desktop.APP_NAME,
                DataKeys.Desktop.NOTICE_URL };
        if (!reqObj.checkParmas(checkList)) {
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");
        }

        AppConfig appConfig = appConfigDao.findById(req.getAppId());
        if (null != appConfig) {
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR, "AppId已经存在.");
        }
        // 生成ShortId,将appId(完整包名)进行截取
        String shortId = req.getAppId().substring(req.getAppId().lastIndexOf(".") + 1, req.getAppId().length());
        // 用while循环判断shortId是否重复
        AppConfig checkShortId = appConfigDao.getByShortId(shortId);
        Random r = new Random();
        while(null != checkShortId){
            // 如果shortId已经存在,则在尾部追加一个100以内的随机整数
            shortId = shortId + "" + r.nextInt(100);
            checkShortId = appConfigDao.getByShortId(shortId);
        }
        appConfig = new AppConfig();
        appConfig.setAppId(req.getAppId());
        appConfig.setAppName(req.getAppName());
        appConfig.setAppSecret(StringUtil.randomString(SIGN_LENGTH));
        appConfig.setPrivateKey(StringUtil.randomString(SIGN_LENGTH));
        appConfig.setEncryptKey(StringUtil.randomString(SIGN_LENGTH));
        appConfig.setPaymentSecret(StringUtil.randomString(SIGN_LENGTH));
        appConfig.setShortAppId(shortId);
        appConfig.setNoticeUrl(req.getNoticeUrl());
        appConfigDao.save(appConfig);

        // 给用户添加默认权限
        GameOwner gameOwner =  gameOwnerDao.findById(req.getUserId());
        // 如果该用户已经存在,查询原始app列表,增加新app权限
        if(gameOwner == null){
            throw new SuperSDKException(ErrorEnum.SERVER_ERROR, "SuperSDK系统内部异常,用户查询失败");
        }
        // 添加新app权限
        gameOwner.addAppId(req.getAppId());
        gameOwnerDao.saveOrUpdate(gameOwner);
        // 组装返回对象
        res.setAppId(appConfig.getAppId());
        res.setAppName(appConfig.getAppName());
        res.setAppSecret(appConfig.getAppSecret());
        res.setPrivateKey(appConfig.getPrivateKey());
        res.setEncryptKey(appConfig.getEncryptKey());
        res.setPaymentSecret(appConfig.getPaymentSecret());
        res.setShortId(appConfig.getShortAppId());
        res.setNoticeUrl(appConfig.getNoticeUrl());

        return res.ok().getRes();
	}
	
	@Override
	public JsonResObj queryApp(JsonReqObj reqObj)
            throws Exception {
		// 请求对象转换
		AppDesktopReq req = new AppDesktopReq(reqObj);
		// 返回对象
		AppDesktopRes res = new AppDesktopRes();
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID };
        if (!reqObj.checkParmas(checkList))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");

        AppConfig appConfig = appConfigDao.findById(req.getAppId());
        if (null == appConfig)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR, "服务器异常,AppId不存在.");
        // 组装返回对象
        res.setAppId(appConfig.getAppId());
        res.setAppName(appConfig.getAppName());
        res.setAppSecret(appConfig.getAppSecret());
        res.setPrivateKey(appConfig.getPrivateKey());
        res.setEncryptKey(appConfig.getEncryptKey());
        res.setPaymentSecret(appConfig.getPaymentSecret());
        res.setShortId(appConfig.getShortAppId());
        res.setNoticeUrl(appConfig.getNoticeUrl());
        return res.ok().getRes();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, value = "configTx")
	public JsonResObj refreshServerKey(JsonReqObj reqObj)
            throws Exception {
		// 请求对象转换
		AppDesktopReq req = new AppDesktopReq(reqObj);
		// 返回对象
		AppDesktopRes res = new AppDesktopRes();
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID };
        if (!reqObj.checkParmas(checkList))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");

        AppConfig appConfig = appConfigDao.findById(req.getAppId());
        if (null == appConfig)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR, "AppId错误");

        appConfig.setEncryptKey(StringUtil.randomString(SIGN_LENGTH));
        appConfig.setPaymentSecret(StringUtil.randomString(SIGN_LENGTH));
        appConfigDao.update(appConfig);
        // 组装返回对象
        res.setAppId(appConfig.getAppId());
        res.setEncryptKey(appConfig.getEncryptKey());
        res.setPaymentSecret(appConfig.getPaymentSecret());
        return res.ok().getRes();
	}

	@Override
	@Transactional(rollbackFor = Exception.class, value = "configTx")
	public JsonResObj updateAppProfile(JsonReqObj reqObj) throws Exception {
		// 请求对象转换
		AppDesktopReq req = new AppDesktopReq(reqObj);
		// 返回对象
		AppDesktopRes res = new AppDesktopRes();
        // 必填参数检查
        String[] checkList = new String[] { DataKeys.Desktop.APP_ID, DataKeys.Desktop.APP_NAME,
                DataKeys.Desktop.NOTICE_URL };
        if (!reqObj.checkParmas(checkList))
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL, "参数不全,请检查参数");

        AppConfig appConfig = appConfigDao.findById(req.getAppId());
        if (null == appConfig)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR, "AppId错误");

        appConfig.setAppName(req.getAppName());
        appConfig.setNoticeUrl(req.getNoticeUrl());
        appConfigDao.update(appConfig);
        // 组装返回对象
        res.setAppId(appConfig.getAppId());
        res.setAppName(appConfig.getAppName());
        res.setNoticeUrl(appConfig.getNoticeUrl());
        return res.ok().getRes();
	}
}
