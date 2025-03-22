package com.henu.registration.model.dto.admin;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询管理员请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AdminQueryRequest extends PageRequest implements Serializable {
	
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
	 * 管理员编号
	 */
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	private String adminName;
	
	/**
	 * 管理员类型
	 */
	private String adminType;
	
	private static final long serialVersionUID = 1L;
}