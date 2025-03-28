package com.henu.registration.model.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author stephen qiu
 */
@Data
public class UserRegisterRequest implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 用户账户
	 */
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
	/**
	 * 用户密码
	 */
	private String checkUserPassword;
}
