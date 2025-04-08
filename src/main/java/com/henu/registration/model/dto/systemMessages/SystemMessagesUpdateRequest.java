package com.henu.registration.model.dto.systemMessages;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 更新系统消息请求
 *
 * @author stephen qiu
 */
@Data
public class SystemMessagesUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 通知标题
	 */
	private String title;
	
	/**
	 * 消息内容
	 */
	private String content;
	
	/**
	 * 推送时间
	 */
	private Date pushTime;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	private Integer pushStatus;
	
	@Serial
	private static final long serialVersionUID = 1L;
}