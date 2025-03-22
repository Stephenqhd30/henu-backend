package com.henu.registration.model.vo.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 * @author stephen qiu
 **/
@Data
public class LoginUserVO implements Serializable {
	
	private static final long serialVersionUID = 2837672255648064012L;
	/**
	 * 用户 id
	 */
	private Long id;
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
	
	/**
	 * token
	 */
	private String token;
	
}