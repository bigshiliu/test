package com.hutong.supersdk.iservice.server;

import java.util.List;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.mysql.config.model.AppConfig;
import com.hutong.supersdk.mysql.inst.model.SdkClientConfig;
import com.hutong.supersdk.mysql.inst.model.SdkConfig;

/**
 * 管理服务器端接口
 * @author QINZH
 *
 */
@ServiceName("manageServerService")
public interface IManageService extends IServerService {
	
	/**
	 * 根据shortId查询所有AppConfig信息
	 * @param appShortId
	 * @return
	 * @throws SuperSDKException
	 */
	public List<AppConfig> finAllAppConfigByShortId(String appShortIdList)throws SuperSDKException;
	
	/**
	 * 根据AppId查询AppConfig信息
	 * @param appId
	 * @return
	 * @throws SuperSDKException
	 */
	public AppConfig findAppByAppId(String appId) throws SuperSDKException;
	
	/**
	 * 根据AppId查询此AppId的所有SDKConfig信息
	 * @param appId
	 * @return
	 * @throws SuperSDKException
	 */
	public List<SdkConfig> findSDKConfigByAppId(String appId)throws SuperSDKException;
	
	/**
	 * 根据AppId查询此AppId的所有SDKClientConfig信息
	 * @param appId
	 * @return
	 * @throws SuperSDKException
	 */
	public List<SdkClientConfig> findSDKClientConfigByAppId(String appId)throws SuperSDKException;
	
	/**
	 * 修改AppConfig
	 * @param appConfig
	 * @return
	 * @throws SuperSDKException
	 */
	public boolean updateAppConfig(AppConfig appConfig)throws SuperSDKException;
	
	/**
	 * 修改AppConfig的启用状态
	 * @param appId
	 * @param status
	 * @return
	 * @throws SuperSDKException
	 */
	public boolean setAppEnableFlag(String appId, boolean status)throws SuperSDKException;
	
	/**
	 * 修改AppSDKConfig的启用状态
	 * @param appChannelId
	 * @param status
	 * @return
	 * @throws SuperSDKException
	 */
	public boolean setAppSDKConfigEnableFlag(String appId, String appChannelId, boolean status)throws SuperSDKException;
	
	/**
	 * 修改AppSDKClientConfig的启用状态
	 * @param appChannelId
	 * @param status
	 * @return
	 * @throws SuperSDKException
	 */
	public boolean setAppSDKClientConfigEnableFlag(String appId, String appChannelId, boolean status)throws SuperSDKException;
	
	/**
	 * 新增AppConfig
	 * @param appConfig
	 * @return
	 */
	public boolean saveAppConfig(AppConfig appConfig);
	
	/**
	 * 根据appChannelId查询此AppId的SDKClientConfig信息
	 * @param appId
	 * @param appChannelId
	 * @return
	 * @throws SuperSDKException
	 */
	public SdkClientConfig findSDKClientConfigByChannelId(String appId, String appChannelId)throws SuperSDKException;
	
	/**
	 * 根据appChannelId查询此AppId的SDKConfig信息
	 * @param appId
	 * @param appChannelId
	 * @return
	 * @throws SuperSDKException
	 */
	public SdkConfig findSDKConfigByChannelId(String appId, String appChannelId)throws SuperSDKException;
	
	/**
	 * 根据AppId修改SdkClientConfig
	 * @param appId
	 * @param sdkClientConfig
	 * @return
	 * @throws SdkClientConfig
	 */
	public boolean updateSdkClientConfig(String appId, SdkClientConfig sdkClientConfig)throws SuperSDKException;
	
	/**
	 * 根据AppId修改SdkConfig
	 * @param appId
	 * @param sdkClientConfig
	 * @return
	 * @throws SdkClientConfig
	 */
	public boolean updateSdkConfig(String appId, SdkConfig sdkConfig)throws SuperSDKException;
	
	/**
	 * 根据AppId添加SdkClientConfig信息
	 * @param appId
	 * @param sdkClientConfig
	 * @return
	 */
	public boolean saveSDKClientConfig(String appId, SdkClientConfig sdkClientConfig);
	
	/**
	 * 根据AppId添加SdkConfig信息
	 * @param appId
	 * @param sdkConfig
	 * @return
	 */
	public boolean saveSDKConfig(String appId, SdkConfig sdkConfig);
}
