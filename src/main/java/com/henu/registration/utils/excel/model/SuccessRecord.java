package com.henu.registration.utils.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成功记录
 * @author stephenqiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessRecord <T> {
	/**
	 * 导入的数据
	 */
	private T data;
	
	/**
	 * 导入成功信息
	 */
	private String message;
}
