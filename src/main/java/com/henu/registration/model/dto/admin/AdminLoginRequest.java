package com.henu.registration.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author stephen qiu
 */
@Data
public class AdminLoginRequest implements Serializable {
	
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 工号
	 */
	private String adminNumber;
	
	/**
	 * 密码
	 */
	private String adminPassword;
}
