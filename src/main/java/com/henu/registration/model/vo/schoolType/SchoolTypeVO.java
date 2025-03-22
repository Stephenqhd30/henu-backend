package com.henu.registration.model.vo.schoolType;

import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 高校类型视图
 *
 * @author stephen
 */
@Data
public class SchoolTypeVO implements Serializable {
	
	private static final long serialVersionUID = 5432946341434796121L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校类别名称
	 */
	private String typeName;
	
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
	 * @param schoolTypeVO schoolTypeVO
	 * @return {@link SchoolType}
	 */
	public static SchoolType voToObj(SchoolTypeVO schoolTypeVO) {
		if (schoolTypeVO == null) {
			return null;
		}
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolTypeVO, schoolType);
		return schoolType;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param schoolType schoolType
	 * @return {@link SchoolTypeVO}
	 */
	public static SchoolTypeVO objToVo(SchoolType schoolType) {
		if (schoolType == null) {
			return null;
		}
		SchoolTypeVO schoolTypeVO = new SchoolTypeVO();
		BeanUtils.copyProperties(schoolType, schoolTypeVO);
		return schoolTypeVO;
	}
}
