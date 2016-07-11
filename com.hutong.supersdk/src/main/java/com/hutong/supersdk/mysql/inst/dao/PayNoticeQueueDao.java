package com.hutong.supersdk.mysql.inst.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.hutong.supersdk.mysql.inst.model.PayNoticeQueue;

@Repository
public class PayNoticeQueueDao extends ABaseInstDao<PayNoticeQueue> {
	
	private static final String GET_NOTICE = " from PayNoticeQueue pn where pn.queueType = ?";
	
	@SuppressWarnings("unchecked")
	public List<PayNoticeQueue> getNoticesByType(String queueType) {
		return this.getHibernateTemplate().find(GET_NOTICE, queueType);
	}

}
