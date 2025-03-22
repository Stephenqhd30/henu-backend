package com.henu.registration.model.dto.cadreType;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新干部类型请求
 *
 * @author stephen qiu
 */
@Data
public class CadreTypeUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 干部类型
	 */
	private String type;
	
	private static final long serialVersionUID = 1L;
}