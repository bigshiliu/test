package com.hutong.supersdk.mysql.inst.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

@Repository
public class PaymentOrderDao extends ABaseInstDao<PaymentOrder> {
	
	private static final String QUERY_LIST_BY_IDS = "FROM PaymentOrder po WHERE po.orderId in (:ids)";
	
	private static final String GET_BY_STATUS_PLATFORMID = "from PaymentOrder po where po.appChannelId = ? and po.payStatus = ? and po.sdkOrderId = ?";
	
	private static final String QUERY_BY_ORDER_ID = "from PaymentOrder po where po.orderId = ?";
	
	@SuppressWarnings("unchecked")
	public List<PaymentOrder> queryList(String[] orderIds) {
		Query query = this.createQuery(QUERY_LIST_BY_IDS).setParameterList("ids", orderIds);
		return query.list();
	}
	
	public PaymentOrder queryByOrderId(String orderId) {
		@SuppressWarnings("unchecked")
		List<PaymentOrder> list = (List<PaymentOrder>) super.find(QUERY_BY_ORDER_ID,orderId);
		if (0 < list.size())
			return list.get(0);
		else
			return null;
	}

	public boolean isExists(String channelId, String payStatus, String sdkOrderId) {
		@SuppressWarnings("unchecked")
		List<PaymentOrder> list = (List<PaymentOrder>) super.find(GET_BY_STATUS_PLATFORMID,
				channelId, payStatus, sdkOrderId);
		if (list.isEmpty())
			return false;
		else
			return true;
	}
	
	@SuppressWarnings("unchecked")
	public List<PaymentOrder> queryOrdersByUserId(String userId, int queryNum, String startTime, String endTime){
		StringBuffer sqlSb = new StringBuffer();
		String payStatus = "SUCCESS";
		sqlSb.append(" from PaymentOrder po where po.supersdkUid = ? and po.payStatus= ?");
		/**
		 * 如果设置了起止时间,则查询起始时间后的queryNum条订单
		 * 如果设置了终止时间,则查询终止时间前的queryNum条订单
		 * 否则,默认按照时间降序排列查询
		 */
		if(!StringUtils.isEmpty(startTime)){
			sqlSb.append(" and po.payTime > ?");
		}else if(!StringUtils.isEmpty(endTime)){
			sqlSb.append(" and po.payTime < ?");
		}
		sqlSb.append(" order by po.payTime");
		
		if(StringUtils.isEmpty(endTime)){
			sqlSb.append(" desc");
		}
		
		Query query = null;
		if(!StringUtils.isEmpty(startTime)){
			query = this.createQuery(sqlSb.toString(), userId,payStatus,startTime);
		}else if(!StringUtils.isEmpty(endTime)){
			query = this.createQuery(sqlSb.toString(), userId,payStatus,endTime);
		}else{
			query = this.createQuery(sqlSb.toString(), userId,payStatus);
		}
		
		query.setFirstResult(0);
		query.setMaxResults(queryNum);
		List<PaymentOrder> lists = query.list();
		return lists;
	}
}
