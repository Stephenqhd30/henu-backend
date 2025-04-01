package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 消息推送表
 *
 * @author stephenqiu
 * @TableName message_push
 */
@TableName(value = "message_push")
@Data
public class MessagePush implements Serializable {
	@Serial
	private static final long serialVersionUID = 5616506834176956343L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
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
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
}