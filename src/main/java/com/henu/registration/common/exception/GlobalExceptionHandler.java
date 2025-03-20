package com.henu.registration.common.exception;

import cn.dev33.satoken.exception.SaTokenException;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author stephenqiu
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	@ExceptionHandler(BusinessException.class)
	public BaseResponse<?> businessExceptionHandler(BusinessException e) {
		log.error("BusinessException", e);
		return ResultUtils.error(e.getCode(), e.getMessage());
	}
	
	@ExceptionHandler(RuntimeException.class)
	public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
		log.error("RuntimeException", e);
		return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
	}
	
	@ExceptionHandler(SaTokenException.class)
	public BaseResponse<?> saTokenExceptionsHandler(SaTokenException e) {
		log.error("SaTokenException", e);
		return ResultUtils.error(e.getCode(), e.getMessage());
	}
}
