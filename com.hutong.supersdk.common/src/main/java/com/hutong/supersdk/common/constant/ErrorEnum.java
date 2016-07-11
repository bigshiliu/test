package com.hutong.supersdk.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * Error枚举类
 * @author QINZH
 *
 */
public enum ErrorEnum {
	/**
	 * 成功
	 */
	NOT_ERROR(0),
	/**
	 * 服务器错误
	 */
	SERVER_ERROR(99),
	/**
	 * 一般错误
	 */
	ERROR(1),
	
	/**
	 * 参数错误
	 */
	APP_ID_ERROR(2),
	
	/**
	 * 签名错误
	 */
	SIGN_ERROR(3),
	
	/**
	 * Service未找到
	 */
	SERVICE_NOT_FOUND(4),

	/**
	 * 参数错误，传入的app_channel_id找不到
	 */
	APP_CHANNEL_ID_NOT_FOUND(1001),
	
	/** 
	 * 参数错误,参数缺失
	 */
	PARAM_IS_NULL(1002),
	
	/**
	 * 查询结果为空
	 */
	SEARCH_RESULT_IS_NULL(1003),
	
	/**
	 * 推送消息内容为空
	 */
	PUSH_MESSAGE_IS_NULL(1004),
	
	/**
	 * 推送消息失败
	 */
	PUSH_FAILED(1005),
	
	/**
	 * 创建用户失败
	 */
	USER_CREATE_FAILED(2000),
	
	/**
	 * SDK验证用户失败
	 */
	USER_VERIFY_FAILED(2001),
	
	/**
	 * 支付订单数据不完整
	 */
	PAY_PARAMETER_INCOMPLETE(3001),
	
	/**
	 * 订单Id未找到
	 */
	ORDER_ID_NOT_FOUND(3002),
	
	/**
	 * 向SDK服务器提交订单失败
	 */
	POST_ORDER_FAILED(3003),
	
	/**
	 * platform应用异常
	 */
	PLATFORM_SERVER_ERROR(4001),
	
	/**
	 * 非法的platformUserId
	 */
	PLATFORM_USER_ID_LLEGAL(4002),
	
	/**
	 * 没有足够的权限操作APP
	 */
	INSUFFICIENT_PERMISSIONS_IN_APP(4003),
	
	/**
	 * token无效
	 */
	ILLEGAL_TOKEN(4004),
	
	/**
	 * token检查失败
	 */
	CHECK_TOKEN_FAIL(4005),
	
	/**
	 * 推送别名错误
	 */
	PUSH_ALIAS_ERROR(5001);
	

	private static Map<Integer,ErrorEnum> map = new HashMap<Integer,ErrorEnum>();
	
	public final int errorCode;
	
	static{
		ErrorEnum[] enums = ErrorEnum.values();
		for (ErrorEnum errorEnum : enums) {
			map.put(errorEnum.errorCode, errorEnum);
			
		}
	}
	
	private ErrorEnum(int errorCode){
		this.errorCode = errorCode;
	}

	public static ErrorEnum valueOf(int errorCode) {
		return map.get(errorCode);
	}
}
