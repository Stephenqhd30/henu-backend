package com.henu.registration.model.dto.messageNotice;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageNoticeAddRequest implements Serializable {
	
	/**
	 * 通知标题
	 */
	private String title;
	
	/**
	 * 通知内容
	 */
	private String content;
	
	/**
	 * 报名登记表id
	 */
	private Long registrationId;
	
	private static final long serialVersionUID = 1L;
}