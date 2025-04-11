package com.henu.registration.model.dto.registrationForm;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 查询报名登记请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RegistrationFormQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索词
	 */
	private String searchText;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 性别(0-男,1-女)
	 */
	private Integer userGender;
	
	/**
	 * 婚姻状况(0-未婚，1-已婚)
	 */
	private Integer marryStatus;
	
	/**
	 * 政治面貌
	 */
	private String politicalStatus;
	
	/**
	 * 工作经历
	 */
	private String workExperience;
	
	/**
	 * 主要学生干部经历
	 */
	private List<String> studentLeaders;
	
	/**
	 * 干部经历描述
	 */
	private String leaderExperience;
	
	/**
	 * 获奖情况
	 */
	private String studentAwards;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 报名状态(0-待审核,1-审核通过,2-审核不通过)
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核人姓名
	 */
	private String reviewer;
	
	/**
	 * 报名状态(0-待报名,1-已报名,2-待发送面试通知,3-已发送面试通知)
	 */
	private Integer registrationStatus;
	
	/**
	 * 高校类别列表
	 */
	private List<String> schoolTypes;
	
	/**
	 * 教育阶段列表
	 */
	private List<String> educationStages;
	
	@Serial
	private static final long serialVersionUID = 1L;
}