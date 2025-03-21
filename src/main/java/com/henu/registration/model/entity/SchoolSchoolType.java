package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 高校与高校类型关联表
 *
 * @author stephenqiu
 * @TableName school_school_type
 */
@TableName(value = "school_school_type")
@Data
public class SchoolSchoolType {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 高校id
	 */
	private Long schoolId;
	
	/**
	 * 高校类别id列表(JSON存储)
	 */
	private String schoolTypes;
	
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
	private Integer isDelete;
}