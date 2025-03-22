package com.henu.registration.model.dto.user;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
	private static final long serialVersionUID = 8796619426266616906L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索关键词
	 */
	private String searchText;
	
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
	
	/**
	 * 民族
	 */
	private String ethnic;
	
	/**
	 * 入党时间
	 */
	private String partyTime;
	
	/**
	 * 出生日期
	 */
	private String birthDate;
	
	/**
	 * 婚姻状况(0-未婚，1-已婚)
	 */
	private Integer marryStatus;
	
	/**
	 * 紧急联系电话
	 */
	private String emergencyPhone;
	
	/**
	 * 家庭住址
	 */
	private String address;
	
	/**
	 * 工作经历
	 */
	private String workExperience;
	
	/**
	 * 主要学生干部经历及获奖情况
	 */
	private String studentLeaderAwards;
	
}