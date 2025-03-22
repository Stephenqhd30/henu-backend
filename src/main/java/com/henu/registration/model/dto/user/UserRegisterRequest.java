package com.henu.registration.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author stephen qiu
 */
@Data
public class UserRegisterRequest implements Serializable {
	
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 身份证号码
	 */
	private String userIdCard;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 再次输入身份证号码
	 */
	private String checkUserIdCard;
}
