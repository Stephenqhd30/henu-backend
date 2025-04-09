package com.henu.registration.model.dto.job;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

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
	 * 截止日期
	 */
	private Date deadlineTime;
	
	/**
	 * 创建用户 id
	 */
	private Long adminId;
	
	@Serial
	private static final long serialVersionUID = 1L;
}