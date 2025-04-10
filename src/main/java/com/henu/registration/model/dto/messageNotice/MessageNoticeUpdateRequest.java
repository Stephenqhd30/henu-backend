package com.henu.registration.model.dto.messageNotice;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

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
	 * 面试内容
	 */
	private String content;
	
	@Serial
	private static final long serialVersionUID = 1L;
}