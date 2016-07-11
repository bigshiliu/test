package com.hutong.supersdk.mysql.inst.dao;

import java.util.List;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.inst.model.SdkConfig;


@Repository
@SuppressWarnings("unchecked")
public class SDKConfigDao extends ABaseInstDao<SdkConfig> {
	
	private static final Logger logger = LoggerFactory.getLogger(SDKConfigDao.class);
	
	private static final String GET_ALL_BY_APP_ID = " from SdkConfig sc where sc.poId.appId = ?";
	
	private static final String GET_BY_APP_ID_AND_CHANNEL_IDS = "FROM SdkConfig sc WHERE sc.poId.appId = ? and sc.poId.appChannelId in (:ids)";

	public List<SdkConfig> queryList(String appId, String[] channelIds) {
		Query query = this.createQuery(GET_BY_APP_ID_AND_CHANNEL_IDS).setString(0, appId).setParameterList("ids", channelIds);
		return query.list();
	}

    public SdkConfig getByPoId(String appId, String channelId) {
		return this.findById(new SdkConfig.PoId(appId, channelId));
	}
	
	/**
	 * 根据AppId查询所有SDKConfig信息
	 * @param appId
	 * @return List<SdkConfig>
	 */
	public List<SdkConfig> getAllByAppId(String appId){
		List<SdkConfig> sdkConfig = (List<SdkConfig>) getHibernateTemplate().find(GET_ALL_BY_APP_ID, appId);
		if (sdkConfig.size() == 0) {
			return null;
		} else {
			return sdkConfig;
		}
	}
	
	/**
	 * 根据status设置appChannelId的enable_flag,如果修改状态与DB相同则不修改,直接返回
	 * @param appChannelId
	 * @param status
	 * @return
	 */
	public boolean setAppSDKConfigEnableFlag(String appChannelId, boolean status){
		boolean flag = false;
		try {
			SdkConfig sdkConfig = this.findById(appChannelId);
			if(null != sdkConfig){
				/**
				 * 如果status跟DB中相同,则不修改直接返回
				 */
				sdkConfig.getEnableFlag();
				if(!((1 == sdkConfig.getEnableFlag() && status) || (0 == sdkConfig.getEnableFlag() && !status))){
					if(status){
						sdkConfig.setEnableFlag(1);
					}else{
						sdkConfig.setEnableFlag(0);
					}
					this.update(sdkConfig);
				}
				flag = true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}
}
