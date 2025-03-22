package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 文件上传类型表
 *
 * @author stephenqiu
 * @TableName file_type
 */
@TableName(value = "file_type")
@Data
public class FileType implements Serializable {
	private static final long serialVersionUID = -8643178839468158058L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 文件上传类型名称
	 */
	private String typeName;
	
	/**
	 * 文件上传类型值(JSON如['jpg','png'])
	 */
	private String typeValues;
	
	/**
	 * 最大可上传文件大小（字节）
	 */
	private Long maxFileSize;
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	/**
	 * 记录创建时间
	 */
	private Date createTime;
	
	/**
	 * 记录更新时间
	 */
	private Date updateTime;
	
	/**
	 * 是否逻辑删除
	 */
	@TableLogic
	private Integer isDelete;
}