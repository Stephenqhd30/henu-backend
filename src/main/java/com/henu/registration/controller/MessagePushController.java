package com.henu.registration.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.messagePush.MessagePushAddRequest;
import com.henu.registration.model.dto.messagePush.MessagePushQueryRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.PushStatusEnum;
import com.henu.registration.model.vo.messagePush.MessagePushVO;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.MessagePushService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.rabbitmq.RabbitMqUtils;
import com.henu.registration.utils.redisson.KeyPrefixConstants;
import com.henu.registration.utils.redisson.lock.LockUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 消息推送接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/messagePush")
@Slf4j
public class MessagePushController {
	
	@Resource
	private MessagePushService messagePushService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	// region 增删改查
	
	/**
	 * 创建消息推送
	 *
	 * @param messagePushAddRequest messagePushAddRequest
	 * @param request               request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@Transactional
	public BaseResponse<Long> addMessagePush(@RequestBody MessagePushAddRequest messagePushAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messagePushAddRequest == null, ErrorCode.PARAMS_ERROR);
		
		MessagePush messagePush = new MessagePush();
		BeanUtils.copyProperties(messagePushAddRequest, messagePush);
		messagePushService.validMessagePush(messagePush, true);
		String lockKey = KeyPrefixConstants.RATE_LIMIT_ANNOTATION_PREFIX + messagePush.getMessageNoticeId();
		return LockUtils.lockEvent(lockKey, () -> {
			LambdaQueryWrapper<MessagePush> eq = Wrappers.lambdaQuery(MessagePush.class)
					.eq(MessagePush::getMessageNoticeId, messagePush.getMessageNoticeId())
					.eq(MessagePush::getPushStatus, PushStatusEnum.SUCCEED.getValue())
					.eq(MessagePush::getPushType, messagePushAddRequest.getPushType())
					.eq(MessagePush::getUserId, messagePush.getUserId());
			MessagePush oldMessagePush = messagePushService.getOne(eq);
			ThrowUtils.throwIf(oldMessagePush != null, ErrorCode.PARAMS_ERROR, "该消息已经推送成功");
			
			// 获取通知信息
			MessageNotice messageNotice = messageNoticeService.getById(messagePush.getMessageNoticeId());
			ThrowUtils.throwIf(messageNotice == null, ErrorCode.NOT_FOUND_ERROR, "面试通知不存在");
			
			// 获取报名信息
			Long registrationId = messageNotice.getRegistrationId();
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR, "报名信息不存在");
			// 获取用户信息并填充
			Long userId = registrationForm.getUserId();
			messagePush.setUserId(userId);
			messagePush.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
			messagePush.setUserName(registrationForm.getUserName());
			// 保存 messagePush 记录
			boolean saveResult = messagePushService.save(messagePush);
			ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
			// 异步发送消息到 RabbitMQ，避免阻塞主线程
			CompletableFuture.runAsync(() -> {
				log.info("异步发送消息到 RabbitMQ: {}", messagePush);
				RabbitMqUtils.defaultSendMsg(messagePush);
			});
			return ResultUtils.success(messagePush.getId());
		}, () -> {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "推送消息处理中，请稍后重试");
		});
	}
	
	/**
	 * 批量创建消息推送
	 *
	 * @param messagePushAddRequest messagePushAddRequest
	 * @param request               HttpServletRequest
	 * @return {@link BaseResponse<List<Long>>}
	 */
	@PostMapping("/add/batch")
	@Transactional
	public BaseResponse<List<Long>> addMessagePushByIds(@RequestBody MessagePushAddRequest messagePushAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messagePushAddRequest == null || CollUtil.isEmpty(messagePushAddRequest.getMessageNoticeIds()), ErrorCode.PARAMS_ERROR, "通知ID列表不能为空");
		
