package com.hutong.supersdk.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.iservice.server.IManageService;
import com.hutong.supersdk.mysql.config.dao.AppConfigDao;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.dao.SDKConfigDao;
import com.hutong.supersdk.mysql.inst.dao.SDKClientConfigDao;
import com.hutong.supersdk.mysql.inst.model.SdkClientConfig;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;

@Service
public class ManageService implements IManageService {
	
	private final Log logger = LogFactory.getLog(ManageService.class);
	
	//manage系统,系统管理员身份标示
	private final String SYSTEM_MANAGER_FOR_MANAGE = "systemManager";
	
	@Autowired
	private AppConfigDao appConfigDao;
	
	@Autowired
	private SDKConfigDao sdkConfigDao;
	
	@Autowired
	private SDKClientConfigDao sdkClientConfigDao;
	
	@Override
	public List<AppConfig> finAllAppConfigByShortId(String appShortIdList) throws SuperSDKException {
		List<AppConfig> allAppConfigList = new ArrayList<AppConfig>();
		//如果是系统管理员,则加载所有app信息,否则按照appIdList加载
		if(!StringUtils.isEmpty(appShortIdList) && SYSTEM_MANAGER_FOR_MANAGE.equals(appShortIdList))
			allAppConfigList = appConfigDao.loadAll();
		else
			allAppConfigList = appConfigDao.loadByShortId(appShortIdList);
		return allAppConfigList;
	}

	@Override
	public AppConfig findAppByAppId(String appId) throws SuperSDKException {
		AppConfig appConfig = appConfigDao.findById(appId);
		return appConfig;
	}
	
	@Override
	public List<SdkConfig> findSDKConfigByAppId(String appId) throws SuperSDKException {
		List<SdkConfig> sdkConfigList = sdkConfigDao.loadAll();
		return sdkConfigList;
	}

	@Override
	public List<SdkClientConfig> findSDKClientConfigByAppId(String appId) throws SuperSDKException {
		List<SdkClientConfig> sdkClientConfigList = sdkClientConfigDao.loadAll();
		return sdkClientConfigList;
	}

	@Override
	public boolean updateAppConfig(AppConfig appConfig) throws SuperSDKException {
		try {
			appConfigDao.update(appConfig);
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean setAppEnableFlag(String appId, boolean status) throws SuperSDKException {
		boolean flag = false;
		try {
			flag = appConfigDao.setAppConfigEnableFlag(appId, status);
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}

	@Override
	public boolean saveAppConfig(AppConfig appConfig) {
		boolean flag = false;
		try {
			appConfigDao.save(appConfig);
			flag = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}

	@Override
	public boolean setAppSDKConfigEnableFlag(String appId, String appChannelId, boolean status) throws SuperSDKException {
		boolean flag = false;
		try {
			flag = sdkConfigDao.setAppSDKConfigEnableFlag(appChannelId, status);
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}

	@Override
	public boolean setAppSDKClientConfigEnableFlag(String appId, String appChannelId, boolean status) throws SuperSDKException {
		boolean flag = false;
		try {
			flag = sdkClientConfigDao.setAppSDKClientConfigEnableFlag(appChannelId, status);
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}

	@Override
	public SdkClientConfig findSDKClientConfigByChannelId(String appId, String appChannelId) throws SuperSDKException {
		SdkClientConfig sdkClientConfig = sdkClientConfigDao.getByPoId(appId, appChannelId);
		return sdkClientConfig;
	}

	@Override
	public SdkConfig findSDKConfigByChannelId(String appId, String appChannelId) throws SuperSDKException {
		SdkConfig sdkConfig = sdkConfigDao.findById(appChannelId);
		return sdkConfig;
	}

	@Override
	public boolean updateSdkClientConfig(String appId, SdkClientConfig sdkClientConfig) throws SuperSDKException {
		try {
			sdkClientConfigDao.update(sdkClientConfig);
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean updateSdkConfig(String appId, SdkConfig sdkConfig) throws SuperSDKException {
		try {
			sdkConfigDao.update(sdkConfig);
		} catch (Exception e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

	@Override
	public boolean saveSDKClientConfig(String appId, SdkClientConfig sdkClientConfig) {
		boolean flag = false;
		try {
			sdkClientConfig.setCreateTime(new Timestamp(System.currentTimeMillis()));
			sdkClientConfig.setEnableFlag(1);
			sdkClientConfigDao.save(sdkClientConfig);
			flag = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}

	@Override
	public boolean saveSDKConfig(String appId, SdkConfig sdkConfig) {
		boolean flag = false;
		try {
			sdkConfig.setCreateTime(new Timestamp(System.currentTimeMillis()));
			sdkConfig.setEnableFlag(1);
			sdkConfigDao.save(sdkConfig);
			flag = true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}
	
}
