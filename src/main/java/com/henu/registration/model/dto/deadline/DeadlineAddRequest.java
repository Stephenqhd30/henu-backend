package com.henu.registration.model.dto.deadline;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建截止时间请求
 *
 * @author stephen qiu
 */
@Data
public class DeadlineAddRequest implements Serializable {
	
	/**
	 * 截止日期
	 */
	private Date deadlineTime;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
	private static final long serialVersionUID = 1L;
}