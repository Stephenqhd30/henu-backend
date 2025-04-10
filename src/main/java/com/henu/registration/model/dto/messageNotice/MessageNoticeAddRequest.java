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
	 * 报名登记表id列表
	 */
	private List<Long> registrationIds;
	
	/**
	 * 面试内容
	 */
	private String content;
	
	@Serial
	private static final long serialVersionUID = 1L;
}