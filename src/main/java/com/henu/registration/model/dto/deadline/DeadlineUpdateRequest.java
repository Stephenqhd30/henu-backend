package com.henu.registration.model.dto.deadline;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新截止时间请求
 *
 * @author stephen qiu
 */
@Data
public class DeadlineUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	/**
	 * 截止日期
	 */
	private Date deadlineTime;
	
	private static final long serialVersionUID = 1L;
}