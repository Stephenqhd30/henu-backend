package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 消息通知表
 *
 * @author stephenqiu
 * @TableName message_notice
 */
@TableName(value = "message_notice")
@Data
public class MessageNotice implements Serializable {
	private static final long serialVersionUID = -6138999993064579713L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
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
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否逻辑删除(0-否,1-是)
	 */
    @TableLogic
	private Integer isDelete;
}