package com.henu.registration.model.dto.school;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建高校信息请求
 *
 * @author stephen qiu
 */
@Data
public class SchoolAddRequest implements Serializable {
	
	/**
	 * 高校名称
	 */
	private String schoolName;
	
	private static final long serialVersionUID = 1L;
}