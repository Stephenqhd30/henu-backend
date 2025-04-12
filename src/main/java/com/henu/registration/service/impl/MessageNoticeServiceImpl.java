package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.MessageNoticeMapper;
import com.henu.registration.model.dto.messageNotice.MessageNoticeQueryRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;
import com.henu.registration.service.MessageNoticeService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
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
		String content = messageNotice.getContent();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(registrationId), ErrorCode.PARAMS_ERROR, "报名登记表id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(content), ErrorCode.PARAMS_ERROR, "面试内容不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(registrationId)) {
			RegistrationForm registrationForm = registrationFormService.getById(registrationId);
			ThrowUtils.throwIf(registrationForm == null, ErrorCode.PARAMS_ERROR, "报名登记表不存在");
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
		String content = messageNoticeQueryRequest.getContent();
		Integer pushStatus = messageNoticeQueryRequest.getPushStatus();
		Long registrationId = messageNoticeQueryRequest.getRegistrationId();
		String sortField = messageNoticeQueryRequest.getSortField();
		String sortOrder = messageNoticeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
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
		return MessageNoticeVO.objToVo(messageNotice);
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
		messageNoticeVOPage.setRecords(messageNoticeVOList);
		return messageNoticeVOPage;
	}
	
}
