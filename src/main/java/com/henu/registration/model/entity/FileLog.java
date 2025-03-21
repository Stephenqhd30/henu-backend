package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

import lombok.Data;

/**
 * 文件上传日志表
 *
 * @author stephenqiu
 * @TableName file_log
 */
@TableName(value = "file_log")
@Data
public class FileLog {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 附件类型编号
	 */
	private String fileType;
	
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