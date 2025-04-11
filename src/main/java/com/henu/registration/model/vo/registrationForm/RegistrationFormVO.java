package com.henu.registration.model.vo.registrationForm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.vo.education.EducationVO;
import com.henu.registration.model.vo.family.FamilyVO;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import com.henu.registration.model.vo.job.JobVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 报名登记视图
 *
 * @author stephen
 */
@Data
public class RegistrationFormVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -4562133149585501968L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
	
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
	 * 家庭住址
	 */
	private String address;
	
	/**
	 * 工作经历
	 */
	private String workExperience;
	
	/**
	 * 报名状态(0-待报名,1-已报名,2-待面试,3-已发送面试通知)
	 */
	private Integer registrationStatus;
	
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
	 * 紧急联系电话
	 */
	private String emergencyPhone;
	
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
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 岗位信息
	 */
	private JobVO jobVO;
	
	/**
	 * 申请人教育经历信息
	 */
	private List<EducationVO> educationVOList;
	
	/**
	 * 申请人家庭关系信息
	 */
	private List<FamilyVO> familyVOList;
	
	/**
	 * 上传附件信息
	 */
	private List<FileLogVO> fileLogVOList;
	
	/**
	 * 封装类转对象
	 *
	 * @param registrationFormVO registrationFormVO
	 * @return {@link RegistrationForm}
	 */
	public static RegistrationForm voToObj(RegistrationFormVO registrationFormVO) {
		if (registrationFormVO == null) {
			return null;
		}
		RegistrationForm registrationForm = new RegistrationForm();
		BeanUtils.copyProperties(registrationFormVO, registrationForm);
		List<String> studentLeaders = registrationFormVO.getStudentLeaders();
		if (CollUtil.isNotEmpty(studentLeaders)) {
			registrationForm.setStudentLeaders(JSONUtil.toJsonStr(studentLeaders));
		}
		return registrationForm;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param registrationForm registrationForm
	 * @return {@link RegistrationFormVO}
	 */
	public static RegistrationFormVO objToVo(RegistrationForm registrationForm) {
		if (registrationForm == null) {
			return null;
		}
		RegistrationFormVO registrationFormVO = new RegistrationFormVO();
		BeanUtils.copyProperties(registrationForm, registrationFormVO);
		String studentLeader = registrationForm.getStudentLeaders();
		if (StrUtil.isNotEmpty(studentLeader)) {
			registrationFormVO.setStudentLeaders(JSONUtil.toList(studentLeader, String.class));
		}
		return registrationFormVO;
	}
}
