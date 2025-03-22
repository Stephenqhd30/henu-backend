package com.henu.registration.model.dto.schoolType;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑高校类型请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolTypeEditRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 高校类别名称
	 */
	private String typeName;
	
	private static final long serialVersionUID = 1L;
}