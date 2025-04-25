package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 报名登记表
 *
 * @author stephenqiu
 * @TableName registration_form
 */
@TableName(value = "registration_form")
@Data
public class RegistrationForm implements Serializable {
	@Serial
	private static final long serialVersionUID = -5469797858558186462L;
	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
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
	 * 民族
	 */
	private String ethnic;
	
	/**
	 * 政治面貌
	 */
	private String politicalStatus;
	
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
	 * 主要学生干部经历
	 */
	private String studentLeaders;
	
	/**
	 * 干部经历描述
	 */
	private String leaderExperience;
	
	/**
	 * 获奖情况
	 */
	private String studentAwards;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
	/**
	 * 报名状态(0-待审核,1-审核通过,2-审核不通过)
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
	/**
	 * 审核人姓名
	 */
	private String reviewer;
	
	/**
	 * 审核意见
	 */
	private String reviewComments;
	
	/**
	 * 报名状态(0-待报名,1-已报名,2-待面试,3-拟录取)
	 */
	private Integer registrationStatus;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否逻辑删除(0-否,1-是)
	 */
	@TableLogic
	private Integer isDelete;
}