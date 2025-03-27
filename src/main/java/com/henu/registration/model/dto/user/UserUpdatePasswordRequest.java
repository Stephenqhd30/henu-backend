package com.henu.registration.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户创建请求
 *
 * @author stephen qiu
 */
@Data
public class UserUpdatePasswordRequest implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -6510457969873015318L;
	
	/**
	 * 联系电话
	 */
	private String userPhone;
	
	/**
	 * 验证码
	 */
	private String verificationCode;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
	/**
	 * 确认密码
	 */
	private String checkUserPassword;
	
	
}