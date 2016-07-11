package com.hutong.supersdk.mysql.config.dao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.config.model.AppConfig;

@Repository
public class AppConfigDao extends ABaseConfigDao<AppConfig> {
	
	private Log logger = LogFactory.getLog(AppConfig.class);
	
	private final String GET_BY_SHORTID = " from AppConfig ac where ac.shortAppId = ?";
	
	private final String GET_BY_SHORT_APP_IDS = " from AppConfig ac where ac.shortAppId in (:ids)";
	
	private final String GET_BY_APP_IDS = " from AppConfig ac where ac.appId in (:ids)";
	
	public AppConfig getByAppId(String appId) {
		return this.findById(appId);
	}

	public AppConfig getByShortId(String shortAppId) {
		@SuppressWarnings("unchecked")
		List<AppConfig> acs = getHibernateTemplate().find(GET_BY_SHORTID, shortAppId);
		if (acs != null && false == acs.isEmpty()) {
			return acs.get(0);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AppConfig> queryList(String[] appIds) {
		Query query = this.createQuery(GET_BY_APP_IDS).setParameterList("ids", appIds);
		if(null == query)
			return null;
		return query.list();
	}
	
	/**
	 * 根据status设置AppId的enable_flag,如果修改状态与DB相同则不修改,直接返回
	 * @param appId
	 * @param status
	 * @return
	 */
	public boolean setAppConfigEnableFlag(String appId, boolean status){
		boolean flag = false;
		try {
			AppConfig appConfig = this.findById(appId);
			if(null != appConfig){
				/**
				 * 如果status跟DB中相同,则不修改直接返回
				 */
				if(!((1 == appConfig.getEnableFlag() && status) || (0 == appConfig.getEnableFlag() && !status))){
					if(status){
						appConfig.setEnableFlag(1);
					}else{
						appConfig.setEnableFlag(0);
					}
					this.update(appConfig);
				}
				flag = true;
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		return flag;
	}
	
	/**
	 * 根据appShortIdList查询appConfig信息
	 * @param appShortIdList
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<AppConfig> loadByShortId(String appShortIdList) {
		if(StringUtils.isEmpty(appShortIdList)){
			return null;
		}
		//根据,将字符串进行分割
		String[] shortIdTemp = appShortIdList.split(",");
//		//组装查询语句
//		String shortIdStr = new String();
//		StringBuffer sb = new StringBuffer();
//		for(String tempStr: shortIdTemp){
//			sb.append("'");
//			sb.append(tempStr);
//			sb.append("',");
//		}
//		//删除最后一个逗号
//		if(!"".equals(sb))
//			shortIdStr = sb.substring(0, sb.lastIndexOf(","));
		Query query = this.createQuery(GET_BY_SHORT_APP_IDS).setParameterList("ids", shortIdTemp);
		if(null == query)
			return null;
		return query.list();
	}
	
	public void saveOrUpdateOnFlush(AppConfig appConfig){
		getHibernateTemplate().flush();
		getHibernateTemplate().clear();
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		session.saveOrUpdate(appConfig);
		session.flush();
		session.clear();
		session.close();
	}
}
