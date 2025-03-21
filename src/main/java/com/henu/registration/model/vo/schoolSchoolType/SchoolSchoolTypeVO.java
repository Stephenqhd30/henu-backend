package com.henu.registration.model.vo.schoolSchoolType;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.model.vo.schoolType.SchoolTypeVO;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 高校与高校类型关联信息视图
 *
 * @author stephen
 */
@Data
public class SchoolSchoolTypeVO implements Serializable {
	
	private static final long serialVersionUID = 2241706733027810025L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校id
	 */
	private Long schoolId;
	
	/**
	 * 高校类别列表(JSON存储)
	 */
	private List<String> schoolTypes;
	
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
	 * 高校信息
	 */
	private SchoolVO schoolVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param schoolSchoolTypeVO schoolSchoolTypeVO
	 * @return {@link SchoolSchoolType}
	 */
	public static SchoolSchoolType voToObj(SchoolSchoolTypeVO schoolSchoolTypeVO) {
		if (schoolSchoolTypeVO == null) {
			return null;
		}
		SchoolSchoolType schoolSchoolType = new SchoolSchoolType();
		BeanUtils.copyProperties(schoolSchoolTypeVO, schoolSchoolType);
		List<String> schoolTypesList = schoolSchoolTypeVO.getSchoolTypes();
		if (CollUtil.isNotEmpty(schoolTypesList)) {
			schoolSchoolType.setSchoolTypes(JSONUtil.toJsonStr(schoolTypesList));
		}
		return schoolSchoolType;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param schoolSchoolType schoolSchoolType
	 * @return {@link SchoolSchoolTypeVO}
	 */
	public static SchoolSchoolTypeVO objToVo(SchoolSchoolType schoolSchoolType) {
		if (schoolSchoolType == null) {
			return null;
		}
		SchoolSchoolTypeVO schoolSchoolTypeVO = new SchoolSchoolTypeVO();
		BeanUtils.copyProperties(schoolSchoolType, schoolSchoolTypeVO);
		if (StringUtils.isNotBlank(schoolSchoolType.getSchoolTypes())) {
			schoolSchoolTypeVO.setSchoolTypes(JSONUtil.toList(schoolSchoolType.getSchoolTypes(), String.class));
		}
		return schoolSchoolTypeVO;
	}
}
