package com.hutong.supersdk.common.iservice;

import com.hutong.supersdk.common.model.JsonReqObj;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * SuperSDK Client组件使用的支付接口
 * @author QINZH
 *
 */
@ServiceName("PayService")
public interface IPayService extends IClientService {

	/**
	 * 根据玩家supersdk_id查询支付订单信息
	 * @param reqObj JSON Format Text
	 *               time:时间戳
	 *               data:
	 *                   num:查询最大数量,最大值为50
	 *                   app_id:游戏app_id
	 *                   supersdk_uid:用户id
	 *                   start_time:查询起时间，Unix时间戳，以s为单位
	 *                   end_time 查询止时间，Unix时间戳，以s为单位
	 *                   //TODO 增加参数策略说明
	 *               extra:
	 *                   key:value
	 * @return JSON Format Text
	 * TODO 补充详细的JSON内容
     */
	@ServiceParam({"jsonData"})
	public JsonResObj queryOrders(JsonReqObj reqObj);

}
