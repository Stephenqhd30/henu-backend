package com.henu.registration.utils.excel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误记录
 * @author stephenqiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRecord <T> {
	/**
	 * 导入的数据
	 */
	private T data;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
}
