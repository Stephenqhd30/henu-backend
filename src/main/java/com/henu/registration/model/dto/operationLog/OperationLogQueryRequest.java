package com.henu.registration.model.dto.operationLog;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询操作日志表请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OperationLogQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索词
	 */
	private String searchText;
	
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
	
	private static final long serialVersionUID = 1L;
}