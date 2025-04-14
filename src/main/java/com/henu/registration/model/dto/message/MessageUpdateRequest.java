package com.henu.registration.model.dto.message;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 通知主题
	 */
	private String title;
	
	/**
	 * 通知内容
	 */
	private String content;
	
	@Serial
	private static final long serialVersionUID = 1L;
}