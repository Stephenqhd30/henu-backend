package com.henu.registration.model.dto.schoolSchoolType;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新高校与高校类型关联信息请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolSchoolTypeUpdateRequest implements Serializable {
	
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
	
	private static final long serialVersionUID = 1L;
}