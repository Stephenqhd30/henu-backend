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
 * 教育经历表
 *
 * @author stephenqiu
 * @TableName education
 */
@TableName(value = "education")
@Data
public class Education implements Serializable {
	@Serial
	private static final long serialVersionUID = 191180529959535080L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 高校编号
	 */
	private Long schoolId;
	
	/**
	 * 教育阶段
	 */
	private String educationalStage;
	
	/**
	 * 专业
	 */
	private String major;
	
	/**
	 * 学习起止年月
	 */
	private String studyTime;
	
	/**
	 * 证明人
	 */
	private String certifier;
	
	/**
	 * 证明人联系电话
	 */
	private String certifierPhone;
	
	/**
	 * 用户id
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
	 * 是否逻辑删除
	 */
	@TableLogic
	private Integer isDelete;
}