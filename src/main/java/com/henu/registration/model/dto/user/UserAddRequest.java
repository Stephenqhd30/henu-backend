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
public class UserAddRequest implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -6510457969873015318L;
	
	/**
	 * 账户
	 */
	private String userAccount;
	
	/**
	 * 用户密码
	 */
	private String userPassword;
	
	/**
	 * 身份证号码
	 */
	private String userIdCard;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 邮箱地址
	 */
	private String userEmail;
	
	/**
	 * 联系电话
	 */
	private String userPhone;
	
	/**
	 * 性别(0-男,1-女)
	 */
	private Integer userGender;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
}