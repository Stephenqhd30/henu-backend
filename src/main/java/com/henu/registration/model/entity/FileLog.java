package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件上传日志表
 *
 * @author stephenqiu
 * @TableName file_log
 */
@TableName(value = "file_log")
@Data
public class FileLog implements Serializable {
	@Serial
	private static final long serialVersionUID = -7463232804253920899L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 附件类型id
	 */
	private Long fileTypeId;
	
	/**
	 * 附件名称
	 */
	private String fileName;
	
	/**
	 * 附件存储路径
	 */
	private String filePath;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
}