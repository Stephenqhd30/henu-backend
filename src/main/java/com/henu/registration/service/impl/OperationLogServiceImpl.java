package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.OperationLogMapper;
import com.henu.registration.model.dto.operationLog.OperationLogQueryRequest;
import com.henu.registration.model.entity.OperationLog;
import com.henu.registration.model.vo.operationLog.OperationLogVO;
import com.henu.registration.service.OperationLogService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 操作日志表服务实现
 *
 * @author stephenqiu
 * @description 针对表【operation_log(操作日志表)】的数据库操作Service实现
 * @createDate 2025-03-22 13:53:50
 */
@Service
@Slf4j
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {
	
	/**
	 * 获取查询条件
	 *
	 * @param operationLogQueryRequest operationLogQueryRequest
	 * @return {@link QueryWrapper<OperationLog>}
	 */
	@Override
	public QueryWrapper<OperationLog> getQueryWrapper(OperationLogQueryRequest operationLogQueryRequest) {
		QueryWrapper<OperationLog> queryWrapper = new QueryWrapper<>();
		if (operationLogQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = operationLogQueryRequest.getId();
		Long notId = operationLogQueryRequest.getNotId();
		String requestId = operationLogQueryRequest.getRequestId();
		String requestPath = operationLogQueryRequest.getRequestPath();
		String requestMethod = operationLogQueryRequest.getRequestMethod();
		String requestIp = operationLogQueryRequest.getRequestIp();
		String requestParams = operationLogQueryRequest.getRequestParams();
		Long responseTime = operationLogQueryRequest.getResponseTime();
		String userAgent = operationLogQueryRequest.getUserAgent();
		String sortField = operationLogQueryRequest.getSortField();
		String sortOrder = operationLogQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(requestPath), "request_path", requestPath);
		queryWrapper.like(StringUtils.isNotBlank(requestIp), "request_ip", requestIp);
		queryWrapper.like(StringUtils.isNotBlank(requestParams), "request_params", requestParams);
		queryWrapper.like(StringUtils.isNotBlank(userAgent), "user_agent", userAgent);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(requestId), "request_id", requestId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(requestMethod), "request_method", requestMethod);
		queryWrapper.eq(ObjectUtils.isNotEmpty(requestMethod), "request_method", requestMethod);
		queryWrapper.eq(ObjectUtils.isNotEmpty(responseTime), "response_time", responseTime);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取操作日志表封装
	 *
	 * @param operationLog operationLog
	 * @param request      request
	 * @return {@link OperationLogVO}
	 */
	@Override
	public OperationLogVO getOperationLogVO(OperationLog operationLog, HttpServletRequest request) {
		// 对象转封装类
		return OperationLogVO.objToVo(operationLog);
	}
	
	/**
	 * 分页获取操作日志表封装
	 *
	 * @param operationLogPage operationLogPage
	 * @param request          request
	 * @return {@link Page<OperationLogVO>}
	 */
	@Override
	public Page<OperationLogVO> getOperationLogVOPage(Page<OperationLog> operationLogPage, HttpServletRequest request) {
		List<OperationLog> operationLogList = operationLogPage.getRecords();
		Page<OperationLogVO> operationLogVOPage = new Page<>(operationLogPage.getCurrent(), operationLogPage.getSize(), operationLogPage.getTotal());
		if (CollUtil.isEmpty(operationLogList)) {
			return operationLogVOPage;
		}
		// 对象列表 => 封装对象列表
		List<OperationLogVO> operationLogVOList = operationLogList.stream()
				.map(OperationLogVO::objToVo)
				.collect(Collectors.toList());
		operationLogVOPage.setRecords(operationLogVOList);
		return operationLogVOPage;
	}
	
}
