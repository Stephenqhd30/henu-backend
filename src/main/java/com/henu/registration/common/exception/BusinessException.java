package com.henu.registration.common.exception;

import com.henu.registration.common.ErrorCode;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author stephenqiu
 */
@Getter
public class BusinessException extends RuntimeException {
	
	private static final long serialVersionUID = -7132430283497991607L;
	/**
	 * 错误码
	 */
	private final int code;
	
	public BusinessException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.code = errorCode.getCode();
	}
	
	public BusinessException(ErrorCode errorCode, String message) {
		super(message);
		this.code = errorCode.getCode();
	}
	
}
