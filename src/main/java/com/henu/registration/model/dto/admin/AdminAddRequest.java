package com.henu.registration.model.dto.admin;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建管理员请求
 *
 * @author stephen qiu
 */
@Data
public class AdminAddRequest implements Serializable {
	
	/**
	 * 管理员编号
	 */
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	private String adminName;
	
	/**
	 * 管理员类型
	 */
	private String adminType;
	
	/**
	 * 管理员密码
	 */
	private String adminPassword;
	
	
	private static final long serialVersionUID = 1L;
}