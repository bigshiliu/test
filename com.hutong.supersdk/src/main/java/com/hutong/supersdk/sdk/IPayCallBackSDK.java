package com.hutong.supersdk.sdk;

import java.io.InputStream;
import java.util.Map;

/**
 * 充值回调接口
 * @author Dongxu
 *
 */
public interface IPayCallBackSDK extends ISDK{
	
	/**
	 * 充值回调
	 * @param callback
	 * @param paramMap
	 * @param servletInputStream
	 * @param method TODO
	 * @param confiMap
	 * @return
	 */
	public Object payCallBack(IPaySuccessHandler callback, Map<String, String> paramMap, 
			InputStream servletInputStream, String method, Object config);
	
	
}
