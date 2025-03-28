package com.henu.registration.model.dto.messageNotice;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageNoticeAddRequest implements Serializable {
	
	/**
	 * 报名登记表id
	 */
	private Long registrationId;
	
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