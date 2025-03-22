package com.henu.registration.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 操作日志表
 *
 * @author stephenqiu
 * @TableName operation_log
 */
@TableName(value = "operation_log")
@Data
public class OperationLog implements Serializable {
	private static final long serialVersionUID = 7816545715270748607L;
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 请求唯一id
	 */
	private String requestId;
	
	/**
	 * 请求路径
	 */
	private String requestPath;
	
	/**
	 * 请求方法（GET, POST等）
	 */
	private String requestMethod;
	
	/**
	 * 请求IP地址
	 */
	private String requestIp;
	
	/**
	 * 请求参数
	 */
	private String requestParams;
	
	/**
	 * 响应时间（毫秒）
	 */
	private Long responseTime;
	
	/**
	 * 用户代理（浏览器信息）
	 */
	private String userAgent;
	
	/**
	 * 操作时间
	 */
	private Date createTime;
	
	/**
	 * 是否逻辑删除
	 */
	@TableLogic
	private Integer isDelete;
}