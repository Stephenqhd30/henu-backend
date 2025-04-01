package com.henu.registration.model.dto.messagePush;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询消息推送请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessagePushQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索词
	 */
	private String searchText;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 消息通知id
	 */
	private Long messageNoticeId;
	
	/**
	 * 通知用户名
	 */
	private String userName;
	
	/**
	 * 推送方式(websocket/email/sms/other)
	 */
	private String pushType;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	private Integer pushStatus;
	
	/**
	 * 推送消息内容
	 */
	private String pushMessage;
	
	/**
	 * 失败重试次数
	 */
	private Integer retryCount;
	
	/**
	 * 失败原因
	 */
	private String errorMessage;
	
	@Serial
	private static final long serialVersionUID = 1L;
}