package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.MessageMapper;
import com.henu.registration.model.dto.message.MessageQueryRequest;
import com.henu.registration.model.entity.Message;
import com.henu.registration.model.vo.message.MessageVO;
import com.henu.registration.service.MessageService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息通知服务实现
 *
 * @author stephenqiu
 * @description 针对表【message(消息通知表)】的数据库操作Service实现
 * @createDate 2025-04-14 23:41:31
 */
@Service
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {
	
	/**
	 * 校验数据
	 *
	 * @param message message
	 * @param add     对创建的数据进行校验
	 */
	@Override
	public void validMessage(Message message, boolean add) {
		ThrowUtils.throwIf(message == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String title = message.getTitle();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(title)) {
			ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param messageQueryRequest messageQueryRequest
	 * @return {@link QueryWrapper<Message>}
	 */
	@Override
	public QueryWrapper<Message> getQueryWrapper(MessageQueryRequest messageQueryRequest) {
		QueryWrapper<Message> queryWrapper = new QueryWrapper<>();
		if (messageQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = messageQueryRequest.getId();
		Long notId = messageQueryRequest.getNotId();
		String searchText = messageQueryRequest.getSearchText();
		String title = messageQueryRequest.getTitle();
		String content = messageQueryRequest.getContent();
		Long adminId = messageQueryRequest.getAdminId();
		String sortField = messageQueryRequest.getSortField();
		String sortOrder = messageQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
		queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取消息通知封装
	 *
	 * @param message message
	 * @param request request
	 * @return {@link MessageVO}
	 */
	@Override
	public MessageVO getMessageVO(Message message, HttpServletRequest request) {
		// 对象转封装类
		return MessageVO.objToVo(message);
	}
	
	/**
	 * 分页获取消息通知封装
	 *
	 * @param messagePage messagePage
	 * @param request     request
	 * @return {@link Page<MessageVO>}
	 */
	@Override
	public Page<MessageVO> getMessageVOPage(Page<Message> messagePage, HttpServletRequest request) {
		List<Message> messageList = messagePage.getRecords();
		Page<MessageVO> messageVOPage = new Page<>(messagePage.getCurrent(), messagePage.getSize(), messagePage.getTotal());
		if (CollUtil.isEmpty(messageList)) {
			return messageVOPage;
		}
		// 对象列表 => 封装对象列表
		List<MessageVO> messageVOList = messageList.stream()
				.map(MessageVO::objToVo)
				.collect(Collectors.toList());
		messageVOPage.setRecords(messageVOList);
		return messageVOPage;
	}
	
}
