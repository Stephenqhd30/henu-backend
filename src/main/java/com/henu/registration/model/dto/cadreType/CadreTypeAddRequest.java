package com.henu.registration.model.dto.cadreType;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建干部类型请求
 *
 * @author stephen qiu
 */
@Data
public class CadreTypeAddRequest implements Serializable {
	
	/**
	 * 干部类型
	 */
	private String type;
	
	
	private static final long serialVersionUID = 1L;
}