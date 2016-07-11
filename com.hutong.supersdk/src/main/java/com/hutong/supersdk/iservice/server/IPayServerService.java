package com.hutong.supersdk.iservice.server;

import java.io.InputStream;
import java.util.Map;

import com.hutong.supersdk.common.exception.SuperSDKException;
import com.hutong.supersdk.common.iservice.ServiceName;
import com.hutong.supersdk.common.iservice.ServiceParam;
import com.hutong.supersdk.common.model.JsonResObj;

/**
 * 支付服务器端接口
 * @author QINZH
 *
 */
@ServiceName("payServerService")
public interface IPayServerService extends IServerService {
	
	/**
	 * 支付回调
	 * @param shortId 应用shorid
	 * @param channelId 渠道Id
	 * @param paramMap 请求参数
	 * @param servletInputStream
	 * @param method 方法
	 * @return
	 * @throws Exception
	 */
	public Object payBack(String shortId, String channelId, Map<String, String> paramMap,
			InputStream servletInputStream, String method) 
			throws SuperSDKException;
	
	@ServiceParam({"app_id", "notice_scope"})
	public JsonResObj renotice(String appId, String noticeScope) 
			throws SuperSDKException;
}
