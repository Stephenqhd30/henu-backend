package com.henu.registration.model.dto.messageNotice;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 查询消息通知请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageNoticeQueryRequest extends PageRequest implements Serializable {
	
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
	 * 通知内容
	 */
	private String content;
	
	/**
	 * 阅读状态(0-未读,1-已读)
	 */
	private Integer readStatus;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 报名登记表id
	 */
	private Long registrationId;
	
	@Serial
	private static final long serialVersionUID = 1L;
}