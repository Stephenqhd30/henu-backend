package com.henu.registration.model.dto.job;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询岗位信息表请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobQueryRequest extends PageRequest implements Serializable {
	
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
	 * 岗位名称
	 */
	private String jobName;
	
	/**
	 * 岗位说明
	 */
	private String jobExplanation;
	
	/**
	 * 创建用户 id
	 */
	private Long adminId;
	
	private static final long serialVersionUID = 1L;
}