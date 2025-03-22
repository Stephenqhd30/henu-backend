package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.operationLog.OperationLogQueryRequest;
import com.henu.registration.model.entity.OperationLog;
import com.henu.registration.model.vo.operationLog.OperationLogVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志表服务
 *
 * @author stephenqiu
 * @description 针对表【operation_log(操作日志表)】的数据库操作Service
 * @createDate 2025-03-22 13:53:50
 */
public interface OperationLogService extends IService<OperationLog> {
	
	
	/**
	 * 获取查询条件
	 *
	 * @param operationLogQueryRequest operationLogQueryRequest
	 * @return {@link QueryWrapper<OperationLog>}
	 */
	QueryWrapper<OperationLog> getQueryWrapper(OperationLogQueryRequest operationLogQueryRequest);
	
	/**
	 * 获取操作日志表封装
	 *
	 * @param operationLog operationLog
	 * @param request      request
	 * @return {@link OperationLogVO}
	 */
	OperationLogVO getOperationLogVO(OperationLog operationLog, HttpServletRequest request);
	
	/**
	 * 分页获取操作日志表封装
	 *
	 * @param operationLogPage operationLogPage
	 * @param request          request
	 * @return {@link Page<OperationLogVO>}
	 */
	Page<OperationLogVO> getOperationLogVOPage(Page<OperationLog> operationLogPage, HttpServletRequest request);
}