package com.henu.registration.common;

import lombok.Getter;

/**
 * 自定义错误码
 *
 * @author stephenqiu
 */
@Getter
public enum ErrorCode {
	
	SUCCESS(0, "ok"),
	PARAMS_ERROR(40000, "请求参数错误"),
	EXCEL_ERROR(40001, "处理excel文件错误, 请检查表格信息是否有误"),
	PARAMS_SIZE_ERROR(40002, "上传图片大小最大为5MB"),
	WORD_ERROR(40003, "处理word文件错误"),
	AI_ERROR(40004, "AI识别失败"),
	CAPTCHA_ERROR(40005, "验证码错误，或已失效"),
	NOT_LOGIN_ERROR(40100, "未登录"),
	NO_AUTH_ERROR(40101, "无权限"),
	NOT_FOUND_ERROR(40400, "请求数据不存在"),
	SMS_ERROR(40400, "短信发送失败"),
	TOO_FREQUENT(40401, "请求太频繁"),
	FORBIDDEN_ERROR(40300, "禁止访问"),
	ALREADY_EXIST(40301, "已存在"),
	SYSTEM_ERROR(50000, "系统内部异常"),
	OPERATION_ERROR(50001, "操作失败");
	
	/**
	 * 状态码
	 */
	private final int code;
	
	/**
	 * 信息
	 */
	private final String message;
	
	ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}
	
}
