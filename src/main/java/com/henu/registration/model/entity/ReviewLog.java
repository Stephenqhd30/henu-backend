package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 审核记录表
 *
 * @author stephenqiu
 * @TableName review_log
 */
@TableName(value = "review_log")
@Data
public class ReviewLog implements Serializable {
	@Serial
	private static final long serialVersionUID = 4476526755672844647L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 报名登记表id
	 */
	private Long registrationId;
	
	/**
	 * 审核人id
	 */
	private Long reviewerId;
	
	/**
	 * 审核状态(0-待审核,1-审核通过,2-审核不通过)
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核意见
	 */
	private String reviewComments;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
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