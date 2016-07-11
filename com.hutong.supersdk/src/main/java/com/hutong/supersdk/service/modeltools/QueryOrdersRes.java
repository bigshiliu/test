package com.hutong.supersdk.service.modeltools;

import java.util.List;

import com.hutong.supersdk.common.constant.DataKeys;
import com.hutong.supersdk.common.constant.ErrorEnum;
import com.hutong.supersdk.common.model.JsonResObj;
import com.hutong.supersdk.common.util.ParseJson;
import com.hutong.supersdk.mysql.inst.model.PaymentOrder;

/**
 * 查询订单返回对象
 * @author QINZH
 *
 */
public class QueryOrdersRes {

	private JsonResObj res;

	public QueryOrdersRes() {
		this.res = new JsonResObj();
	}

	public QueryOrdersRes ok() {
		this.res.ok();
		return this;
	}
	
	public QueryOrdersRes fail() {
		this.res.fail();
		return this;
	}

	public QueryOrdersRes setError(ErrorEnum error, String errorMsg) {
		this.res.fail();
		this.res.setError(error, errorMsg);
		return this;
	}
	
	public QueryOrdersRes setErrorMsg(String errorMsg) {
		this.res.getExtra().put("errorMsg", errorMsg);
		return this;
	}
	
	public QueryOrdersRes setOrderList(List<PaymentOrder> list){
		this.res.getData().put(DataKeys.QueryOrder.ORDER_LIST_KEY, ParseJson.encodeJson(list));
		return this;
	}

	public JsonResObj getRes() {
		return res;
	}

}
