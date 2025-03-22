package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 岗位信息表
 *
 * @author stephenqiu
 * @TableName job
 */
@TableName(value = "job")
@Data
public class Job implements Serializable {
	private static final long serialVersionUID = 7434903103837781054L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 岗位名称
	 */
	private String jobName;
	
	/**
	 * 岗位说明
	 */
	private String jobExplanation;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否逻辑删除
	 */
	@TableLogic
	private Integer isDelete;
}