package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.model.dto.operationLog.OperationLogQueryRequest;
import com.henu.registration.model.entity.OperationLog;
import com.henu.registration.model.vo.operationLog.OperationLogVO;
import com.henu.registration.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 操作日志表接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/operationLog")
@Slf4j
public class OperationLogController {
	
	@Resource
	private OperationLogService operationLogService;
	
	// region 增删改查
	
	/**
	 * 根据 id 获取操作日志表（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<OperationLogVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<OperationLogVO> getOperationLogVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		OperationLog operationLog = operationLogService.getById(id);
		ThrowUtils.throwIf(operationLog == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(operationLogService.getOperationLogVO(operationLog, request));
	}
	
	/**
	 * 分页获取操作日志表列表（仅管理员可用）
	 *
	 * @param operationLogQueryRequest operationLogQueryRequest
	 * @return {@link BaseResponse<Page<OperationLog>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<OperationLog>> listOperationLogByPage(@RequestBody OperationLogQueryRequest operationLogQueryRequest) {
		long current = operationLogQueryRequest.getCurrent();
		long size = operationLogQueryRequest.getPageSize();
		// 查询数据库
		Page<OperationLog> operationLogPage = operationLogService.page(new Page<>(current, size),
				operationLogService.getQueryWrapper(operationLogQueryRequest));
		return ResultUtils.success(operationLogPage);
	}
	
	/**
	 * 分页获取操作日志表列表（封装类）
	 *
	 * @param operationLogQueryRequest operationLogQueryRequest
	 * @param request                  request
	 * @return {@link BaseResponse<Page<OperationLogVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<OperationLogVO>> listOperationLogVOByPage(@RequestBody OperationLogQueryRequest operationLogQueryRequest,
	                                                                   HttpServletRequest request) {
		long current = operationLogQueryRequest.getCurrent();
		long size = operationLogQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<OperationLog> operationLogPage = operationLogService.page(new Page<>(current, size),
				operationLogService.getQueryWrapper(operationLogQueryRequest));
		// 获取封装类
		return ResultUtils.success(operationLogService.getOperationLogVOPage(operationLogPage, request));
	}
	
	// endregion
}