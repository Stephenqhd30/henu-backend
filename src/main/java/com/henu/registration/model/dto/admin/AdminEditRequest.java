package com.henu.registration.model.dto.admin;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑管理员请求
 *
 * @author stephen qiu
 */
@Data
public class AdminEditRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 管理员编号
	 */
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	private String adminName;
	
	private static final long serialVersionUID = 1L;
}