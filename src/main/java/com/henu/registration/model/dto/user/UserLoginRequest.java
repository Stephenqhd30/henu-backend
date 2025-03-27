package com.henu.registration.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author stephen qiu
 */
@Data
public class UserLoginRequest implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
	/**
	 * 账号
	 */
	private String userAccount;
}
