package com.hutong.supersdk.iservice;

/**
 * 调用Platform系统Service
 * @author QINZH
 *
 */
public interface IPlatformService {
	
	public boolean checkToken(String userId, String token);
}
