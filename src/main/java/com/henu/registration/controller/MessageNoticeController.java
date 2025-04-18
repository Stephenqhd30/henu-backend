package com.henu.registration.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.messageNotice.MessageNoticeAddRequest;
import com.henu.registration.model.dto.messageNotice.MessageNoticeQueryRequest;
import com.henu.registration.model.dto.messageNotice.MessageNoticeUpdateRequest;
import com.henu.registration.model.entity.*;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.model.enums.PushTypeEnum;
import com.henu.registration.model.enums.RegistrationStatueEnum;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;
import com.henu.registration.service.*;
import com.henu.registration.utils.rabbitmq.RabbitMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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
	
	@Resource
	private UserService userService;
	
	@Resource
	private MessagePushService messagePushService;
	
	// region 增删改查
	
	/**
	 * 创建消息通知
	 *
	 * @param messageNoticeAddRequest messageNoticeAddRequest
	 * @param request                 request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Long> addMessageNotice(@RequestBody MessageNoticeAddRequest messageNoticeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messageNoticeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		MessageNotice messageNotice = new MessageNotice();
		BeanUtils.copyProperties(messageNoticeAddRequest, messageNotice);
		// 数据校验
		messageNoticeService.validMessageNotice(messageNotice, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		messageNotice.setAdminId(loginAdmin.getId());
		Long registrationId = messageNoticeAddRequest.getRegistrationId();
		RegistrationForm registrationForm = registrationFormService.getById(registrationId);
		ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR, "报名信息不存在");
		Long userId = registrationForm.getUserId();
		User user = userService.getById(userId);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		messageNotice.setUserId(userId);
		messageNotice.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
		// 写入数据库
		boolean result = messageNoticeService.saveOrUpdate(messageNotice);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 同步修改登记表的审核状态信息
		boolean update = registrationFormService.lambdaUpdate()
				.eq(RegistrationForm::getId, registrationId)
				// 如果审核状态为通过，设置为“面试阶段”
				.set(RegistrationForm::getRegistrationStatus, RegistrationStatueEnum.INTERVIEW.getValue())
				.update();
		ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
		// 创建消息推送记录（短信）
		MessagePush messagePush = new MessagePush();
		messagePush.setUserId(registrationForm.getUserId());
		messagePush.setMessageNoticeId(messageNotice.getId());
		messagePush.setUserName(user.getUserName());
		messagePush.setPushType(PushTypeEnum.SMS.getValue());
		messagePush.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
		boolean save = messagePushService.save(messagePush);
		ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
		// 发送 MQ 消息
		CompletableFuture.runAsync(() -> {
			log.info("异步发送面试通知短信：{}", messagePush);
			RabbitMqUtils.defaultSendMsg(messagePush);
		});
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
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<List<Long>> addMessageNoticeByBatch(@RequestBody MessageNoticeAddRequest messageNoticeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messageNoticeAddRequest == null || CollectionUtils.isEmpty(messageNoticeAddRequest.getRegistrationIds()), ErrorCode.PARAMS_ERROR);
		Admin loginAdmin = adminService.getLoginAdmin(request);
		List<MessageNotice> messageNotices = new ArrayList<>();
		List<MessagePush> messagePushList = new ArrayList<>();
		for (Long registrationId : messageNoticeAddRequest.getRegistrationIds()) {
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR, "报名登记表不存在");
			User user = userService.getById(registrationForm.getUserId());
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
			// 构造通知
			MessageNotice messageNotice = new MessageNotice();
			BeanUtils.copyProperties(messageNoticeAddRequest, messageNotice);
			messageNotice.setRegistrationId(registrationId);
			messageNotice.setAdminId(loginAdmin.getId());
			messageNotice.setUserId(user.getId());
			messageNotice.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
			messageNoticeService.validMessageNotice(messageNotice, true);
			messageNotices.add(messageNotice);
		}
		// 写入数据库
		boolean result = messageNoticeService.saveOrUpdateBatch(messageNotices);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 批量更新报名状态为“面试阶段”
		boolean update = registrationFormService.lambdaUpdate()
				.in(RegistrationForm::getId, messageNoticeAddRequest.getRegistrationIds())
				.set(RegistrationForm::getRegistrationStatus, RegistrationStatueEnum.INTERVIEW.getValue())
				.update();
		ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
		// 构造并写入 MessagePush 消息推送记录
		for (MessageNotice notice : messageNotices) {
			User user = userService.getById(notice.getUserId());
			MessagePush messagePush = new MessagePush();
			messagePush.setUserId(notice.getUserId());
			messagePush.setUserName(user.getUserName());
			messagePush.setMessageNoticeId(notice.getId());
			messagePush.setPushType(PushTypeEnum.SMS.getValue());
			messagePush.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
			messagePushList.add(messagePush);
		}
		boolean pushSaveResult = messagePushService.saveBatch(messagePushList);
		ThrowUtils.throwIf(!pushSaveResult, ErrorCode.OPERATION_ERROR);
		// 异步 MQ 推送
		messagePushList.forEach(messagePush ->
				CompletableFuture.runAsync(() -> {
					log.info("异步发送面试通知短信：{}", messagePush);
					RabbitMqUtils.defaultSendMsg(messagePush);
				})
		);
		// 返回 ID 列表
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
	 * 分页获取当前用户的消息通知列表
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
		User loginUser = userService.getLoginUser(request);
		messageNoticeQueryRequest.setUserId(loginUser.getId());
		// 设置推送状态为成功
		messageNoticeQueryRequest.setPushStatus(PushStatusEnum.SUCCEED.getValue());
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