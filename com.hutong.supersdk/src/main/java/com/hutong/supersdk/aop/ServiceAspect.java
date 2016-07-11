package com.hutong.supersdk.aop;

import com.hutong.supersdk.constants.ApiTarget;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.util.EncryptUtil;
import com.hutong.supersdk.common.util.StringUtil;
import com.hutong.supersdk.iservice.IPlatformService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.dao.GameOwnerDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.config.model.GameOwner;
import com.hutong.supersdk.service.PayNoticeService;
import com.hutong.supersdk.service.common.ThreadHelper;
import com.hutong.supersdk.service.modeltools.ConfigDesktopReq;


@Order(1)
@Aspect
public class ServiceAspect {

	private static final Log logger = LogFactory.getLog(ServiceAspect.class);

	@Autowired
	private AppConfigDao appConfigDao;

	@Autowired
	private GameOwnerDao gameOwnerDao;

	@Autowired
	private IPlatformService platformService;

	@Before(value = "execution(* com.hutong.supersdk.common.iservice.IUserService.*(..))"
			+ "|| execution(* com.hutong.supersdk.common.iservice.IPushService.*(..))"
			+ "|| execution(* com.hutong.supersdk.common.iservice.IPayService.*(..))")
	public void beforeClientUser(JoinPoint joinPoint) throws Throwable {
        checkRequestAndSetThread(joinPoint, ApiTarget.CLIENT);
    }

    @Before(value = "execution(* com.hutong.supersdk.iservice.app.IPayAppService.*(..)) " +
            "|| execution(* com.hutong.supersdk.iservice.app.IQueryAppService.*(..))")
	public void beforeAppPay(JoinPoint joinPoint) throws Throwable {
        this.checkRequestAndSetThread(joinPoint, ApiTarget.APP);
	}

    @SuppressWarnings("incomplete-switch")
	private void checkRequestAndSetThread(JoinPoint joinPoint, ApiTarget target)
            throws SuperSDKException {
        JsonReqObj jsonReq = null;

        Object jsonObj = joinPoint.getArgs()[0];
        if (jsonObj instanceof JsonReqObj)
            jsonReq = (JsonReqObj) jsonObj;

        if (jsonReq == null)
            throw new SuperSDKException(ErrorEnum.PARAM_IS_NULL);

        String appId = jsonReq.getAppId();
        AppConfig appConfig = appConfigDao.getByAppId(appId);
        if (appConfig == null)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);

        String key = "";
        switch (target) {
            case CLIENT:
                key = appConfig.getEncryptKey();
                break;
            case APP:
                key = appConfig.getAppSecret();
                break;
        }

        if (target != ApiTarget.DESKTOP_APP && !EncryptUtil.checkSign(jsonReq.getData(), key))
            throw new SuperSDKException(ErrorEnum.SIGN_ERROR);

