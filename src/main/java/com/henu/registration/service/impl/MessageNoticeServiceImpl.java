package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.controller.MessageNoticeController;
import com.henu.registration.mapper.MessageNoticeMapper;
import com.henu.registration.model.dto.messageNotice.MessageNoticeQueryRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * 消息通知服务实现
 *
 * @author stephenqiu
 * @description 针对表【message_notice(消息通知表)】的数据库操作Service实现
 * @createDate 2025-03-24 00:56:26
 */
@Service
@Slf4j
public class MessageNoticeServiceImpl extends ServiceImpl<MessageNoticeMapper, MessageNotice> implements MessageNoticeService {
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	/**
	 * 校验数据
	 *
	 * @param messageNotice messageNotice
	 * @param add           对创建的数据进行校验
	 */
	@Override
	public void validMessageNotice(MessageNotice messageNotice, boolean add) {
		ThrowUtils.throwIf(messageNotice == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long registrationId = messageNotice.getRegistrationId();
		Date interviewTime = messageNotice.getInterviewTime();
		String interviewLocation = messageNotice.getInterviewLocation();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(registrationId), ErrorCode.PARAMS_ERROR, "报名登记表id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(interviewTime), ErrorCode.PARAMS_ERROR, "面试时间不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(interviewLocation), ErrorCode.PARAMS_ERROR, "面试地点不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(registrationId)) {
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			ThrowUtils.throwIf(registrationForm == null, ErrorCode.PARAMS_ERROR, "报名登记表不存在");
		}
		if (ObjectUtils.isNotEmpty(interviewTime)) {
			ThrowUtils.throwIf(interviewTime.before(new Date()), ErrorCode.PARAMS_ERROR, "面试时间不能早于当前时间");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param messageNoticeQueryRequest messageNoticeQueryRequest
	 * @return {@link QueryWrapper<MessageNotice>}
	 */
	@Override
	public QueryWrapper<MessageNotice> getQueryWrapper(MessageNoticeQueryRequest messageNoticeQueryRequest) {
		QueryWrapper<MessageNotice> queryWrapper = new QueryWrapper<>();
		if (messageNoticeQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = messageNoticeQueryRequest.getId();
		Long notId = messageNoticeQueryRequest.getNotId();
		Long adminId = messageNoticeQueryRequest.getAdminId();
		Date interviewTime = messageNoticeQueryRequest.getInterviewTime();
		String interviewLocation = messageNoticeQueryRequest.getInterviewLocation();
		Integer pushStatus = messageNoticeQueryRequest.getPushStatus();
		Long registrationId = messageNoticeQueryRequest.getRegistrationId();
		String sortField = messageNoticeQueryRequest.getSortField();
		String sortOrder = messageNoticeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(interviewLocation), "interview_location", interviewLocation);
		queryWrapper.like(ObjectUtils.isNotEmpty(interviewTime), "interview_time", interviewTime);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "push_status", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(pushStatus), "push_status", pushStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(registrationId), "registration_id", registrationId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取消息通知封装
	 *
	 * @param messageNotice messageNotice
	 * @param request       request
	 * @return {@link MessageNoticeVO}
	 */
	@Override
	public MessageNoticeVO getMessageNoticeVO(MessageNotice messageNotice, HttpServletRequest request) {
		// 对象转封装类
		MessageNoticeVO messageNoticeVO = MessageNoticeVO.objToVo(messageNotice);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询报名消息信息
		Long registrationId = messageNotice.getRegistrationId();
		RegistrationForm registrationForm = null;
		if (registrationId != null && registrationId > 0) {
			registrationForm = registrationFormService.getById(registrationId);
		}
		RegistrationFormVO registrationFormVO = registrationFormService.getRegistrationFormVO(registrationForm, request);
		messageNoticeVO.setRegistrationFormVO(registrationFormVO);
		
		// endregion
		return messageNoticeVO;
	}
	
	/**
	 * 分页获取消息通知封装
	 *
	 * @param messageNoticePage messageNoticePage
	 * @param request           request
	 * @return {@link Page<MessageNoticeVO>}
	 */
	@Override
	public Page<MessageNoticeVO> getMessageNoticeVOPage(Page<MessageNotice> messageNoticePage, HttpServletRequest request) {
		List<MessageNotice> messageNoticeList = messageNoticePage.getRecords();
		Page<MessageNoticeVO> messageNoticeVOPage = new Page<>(messageNoticePage.getCurrent(), messageNoticePage.getSize(), messageNoticePage.getTotal());
		if (CollUtil.isEmpty(messageNoticeList)) {
			return messageNoticeVOPage;
		}
		// 对象列表 => 封装对象列表
		List<MessageNoticeVO> messageNoticeVOList = messageNoticeList.stream()
				.map(MessageNoticeVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> registerationIdSet = messageNoticeList.stream().map(MessageNotice::getRegistrationId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(registerationIdSet)) {
			CompletableFuture<Map<Long, List<RegistrationForm>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> registrationFormService.listByIds(registerationIdSet).stream()
					.collect(Collectors.groupingBy(RegistrationForm::getId)));
			try {
				Map<Long, List<RegistrationForm>> registerationIdRegistratinListMap = mapCompletableFuture.get();
				// 填充信息
				messageNoticeVOList.forEach(messageNoticeVO -> {
					Long registrationId = messageNoticeVO.getRegistrationId();
					RegistrationForm registrationForm = null;
					if (registerationIdRegistratinListMap.containsKey(registrationId)) {
						registrationForm = registerationIdRegistratinListMap.get(registrationId).get(0);
					}
					messageNoticeVO.setRegistrationFormVO(registrationFormService.getRegistrationFormVO(registrationForm, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		messageNoticeVOPage.setRecords(messageNoticeVOList);
		return messageNoticeVOPage;
	}
	
}
