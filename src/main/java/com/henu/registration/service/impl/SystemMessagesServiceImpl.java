package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.SystemMessagesMapper;
import com.henu.registration.model.dto.systemMessages.SystemMessagesQueryRequest;
import com.henu.registration.model.entity.SystemMessages;
import com.henu.registration.model.enums.MessageTypeEnum;
import com.henu.registration.model.vo.systemMessages.SystemMessagesVO;
import com.henu.registration.service.SystemMessagesService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 系统消息服务实现
 *
 * @author stephenqiu
 * @description 针对表【system_messages(系统消息表)】的数据库操作Service实现
 * @createDate 2025-03-27 00:01:59
 */
@Service
@Slf4j
public class SystemMessagesServiceImpl extends ServiceImpl<SystemMessagesMapper, SystemMessages> implements SystemMessagesService {
	
	/**
	 * 校验数据
	 *
	 * @param systemMessages systemMessages
	 * @param add            对创建的数据进行校验
	 */
	@Override
	public void validSystemMessages(SystemMessages systemMessages, boolean add) {
		ThrowUtils.throwIf(systemMessages == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String title = systemMessages.getTitle();
		String content = systemMessages.getContent();
		String type = systemMessages.getType();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(content), ErrorCode.PARAMS_ERROR, "内容不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR, "类型不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(title)) {
			ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
		}
		if (StringUtils.isNotBlank(type)) {
			ThrowUtils.throwIf(MessageTypeEnum.getEnumByValue(type) == null, ErrorCode.PARAMS_ERROR, "类型不存在");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param systemMessagesQueryRequest systemMessagesQueryRequest
	 * @return {@link QueryWrapper<SystemMessages>}
	 */
	@Override
	public QueryWrapper<SystemMessages> getQueryWrapper(SystemMessagesQueryRequest systemMessagesQueryRequest) {
		QueryWrapper<SystemMessages> queryWrapper = new QueryWrapper<>();
		if (systemMessagesQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = systemMessagesQueryRequest.getId();
		Long notId = systemMessagesQueryRequest.getNotId();
		String title = systemMessagesQueryRequest.getTitle();
		String content = systemMessagesQueryRequest.getContent();
		Date pushTime = systemMessagesQueryRequest.getPushTime();
		Integer pushStatus = systemMessagesQueryRequest.getPushStatus();
		String type = systemMessagesQueryRequest.getType();
		String sortField = systemMessagesQueryRequest.getSortField();
		String sortOrder = systemMessagesQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
		queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(pushTime), "push_time", pushTime);
		queryWrapper.eq(ObjectUtils.isNotEmpty(pushStatus), "push_status", pushStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(type), "type", type);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取系统消息封装
	 *
	 * @param systemMessages systemMessages
	 * @param request        request
	 * @return {@link SystemMessagesVO}
	 */
	@Override
	public SystemMessagesVO getSystemMessagesVO(SystemMessages systemMessages, HttpServletRequest request) {
		// 对象转封装类
		return SystemMessagesVO.objToVo(systemMessages);
	}
	
	/**
	 * 分页获取系统消息封装
	 *
	 * @param systemMessagesPage systemMessagesPage
	 * @param request            request
	 * @return {@link Page<SystemMessagesVO>}
	 */
	@Override
	public Page<SystemMessagesVO> getSystemMessagesVOPage(Page<SystemMessages> systemMessagesPage, HttpServletRequest request) {
		List<SystemMessages> systemMessagesList = systemMessagesPage.getRecords();
		Page<SystemMessagesVO> systemMessagesVOPage = new Page<>(systemMessagesPage.getCurrent(), systemMessagesPage.getSize(), systemMessagesPage.getTotal());
		if (CollUtil.isEmpty(systemMessagesList)) {
			return systemMessagesVOPage;
		}
		// 对象列表 => 封装对象列表
		List<SystemMessagesVO> systemMessagesVOList = systemMessagesList.stream()
				.map(SystemMessagesVO::objToVo)
				.collect(Collectors.toList());
		systemMessagesVOPage.setRecords(systemMessagesVOList);
		return systemMessagesVOPage;
	}
	
}
