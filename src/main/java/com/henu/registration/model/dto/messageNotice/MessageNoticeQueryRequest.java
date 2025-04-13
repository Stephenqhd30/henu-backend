package com.henu.registration.model.dto.messageNotice;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
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
	 * 通知用户名
	 */
	private String userName;
	
	/**
	 * 面试内容
	 */
	private String content;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	private Integer pushStatus;
	
	/**
	 * 通知用户id
	 */
	private Long userId;
	
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