        ThreadHelper.setAppId(appId);
    }

	@Before(value = "execution(* com.hutong.supersdk.iservice.server.IPayServerService.payBack(..))")
	public void beforePayback(JoinPoint joinPoint)
            throws Throwable {
		/**
		 * 因为回调地址为:supersdk-web/payback/shortAppId/appChannelId
		 * 所以解析joinPoint根据shortAppId查询appId 从而进行数据源的设置
		 */
		/**
		 * 回调地址的第一个参数为shortAppId
		 */
		String shortAppId = String.valueOf(joinPoint.getArgs()[0]);
		/**
		 * 查询AppConfig
		 */
		AppConfig appConfig = appConfigDao.getByShortId(shortAppId);

        /**
         * AppConfig判空处理
         */
        if (appConfig == null)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);

		String appId = appConfig.getAppId();
		ThreadHelper.setAppId(appId);
	}

	@Before(value = "execution(* com.hutong.supersdk.iservice.server.IPayServerService.renotice(..))")
	public void beforeRenotice(JoinPoint joinPoint)
            throws Throwable {
		String appId = String.valueOf(joinPoint.getArgs()[0]);

		if (!StringUtil.equalsStringIgnoreCase(appId, PayNoticeService.PAY_RENOTICE_APP_ALL)) {
			AppConfig appConfig = appConfigDao.get(appId);
			if (appConfig == null) {
				throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);
			}
			ThreadHelper.setAppId(appId);
		}
	}

	@SuppressWarnings("static-access")
	@Before(value = "execution(* com.hutong.supersdk.iservice.server.IManageService.*(..))")
	public void beforeManage(JoinPoint joinPoint)
            throws Throwable {
		Object[] args = joinPoint.getArgs();
		if (0 < args.length && null != args[0]) {
			ThreadHelper threadHelper = new ThreadHelper();
			threadHelper.setAppId(String.valueOf(args[0]));
		}
		else
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);
	}

    @Before(value = "execution(* com.hutong.supersdk.iservice.desktop.IAppDesktopService.*(..))")
    public void beforeDesktopApp(JoinPoint joinPoint)
            throws Throwable {
        JsonReqObj jsonReq = null;
        Object jsonObj = joinPoint.getArgs()[0];
        if (jsonObj instanceof JsonReqObj) {
            jsonReq = (JsonReqObj) jsonObj;
        }
        // 请求对象转换
        ConfigDesktopReq req = new ConfigDesktopReq(jsonReq);

        beforeApp(req);
    }

    /**
	 * Desktop接口token验证AOP处理
	 * 
	 * @param joinPoint 切面类
	 * @throws Throwable
	 */
	@Before(value = "execution(* com.hutong.supersdk.iservice.desktop.IConfigDesktopService.*(..))")
	public void beforeDesktopConfig(JoinPoint joinPoint)
            throws Throwable {

        JsonReqObj jsonReq = null;
        Object jsonObj = joinPoint.getArgs()[0];
        if (jsonObj instanceof JsonReqObj) {
            jsonReq = (JsonReqObj) jsonObj;
        }
        // 请求对象转换
        ConfigDesktopReq req = new ConfigDesktopReq(jsonReq);

        GameOwner gameOwner = beforeApp(req);

        if (!gameOwner.existsAppId(req.getAppId()))
            throw new SuperSDKException(ErrorEnum.INSUFFICIENT_PERMISSIONS_IN_APP, "没有足够的权限操作APP");

        // AppId检查
        String appId = req.getAppId();
        //appConfig的数据源信息为default,设置默认数据源
        AppConfig appConfig = appConfigDao.findById(appId);

        if (appConfig == null)
            throw new SuperSDKException(ErrorEnum.APP_ID_ERROR);

        // 设置数据源
        if(!StringUtils.isEmpty(appConfig.getDbId()) && DataKeys.Desktop.DEFAULT_DB_ID.equalsIgnoreCase(appConfig.getDbId()))
            ThreadHelper.setAppId(null);
        else
            ThreadHelper.setAppId(appId);
	}

    private GameOwner beforeApp(ConfigDesktopReq req) throws SuperSDKException {
        /**
         * 调用Platform验证用户token
         */
        // 参数检查
        if (null == req.getUserId() || null == req.getToken()) {
            throw new SuperSDKException(ErrorEnum.CHECK_TOKEN_FAIL, "token检查失败,参数不全");
        }
        // 调用platformService验证接口
        boolean checkToken = platformService.checkToken(req.getUserId(), req.getToken());

        if (!checkToken) {
            logger.error("ServiceAspect Platform check token Error. Platform效验失败");
            throw new SuperSDKException(ErrorEnum.CHECK_TOKEN_FAIL, "token检查失败,Platform效验失败");
        }

        // platform验证成功,判断有无SuperSDK用户
        ThreadHelper.setAppId(null);
        GameOwner gameOwner = gameOwnerDao.findById(req.getUserId());
        // 如果为null,创建SuperSDK对应platform用户
        if (null == gameOwner) {
            gameOwner = new GameOwner();
            gameOwner.setPlatformUserId(req.getUserId());
            gameOwnerDao.saveOrUpdate(gameOwner);
        }

        return gameOwner;
    }
}
