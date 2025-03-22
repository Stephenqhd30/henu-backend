package com.henu.registration.model.vo.operationLog;

import com.henu.registration.model.entity.OperationLog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志表视图
 *
 * @author stephen
 */
@Data
public class OperationLogVO implements Serializable {
	
	private static final long serialVersionUID = 9132689655521294291L;
	/**
	 * id
	 */
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
	 * 创建时间
	 */
	private Date createTime;
	
	
	/**
	 * 封装类转对象
	 *
	 * @param operationLogVO operationLogVO
	 * @return {@link OperationLog}
	 */
	public static OperationLog voToObj(OperationLogVO operationLogVO) {
		if (operationLogVO == null) {
			return null;
		}
		OperationLog operationLog = new OperationLog();
		BeanUtils.copyProperties(operationLogVO, operationLog);
		return operationLog;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param operationLog operationLog
	 * @return {@link OperationLogVO}
	 */
	public static OperationLogVO objToVo(OperationLog operationLog) {
		if (operationLog == null) {
			return null;
		}
		OperationLogVO operationLogVO = new OperationLogVO();
		BeanUtils.copyProperties(operationLog, operationLogVO);
		return operationLogVO;
	}
}
