package com.hutong.supersdk.common.exception;

import com.hutong.supersdk.common.constant.ErrorEnum;

/**
 * SuperSDK异常类
 * @author QINZH
 *
 */
public class SuperSDKException extends Exception {

	private static final long serialVersionUID = 1L;

	protected int errorCode = ErrorEnum.ERROR.errorCode;

	public SuperSDKException() {
		super();
	}

	public SuperSDKException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		if (arg1 instanceof SuperSDKException) {
			this.errorCode = ((SuperSDKException) arg1).errorCode;
		}
	}

	public SuperSDKException(String arg0) {
		super(arg0);
	}

	public SuperSDKException(Throwable arg0) {
		super(arg0);
		if (arg0 instanceof SuperSDKException) {
			this.errorCode = ((SuperSDKException) arg0).errorCode;
		}
	}
	
	public SuperSDKException(int errorCode) {
		super(ErrorEnum.valueOf(errorCode).toString());
		this.errorCode = errorCode;
	}

	public SuperSDKException(int errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}
	
	public SuperSDKException(ErrorEnum e) {
		super(e.toString());
		this.errorCode = e.errorCode;
	}

	public SuperSDKException(ErrorEnum e, String msg) {
		super(msg);
		this.errorCode = e.errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	@Override
	public String toString() {
		return super.toString() + " ErrorCode:" + this.errorCode;
	}

}
