package com.henu.registration.model.dto.schoolType;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建高校类型请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolTypeAddRequest implements Serializable {
	
	/**
	 * 高校类别名称
	 */
	private String typeName;
	
	
	@Serial
	private static final long serialVersionUID = 1L;
}