		List<Long> noticeIds = messagePushAddRequest.getMessageNoticeIds();
		List<MessagePush> pushListToSave = new ArrayList<>();
		List<Long> idList = new ArrayList<>();
		for (Long noticeId : noticeIds) {
			// 封装每个推送对象
			MessageNotice messageNotice = messageNoticeService.getById(noticeId);
			if (messageNotice == null) {
				log.warn("通知不存在，跳过，noticeId={}", noticeId);
				continue;
			}
			RegistrationForm form = registrationFormService.getById(messageNotice.getRegistrationId());
			if (form == null) {
				log.warn("报名信息不存在，跳过，registrationId={}", messageNotice.getRegistrationId());
				continue;
			}
			Long userId = form.getUserId();
			// 判断是否已成功推送
			boolean alreadyPushed = messagePushService.count(
					Wrappers.lambdaQuery(MessagePush.class)
							.eq(MessagePush::getMessageNoticeId, noticeId)
							.eq(MessagePush::getPushStatus, PushStatusEnum.SUCCEED.getValue())
							.eq(MessagePush::getPushType, messagePushAddRequest.getPushType())
							.eq(MessagePush::getUserId, userId)
			) > 0;
			if (alreadyPushed) {
				log.info("通知 {} 已经成功推送给用户 {}，跳过", noticeId, userId);
				continue;
			}
			MessagePush push = new MessagePush();
			push.setMessageNoticeId(noticeId);
			push.setPushType(messagePushAddRequest.getPushType());
			push.setUserId(userId);
			push.setUserName(form.getUserName());
			push.setPushStatus(PushStatusEnum.NOT_PUSHED.getValue());
			// 校验基本合法性
			messagePushService.validMessagePush(push, true);
			pushListToSave.add(push);
		}
		// 保存并异步推送
		if (CollUtil.isNotEmpty(pushListToSave)) {
			boolean saved = messagePushService.saveBatch(pushListToSave);
			ThrowUtils.throwIf(!saved, ErrorCode.OPERATION_ERROR, "保存失败");
			pushListToSave.forEach(push -> {
				idList.add(push.getId());
				CompletableFuture.runAsync(() -> {
					log.info("异步发送 MQ 消息: {}", push);
					RabbitMqUtils.defaultSendMsg(push);
				});
			});
		}
		return ResultUtils.success(idList);
	}
	
	/**
	 * 删除消息推送
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteMessagePush(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		MessagePush oldMessagePush = messagePushService.getById(id);
		ThrowUtils.throwIf(oldMessagePush == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = messagePushService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取消息推送（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<MessagePushVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<MessagePushVO> getMessagePushVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		MessagePush messagePush = messagePushService.getById(id);
		ThrowUtils.throwIf(messagePush == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(messagePushService.getMessagePushVO(messagePush, request));
	}
	
	/**
	 * 分页获取消息推送列表（仅管理员可用）
	 *
	 * @param messagePushQueryRequest messagePushQueryRequest
	 * @return {@link BaseResponse<Page<MessagePush>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<MessagePush>> listMessagePushByPage(@RequestBody MessagePushQueryRequest messagePushQueryRequest) {
		long current = messagePushQueryRequest.getCurrent();
		long size = messagePushQueryRequest.getPageSize();
		// 查询数据库
		Page<MessagePush> messagePushPage = messagePushService.page(new Page<>(current, size),
				messagePushService.getQueryWrapper(messagePushQueryRequest));
		return ResultUtils.success(messagePushPage);
	}
	
	/**
	 * 分页获取消息推送列表（封装类）
	 *
	 * @param messagePushQueryRequest messagePushQueryRequest
	 * @param request                 request
	 * @return {@link BaseResponse<Page<MessagePushVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<MessagePushVO>> listMessagePushVOByPage(@RequestBody MessagePushQueryRequest messagePushQueryRequest,
	                                                                 HttpServletRequest request) {
		long current = messagePushQueryRequest.getCurrent();
		long size = messagePushQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<MessagePush> messagePushPage = messagePushService.page(new Page<>(current, size),
				messagePushService.getQueryWrapper(messagePushQueryRequest));
		// 获取封装类
		return ResultUtils.success(messagePushService.getMessagePushVOPage(messagePushPage, request));
	}
	
	/**
	 * 分页获取用户收到的消息推送列表
	 *
	 * @param messagePushQueryRequest messagePushQueryRequest
	 * @param request                 request
	 * @return {@link BaseResponse<Page<MessagePushVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<MessagePushVO>> listMyMessagePushVOByPage(@RequestBody MessagePushQueryRequest messagePushQueryRequest,
	                                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(messagePushQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		messagePushQueryRequest.setUserId(loginUser.getId());
		long current = messagePushQueryRequest.getCurrent();
		long size = messagePushQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<MessagePush> messagePushPage = messagePushService.page(new Page<>(current, size),
				messagePushService.getQueryWrapper(messagePushQueryRequest));
		// 获取封装类
		return ResultUtils.success(messagePushService.getMessagePushVOPage(messagePushPage, request));
	}
	// endregion
}