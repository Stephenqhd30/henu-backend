package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.message.MessageAddRequest;
import com.henu.registration.model.dto.message.MessageQueryRequest;
import com.henu.registration.model.dto.message.MessageUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Message;
import com.henu.registration.model.vo.message.MessageVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 消息通知接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {
	
	@Resource
	private MessageService messageService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建消息通知
	 *
	 * @param messageAddRequest messageAddRequest
	 * @param request           request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addMessage(@RequestBody MessageAddRequest messageAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messageAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Message message = new Message();
		BeanUtils.copyProperties(messageAddRequest, message);
		// 数据校验
		messageService.validMessage(message, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		message.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = messageService.save(message);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newMessageId = message.getId();
		return ResultUtils.success(newMessageId);
	}
	
	/**
	 * 删除消息通知
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteMessage(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Message oldMessage = messageService.getById(id);
		ThrowUtils.throwIf(oldMessage == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldMessage.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = messageService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新消息通知（仅管理员可用）
	 *
	 * @param messageUpdateRequest messageUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateMessage(@RequestBody MessageUpdateRequest messageUpdateRequest) {
		if (messageUpdateRequest == null || messageUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Message message = new Message();
		BeanUtils.copyProperties(messageUpdateRequest, message);
		// 数据校验
		messageService.validMessage(message, false);
		
		// 判断是否存在
		long id = messageUpdateRequest.getId();
		Message oldMessage = messageService.getById(id);
		ThrowUtils.throwIf(oldMessage == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = messageService.updateById(message);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取消息通知（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<MessageVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<MessageVO> getMessageVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Message message = messageService.getById(id);
		ThrowUtils.throwIf(message == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(messageService.getMessageVO(message, request));
	}
	
	/**
	 * 分页获取消息通知列表（仅管理员可用）
	 *
	 * @param messageQueryRequest messageQueryRequest
	 * @return {@link BaseResponse<Page<Message>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<Message>> listMessageByPage(@RequestBody MessageQueryRequest messageQueryRequest) {
		long current = messageQueryRequest.getCurrent();
		long size = messageQueryRequest.getPageSize();
		// 查询数据库
		Page<Message> messagePage = messageService.page(new Page<>(current, size),
				messageService.getQueryWrapper(messageQueryRequest));
		return ResultUtils.success(messagePage);
	}
	
	/**
	 * 分页获取消息通知列表（封装类）
	 *
	 * @param messageQueryRequest messageQueryRequest
	 * @param request             request
	 * @return {@link BaseResponse<Page<MessageVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<MessageVO>> listMessageVOByPage(@RequestBody MessageQueryRequest messageQueryRequest,
	                                                         HttpServletRequest request) {
		long current = messageQueryRequest.getCurrent();
		long size = messageQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Message> messagePage = messageService.page(new Page<>(current, size),
				messageService.getQueryWrapper(messageQueryRequest));
		// 获取封装类
		return ResultUtils.success(messageService.getMessageVOPage(messagePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的消息通知列表
	 *
	 * @param messageQueryRequest messageQueryRequest
	 * @param request             request
	 * @return {@link BaseResponse<Page<MessageVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<MessageVO>> listMyMessageVOByPage(@RequestBody MessageQueryRequest messageQueryRequest,
	                                                           HttpServletRequest request) {
		ThrowUtils.throwIf(messageQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		messageQueryRequest.setAdminId(loginAdmin.getId());
		long current = messageQueryRequest.getCurrent();
		long size = messageQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Message> messagePage = messageService.page(new Page<>(current, size),
				messageService.getQueryWrapper(messageQueryRequest));
		// 获取封装类
		return ResultUtils.success(messageService.getMessageVOPage(messagePage, request));
	}
	
	// endregion
}