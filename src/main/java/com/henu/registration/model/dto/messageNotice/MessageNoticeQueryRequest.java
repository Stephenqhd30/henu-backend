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
	 * 面试时间
	 */
	private Date interviewTime;
	
	/**
	 * 面试地点
	 */
	private String interviewLocation;
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