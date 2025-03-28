package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.MessagePushMapper;
import com.henu.registration.model.dto.messagePush.MessagePushQueryRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.PushTyprEnum;
import com.henu.registration.model.vo.messagePush.MessagePushVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.MessagePushService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * 消息推送服务实现
 * @author stephenqiu
 * @description 针对表【message_push(消息推送表)】的数据库操作Service实现
 * @createDate 2025-03-28 00:11:11
 */
@Service
@Slf4j
public class MessagePushServiceImpl extends ServiceImpl<MessagePushMapper, MessagePush> implements MessagePushService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	/**
	 * 校验数据
	 *
	 * @param messagePush messagePush
	 * @param add         对创建的数据进行校验
	 */
	@Override
	public void validMessagePush(MessagePush messagePush, boolean add) {
		ThrowUtils.throwIf(messagePush == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long messageNoticeId = messagePush.getMessageNoticeId();
		String pushType = messagePush.getPushType();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(messageNoticeId), ErrorCode.PARAMS_ERROR, "消息通知不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(pushType), ErrorCode.PARAMS_ERROR, "推送类型不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(messageNoticeId)) {
			MessageNotice messageNotice = messageNoticeService.getById(messageNoticeId);
			ThrowUtils.throwIf(messageNotice == null, ErrorCode.PARAMS_ERROR, "消息通知不存在");
		}
		if (StringUtils.isNotBlank(pushType)) {
			ThrowUtils.throwIf(PushTyprEnum.getEnumByValue(pushType) == null, ErrorCode.PARAMS_ERROR, "推送类型不存在");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param messagePushQueryRequest messagePushQueryRequest
	 * @return {@link QueryWrapper<MessagePush>}
	 */
	@Override
	public QueryWrapper<MessagePush> getQueryWrapper(MessagePushQueryRequest messagePushQueryRequest) {
		QueryWrapper<MessagePush> queryWrapper = new QueryWrapper<>();
		if (messagePushQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = messagePushQueryRequest.getId();
		Long notId = messagePushQueryRequest.getNotId();
		Long userId = messagePushQueryRequest.getUserId();
		Long messageNoticeId = messagePushQueryRequest.getMessageNoticeId();
		String pushType = messagePushQueryRequest.getPushType();
		Integer pushStatus = messagePushQueryRequest.getPushStatus();
		String pushMessage = messagePushQueryRequest.getPushMessage();
		Integer retryCount = messagePushQueryRequest.getRetryCount();
		String errorMessage = messagePushQueryRequest.getErrorMessage();
		String sortField = messagePushQueryRequest.getSortField();
		String sortOrder = messagePushQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(pushMessage), "push_message", pushMessage);
		queryWrapper.like(StringUtils.isNotBlank(errorMessage), "error_message", errorMessage);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(messageNoticeId), "message_notice_id", messageNoticeId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(pushType), "push_type", pushType);
		queryWrapper.eq(ObjectUtils.isNotEmpty(pushStatus), "push_status", pushStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(retryCount), "retry_count", retryCount);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取消息推送封装
	 *
	 * @param messagePush messagePush
	 * @param request     request
	 * @return {@link MessagePushVO}
	 */
	@Override
	public MessagePushVO getMessagePushVO(MessagePush messagePush, HttpServletRequest request) {
		// 对象转封装类
		MessagePushVO messagePushVO = MessagePushVO.objToVo(messagePush);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = messagePush.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		messagePushVO.setUserVO(userVO);
		
		// endregion
		return messagePushVO;
	}
	
	/**
	 * 分页获取消息推送封装
	 *
	 * @param messagePushPage messagePushPage
	 * @param request         request
	 * @return {@link Page<MessagePushVO>}
	 */
	@Override
	public Page<MessagePushVO> getMessagePushVOPage(Page<MessagePush> messagePushPage, HttpServletRequest request) {
		List<MessagePush> messagePushList = messagePushPage.getRecords();
		Page<MessagePushVO> messagePushVOPage = new Page<>(messagePushPage.getCurrent(), messagePushPage.getSize(), messagePushPage.getTotal());
		if (CollUtil.isEmpty(messagePushList)) {
			return messagePushVOPage;
		}
		// 对象列表 => 封装对象列表
		List<MessagePushVO> messagePushVOList = messagePushList.stream()
				.map(MessagePushVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = messagePushList.stream().map(MessagePush::getUserId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(userIdSet)) {
			CompletableFuture<Map<Long, List<User>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> userService.listByIds(userIdSet).stream()
					.collect(Collectors.groupingBy(User::getId)));
			try {
				Map<Long, List<User>> userIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				messagePushVOList.forEach(messagePushVO -> {
					Long userId = messagePushVO.getUserId();
					User user = null;
					if (userIdUserListMap.containsKey(userId)) {
						user = userIdUserListMap.get(userId).get(0);
					}
					messagePushVO.setUserVO(userService.getUserVO(user, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		messagePushVOPage.setRecords(messagePushVOList);
		return messagePushVOPage;
	}
	
}
