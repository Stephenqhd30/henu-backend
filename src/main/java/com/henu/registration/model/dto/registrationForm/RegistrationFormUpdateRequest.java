package com.henu.registration.model.dto.registrationForm;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新报名登记请求
 *
 * @author stephen qiu
 */
@Data
public class RegistrationFormUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
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
	 * 证件照
	 */
	private String userAvatar;
	
	/**
	 * 生活照
	 */
	private String userLifePhoto;
	
	/**
	 * 报名登记表文件
	 */
	private String registrationForm;
	
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
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	@Serial
	private static final long serialVersionUID = 1L;
}