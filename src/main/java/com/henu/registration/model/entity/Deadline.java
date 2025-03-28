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
 * 截止时间
 *
 * @author stephenqiu
 * @TableName deadline
 */
@TableName(value = "deadline")
@Data
public class Deadline implements Serializable {
	@Serial
	private static final long serialVersionUID = 3641021925607642974L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 截止日期
	 */
	private Date deadlineTime;
	
	/**
	 * 岗位信息id
	 */
	private Long jobId;
	
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