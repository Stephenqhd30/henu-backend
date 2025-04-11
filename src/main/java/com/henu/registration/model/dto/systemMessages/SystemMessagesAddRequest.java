package com.henu.registration.model.dto.systemMessages;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 创建系统消息请求
 *
 * @author stephen qiu
 */
@Data
public class SystemMessagesAddRequest implements Serializable {
	
	/**
	 * 通知标题
	 */
	private String title;
	
	/**
	 * 消息内容
	 */
	private String content;
	
	/**
	 * 消息类型
	 */
	private String type;
	
	@Serial
	private static final long serialVersionUID = 1L;
}