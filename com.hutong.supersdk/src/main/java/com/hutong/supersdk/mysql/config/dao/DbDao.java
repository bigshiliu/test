package com.hutong.supersdk.mysql.config.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.classic.Session;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.config.model.Db;

@Repository
public class DbDao extends ABaseConfigDao<Db> {
	
	private final String GET_BY_DB_IDS = " from Db d where d.dbId in (:ids)";
	
	/**
	 * 根据dbIds查询Db信息
	 * @param dbIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Db> loadByDbIds(String dbIds) {
		if(StringUtils.isEmpty(dbIds)){
			return null;
		}
		//根据,将字符串进行分割
		String[] dbIdTemp = dbIds.split(",");
		Query query = this.createQuery(GET_BY_DB_IDS).setParameterList("ids", dbIdTemp);
		if(null == query)
			return null;
		return query.list();
	}
	
	public void saveOrUpdateOnFlush(Db db){
		getHibernateTemplate().flush();
		getHibernateTemplate().clear();
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		session.saveOrUpdate(db);
		session.flush();
		session.clear();
		session.close();
	}
}
