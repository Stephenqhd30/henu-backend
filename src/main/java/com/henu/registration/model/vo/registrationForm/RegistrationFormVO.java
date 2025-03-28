package com.henu.registration.model.vo.registrationForm;

import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.utils.encrypt.AESUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import cn.hutool.core.collection.CollUtil;
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
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
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
		return registrationFormVO;
	}
}
