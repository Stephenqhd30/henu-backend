package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.DeleteRequest;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.constants.UserConstant;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.model.dto.messagePush.MessagePushAddRequest;
import com.henu.registration.model.dto.messagePush.MessagePushQueryRequest;
import com.henu.registration.model.dto.messagePush.MessagePushUpdateRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.messagePush.MessagePushVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.MessagePushService;
import com.henu.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
	private AdminService adminService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	// region 增删改查
	
	/**
	 * 创建消息推送
	 *
	 * @param messagePushAddRequest messagePushAddRequest
	 * @param request               request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addMessagePush(@RequestBody MessagePushAddRequest messagePushAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(messagePushAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		MessagePush messagePush = new MessagePush();
		BeanUtils.copyProperties(messagePushAddRequest, messagePush);
		// 数据校验
		messagePushService.validMessagePush(messagePush, true);
		// todo 填充默认值
		MessageNotice messageNotice = messageNoticeService.getById(messagePush.getMessageNoticeId());
		messagePush.setUserId(messagePush.getUserId());
		messagePush.setPushMessage(messageNotice.getContent());
		// 写入数据库
		boolean result = messagePushService.save(messagePush);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newMessagePushId = messagePush.getId();
		return ResultUtils.success(newMessagePushId);
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
		// 仅本人或管理员可删除
		if (!adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = messagePushService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新消息推送（仅管理员可用）
	 *
	 * @param messagePushUpdateRequest messagePushUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateMessagePush(@RequestBody MessagePushUpdateRequest messagePushUpdateRequest) {
		if (messagePushUpdateRequest == null || messagePushUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		MessagePush messagePush = new MessagePush();
		BeanUtils.copyProperties(messagePushUpdateRequest, messagePush);
		// 数据校验
		messagePushService.validMessagePush(messagePush, false);
		// 判断是否存在
		long id = messagePushUpdateRequest.getId();
		MessagePush oldMessagePush = messagePushService.getById(id);
		ThrowUtils.throwIf(oldMessagePush == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = messagePushService.updateById(messagePush);
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
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
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