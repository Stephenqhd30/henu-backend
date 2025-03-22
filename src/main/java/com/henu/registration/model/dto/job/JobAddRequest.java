package com.henu.registration.model.dto.job;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建岗位信息表请求
 *
 * @author stephen qiu
 */
@Data
public class JobAddRequest implements Serializable {
	
	/**
	 * 岗位名称
	 */
	private String jobName;
	
	/**
	 * 岗位说明
	 */
	private String jobExplanation;
	
	private static final long serialVersionUID = 1L;
}