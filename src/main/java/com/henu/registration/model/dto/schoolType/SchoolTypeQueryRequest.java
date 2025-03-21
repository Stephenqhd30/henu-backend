package com.henu.registration.model.dto.schoolType;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询高校类型请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SchoolTypeQueryRequest extends PageRequest implements Serializable {
	
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
	 * 高校类别名称
	 */
	private String typeName;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	private static final long serialVersionUID = 1L;
}