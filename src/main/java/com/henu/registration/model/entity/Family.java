package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 家庭关系表
 *
 * @author stephenqiu
 * @TableName family
 */
@TableName(value = "family")
@Data
public class Family implements Serializable {
	@Serial
	private static final long serialVersionUID = -6885927370388853335L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 称谓
	 */
	private String appellation;
	
	/**
	 * 姓名
	 */
	private String familyName;
	
	/**
	 * 工作单位及职务
	 */
	private String workDetail;
	
	/**
	 * 创建用户id
	 */
	private Long userId;
	
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