package com.henu.registration.model.vo.school;

import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 高校信息视图
 *
 * @author stephen
 */
@Data
public class SchoolVO implements Serializable {
	
	private static final long serialVersionUID = -5941319198049937390L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校名称
	 */
	private String schoolName;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
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
	private AdminVO adminVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param schoolVO schoolVO
	 * @return {@link School}
	 */
	public static School voToObj(SchoolVO schoolVO) {
		if (schoolVO == null) {
			return null;
		}
		School school = new School();
		BeanUtils.copyProperties(schoolVO, school);
		return school;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param school school
	 * @return {@link SchoolVO}
	 */
	public static SchoolVO objToVo(School school) {
		if (school == null) {
			return null;
		}
		SchoolVO schoolVO = new SchoolVO();
		BeanUtils.copyProperties(school, schoolVO);
		return schoolVO;
	}
}
