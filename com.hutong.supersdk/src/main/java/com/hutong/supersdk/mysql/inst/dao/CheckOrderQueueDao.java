package com.hutong.supersdk.mysql.inst.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.inst.model.CheckOrderQueue;

@Repository
public class CheckOrderQueueDao extends ABaseInstDao<CheckOrderQueue> {
	
	private static final String GET_ORDER_QUEUE = " from CheckOrderQueue co where co.queueType = ?";
	
	@SuppressWarnings("unchecked")
	public List<CheckOrderQueue> getNoticesByType(String queueType) {
		return this.getHibernateTemplate().find(GET_ORDER_QUEUE, queueType);
	}

}
