package com.hutong.supersdk.mysql.inst.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.inst.model.SdkClientConfig;

@Repository
@SuppressWarnings("unchecked")
public class SDKClientConfigDao extends ABaseInstDao<SdkClientConfig> {
	
	private final String GET_BY_APP_ID_AND_CHANNEL_IDS = " from SdkClientConfig scc where scc.poId.appId = ? and scc.poId.appChannelId in (:ids)";
	
	private static final String GET_ALL_BY_APP_ID = " from SdkClientConfig scc where scc.poId.appId = ?";

    public SdkClientConfig getByPoId(String appId, String channelId) {
        return this.findById(new SdkClientConfig.PoId(appId, channelId));
    }

    /**
	 * 根据AppId查询所有SDKConfig信息
	 * @param appId
	 * @return List<SdkConfig>
	 */
	public List<SdkClientConfig> getAllByAppId(String appId){
		List<SdkClientConfig> sdkClientConfig = (List<SdkClientConfig>) getHibernateTemplate().find(GET_ALL_BY_APP_ID, appId);
		if (sdkClientConfig.size() == 0) {
			return null;
		} else {
			return sdkClientConfig;
		}
	}
    
	/**
	 * 根据channelIds查询客户端信息
	 * @param appId
	 * @param channelIds
	 * @return
	 */
	public List<SdkClientConfig> queryList(String appId, String[] channelIds) {
		Query query = this.createQuery(GET_BY_APP_ID_AND_CHANNEL_IDS).setString(0, appId).setParameterList("ids", channelIds);
		if(null == query)
			return null;
		return query.list();
	}
	
	/**
	 * 根据status设置appChannelId的enable_flag,如果修改状态与DB相同则不修改,直接返回
	 * @param channelId
	 * @param status
	 * @return
	 */
	public boolean setAppSDKClientConfigEnableFlag(String channelId, boolean status){
		boolean flag = false;
//		try {
//			SdkClientConfig sdkClientConfig = this.findById(channelId);
//			if(null != sdkClientConfig){
//				/**
//				 * 如果status跟DB中相同,则不修改直接返回
//				 */
//				if(!((1 == sdkClientConfig.getEnableFlag() && status) || (0 == sdkClientConfig.getEnableFlag() && !status))){
//					if(status){
//						sdkClientConfig.setEnableFlag(1);
//					}else{
//						sdkClientConfig.setEnableFlag(0);
//					}
//					this.update(sdkClientConfig);
//				}
//				flag = true;
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}
		return flag;
	}
}
