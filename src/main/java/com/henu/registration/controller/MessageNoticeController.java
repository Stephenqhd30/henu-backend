package com.henu.registration.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.messageNotice.MessageNoticeAddRequest;
import com.henu.registration.model.dto.messageNotice.MessageNoticeQueryRequest;
import com.henu.registration.model.dto.messageNotice.MessageNoticeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.RegistrationFormService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息通知接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/messageNotice")
@Slf4j
public class MessageNoticeController {
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	
	// region 增删改查
	
	/**
	 * 创建消息通知
	 *
	 * @param messageNoticeAddRequest messageNoticeAddRequest
	 * @param request                 request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addMessageNotice(@RequestBody MessageNoticeAddRequest messageNoticeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messageNoticeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		MessageNotice messageNotice = new MessageNotice();
		BeanUtils.copyProperties(messageNoticeAddRequest, messageNotice);
		// 数据校验
		messageNoticeService.validMessageNotice(messageNotice, true);
		// 检查消息通知是够已经被创建
		MessageNotice oldMessageNotice = messageNoticeService.getOne(Wrappers.lambdaQuery(MessageNotice.class)
				.eq(MessageNotice::getRegistrationId, messageNotice.getRegistrationId()));
		if (oldMessageNotice != null) {
			messageNotice.setId(oldMessageNotice.getId());
		}
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		messageNotice.setAdminId(loginAdmin.getId());
		Long registrationId = messageNoticeAddRequest.getRegistrationId();
		RegistrationForm registrationForm = registrationFormService.getById(registrationId);
		messageNotice.setUserName(registrationForm.getUserName());
		messageNotice.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
		// 写入数据库
		boolean result = messageNoticeService.saveOrUpdate(messageNotice);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newMessageNoticeId = messageNotice.getId();
		return ResultUtils.success(newMessageNoticeId);
	}
	
	/**
	 * 批量创建消息通知
	 *
	 * @param messageNoticeAddRequest messageNoticeAddRequest
	 * @param request                 request
	 * @return {@link BaseResponse<List<Long>>}
	 */
	@PostMapping("/add/batch")
	public BaseResponse<List<Long>> addMessageNoticeByBatch(@RequestBody MessageNoticeAddRequest messageNoticeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messageNoticeAddRequest == null, ErrorCode.PARAMS_ERROR);
		List<MessageNotice> messageNotices = new ArrayList<>();
		
		Admin loginAdmin = adminService.getLoginAdmin(request);
		for (Long registrationId : messageNoticeAddRequest.getRegistrationIds()) {
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR, "报名登记表不存在");
			// todo 填充默认值
			MessageNotice messageNotice = new MessageNotice();
			BeanUtils.copyProperties(messageNoticeAddRequest, messageNotice);
			messageNotice.setRegistrationId(registrationId);
			messageNotice.setAdminId(loginAdmin.getId());
			messageNotice.setUserName(registrationForm.getUserName());
			messageNotice.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
			messageNoticeService.validMessageNotice(messageNotice, true);
			MessageNotice oldMessageNotice = messageNoticeService.getOne(Wrappers.lambdaQuery(MessageNotice.class)
					.eq(MessageNotice::getRegistrationId, registrationId));
			// 检查消息通知是够已经被创建
			if (oldMessageNotice != null) {
				messageNotice.setId(oldMessageNotice.getId());
			}
			messageNotices.add(messageNotice);
		}
		// 写入数据库
		boolean result = messageNoticeService.saveOrUpdateBatch(messageNotices);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		List<Long> savedIds = messageNotices.stream()
				.map(MessageNotice::getId)
				.collect(Collectors.toList());
		return ResultUtils.success(savedIds);
	}
	
	/**
	 * 删除消息通知
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteMessageNotice(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		MessageNotice oldMessageNotice = messageNoticeService.getById(id);
		ThrowUtils.throwIf(oldMessageNotice == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldMessageNotice.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = messageNoticeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新消息通知（仅管理员可用）
	 *
	 * @param messageNoticeUpdateRequest messageNoticeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateMessageNotice(@RequestBody MessageNoticeUpdateRequest messageNoticeUpdateRequest) {
		if (messageNoticeUpdateRequest == null || messageNoticeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		MessageNotice messageNotice = new MessageNotice();
		BeanUtils.copyProperties(messageNoticeUpdateRequest, messageNotice);
		// 数据校验
		messageNoticeService.validMessageNotice(messageNotice, false);
		// 判断是否存在
		long id = messageNoticeUpdateRequest.getId();
		MessageNotice oldMessageNotice = messageNoticeService.getById(id);
		ThrowUtils.throwIf(oldMessageNotice == null, ErrorCode.NOT_FOUND_ERROR);
		// 更新数据为推送状态
		messageNotice.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
		// 操作数据库
		boolean result = messageNoticeService.updateById(messageNotice);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取消息通知（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<MessageNoticeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<MessageNoticeVO> getMessageNoticeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		MessageNotice messageNotice = messageNoticeService.getById(id);
		ThrowUtils.throwIf(messageNotice == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(messageNoticeService.getMessageNoticeVO(messageNotice, request));
	}
	
	/**
	 * 分页获取消息通知列表
	 *
	 * @param messageNoticeQueryRequest messageNoticeQueryRequest
	 * @return {@link BaseResponse<Page<MessageNotice>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<MessageNotice>> listMessageNoticeByPage(@RequestBody MessageNoticeQueryRequest messageNoticeQueryRequest) {
		long current = messageNoticeQueryRequest.getCurrent();
		long size = messageNoticeQueryRequest.getPageSize();
		// 查询数据库
		Page<MessageNotice> messageNoticePage = messageNoticeService.page(new Page<>(current, size),
				messageNoticeService.getQueryWrapper(messageNoticeQueryRequest));
		return ResultUtils.success(messageNoticePage);
	}
	
	/**
	 * 分页获取消息通知列表（封装类）
	 *
	 * @param messageNoticeQueryRequest messageNoticeQueryRequest
	 * @param request                   request
	 * @return {@link BaseResponse<Page<MessageNoticeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<MessageNoticeVO>> listMessageNoticeVOByPage(@RequestBody MessageNoticeQueryRequest messageNoticeQueryRequest,
	                                                                     HttpServletRequest request) {
		long current = messageNoticeQueryRequest.getCurrent();
		long size = messageNoticeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<MessageNotice> messageNoticePage = messageNoticeService.page(new Page<>(current, size),
				messageNoticeService.getQueryWrapper(messageNoticeQueryRequest));
		// 获取封装类
		return ResultUtils.success(messageNoticeService.getMessageNoticeVOPage(messageNoticePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的消息通知列表
	 *
	 * @param messageNoticeQueryRequest messageNoticeQueryRequest
	 * @param request                   request
	 * @return {@link BaseResponse<Page<MessageNoticeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<MessageNoticeVO>> listMyMessageNoticeVOByPage(@RequestBody MessageNoticeQueryRequest messageNoticeQueryRequest,
	                                                                       HttpServletRequest request) {
		ThrowUtils.throwIf(messageNoticeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		messageNoticeQueryRequest.setAdminId(loginAdmin.getId());
		long current = messageNoticeQueryRequest.getCurrent();
		long size = messageNoticeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<MessageNotice> messageNoticePage = messageNoticeService.page(new Page<>(current, size),
				messageNoticeService.getQueryWrapper(messageNoticeQueryRequest));
		// 获取封装类
		return ResultUtils.success(messageNoticeService.getMessageNoticeVOPage(messageNoticePage, request));
	}
	// endregion
}