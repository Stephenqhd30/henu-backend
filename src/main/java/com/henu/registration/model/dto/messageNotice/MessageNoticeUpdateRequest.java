package com.henu.registration.model.dto.messageNotice;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 更新消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageNoticeUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 面试时间
	 */
	private Date interviewTime;
	
	/**
	 * 面试地点
	 */
	private String interviewLocation;
	
	@Serial
	private static final long serialVersionUID = 1L;
}