package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.systemMessages.SystemMessagesAddRequest;
import com.henu.registration.model.dto.systemMessages.SystemMessagesQueryRequest;
import com.henu.registration.model.dto.systemMessages.SystemMessagesUpdateRequest;
import com.henu.registration.model.entity.SystemMessages;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.model.vo.systemMessages.SystemMessagesVO;
import com.henu.registration.service.SystemMessagesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * 系统消息接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/systemMessages")
@Slf4j
public class SystemMessagesController {
	
	@Resource
	private SystemMessagesService systemMessagesService;
	// region 增删改查
	/**
	 * 创建系统消息
	 *
	 * @param systemMessagesAddRequest systemMessagesAddRequest
	 * @param request                  request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addSystemMessages(@RequestBody SystemMessagesAddRequest systemMessagesAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(systemMessagesAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		SystemMessages systemMessages = new SystemMessages();
		BeanUtils.copyProperties(systemMessagesAddRequest, systemMessages);
		// 数据校验
		systemMessagesService.validSystemMessages(systemMessages, true);
		// 写入数据库
		boolean result = systemMessagesService.save(systemMessages);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newSystemMessagesId = systemMessages.getId();
		return ResultUtils.success(newSystemMessagesId);
	}
	
	/**
	 * 删除系统消息
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSystemMessages(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		SystemMessages oldSystemMessages = systemMessagesService.getById(id);
		ThrowUtils.throwIf(oldSystemMessages == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = systemMessagesService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新系统消息（仅管理员可用）
	 *
	 * @param systemMessagesUpdateRequest systemMessagesUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateSystemMessages(@RequestBody SystemMessagesUpdateRequest systemMessagesUpdateRequest) {
		if (systemMessagesUpdateRequest == null || systemMessagesUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		SystemMessages systemMessages = new SystemMessages();
		BeanUtils.copyProperties(systemMessagesUpdateRequest, systemMessages);
		// 数据校验
		systemMessagesService.validSystemMessages(systemMessages, false);
		// 判断是否存在
		long id = systemMessagesUpdateRequest.getId();
		SystemMessages oldSystemMessages = systemMessagesService.getById(id);
		ThrowUtils.throwIf(oldSystemMessages == null, ErrorCode.NOT_FOUND_ERROR);
		// todo 更新推送状态
		systemMessages.setPushStatus(Optional.ofNullable(systemMessages.getPushStatus()).orElse(PushStatusEnum.NOT_PUSHED.getValue()));
		// 操作数据库
		boolean result = systemMessagesService.updateById(systemMessages);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取系统消息（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SystemMessagesVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SystemMessagesVO> getSystemMessagesVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		SystemMessages systemMessages = systemMessagesService.getById(id);
		ThrowUtils.throwIf(systemMessages == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(systemMessagesService.getSystemMessagesVO(systemMessages, request));
	}
	
	/**
	 * 分页获取系统消息列表（仅管理员可用）
	 *
	 * @param systemMessagesQueryRequest systemMessagesQueryRequest
	 * @return {@link BaseResponse<Page<SystemMessages>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<SystemMessages>> listSystemMessagesByPage(@RequestBody SystemMessagesQueryRequest systemMessagesQueryRequest) {
		long current = systemMessagesQueryRequest.getCurrent();
		long size = systemMessagesQueryRequest.getPageSize();
		// 查询数据库
		Page<SystemMessages> systemMessagesPage = systemMessagesService.page(new Page<>(current, size),
				systemMessagesService.getQueryWrapper(systemMessagesQueryRequest));
		return ResultUtils.success(systemMessagesPage);
	}
	
	/**
	 * 分页获取系统消息列表（封装类）
	 *
	 * @param systemMessagesQueryRequest systemMessagesQueryRequest
	 * @param request                    request
	 * @return {@link BaseResponse<Page<SystemMessagesVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SystemMessagesVO>> listSystemMessagesVOByPage(@RequestBody SystemMessagesQueryRequest systemMessagesQueryRequest,
	                                                                       HttpServletRequest request) {
		long current = systemMessagesQueryRequest.getCurrent();
		long size = systemMessagesQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SystemMessages> systemMessagesPage = systemMessagesService.page(new Page<>(current, size),
				systemMessagesService.getQueryWrapper(systemMessagesQueryRequest));
		// 获取封装类
		return ResultUtils.success(systemMessagesService.getSystemMessagesVOPage(systemMessagesPage, request));
	}
	// endregion
}