package com.henu.registration.model.vo.education;

import com.henu.registration.model.entity.Education;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 教育经历表视图
 *
 * @author stephen
 */
@Data
public class EducationVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 243184720008858972L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校编号
	 */
	private Long schoolId;
	
	/**
	 * 教育阶段
	 */
	private String educationalStage;
	
	/**
	 * 专业
	 */
	private String major;
	
	/**
	 * 学习起止年月
	 */
	private String studyTime;
	
	/**
	 * 证明人
	 */
	private String certifier;
	
	/**
	 * 证明人联系电话
	 */
	private String certifierPhone;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	
	/**
	 * 创建用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 毕业学校信息
	 */
	private SchoolVO schoolVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param educationVO educationVO
	 * @return {@link Education}
	 */
	public static Education voToObj(EducationVO educationVO) {
		if (educationVO == null) {
			return null;
		}
		Education education = new Education();
		BeanUtils.copyProperties(educationVO, education);
		return education;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param education education
	 * @return {@link EducationVO}
	 */
	public static EducationVO objToVo(Education education) {
		if (education == null) {
			return null;
		}
		EducationVO educationVO = new EducationVO();
		BeanUtils.copyProperties(education, educationVO);
		return educationVO;
	}
}
