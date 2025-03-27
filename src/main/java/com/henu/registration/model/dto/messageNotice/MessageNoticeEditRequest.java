package com.henu.registration.model.dto.messageNotice;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑消息通知请求
 *
 * @author stephen qiu
 */
@Data
public class MessageNoticeEditRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 阅读状态(0-未读,1-已读)
	 */
	private Integer readStatus;
	
	@Serial
	private static final long serialVersionUID = 1L;
}