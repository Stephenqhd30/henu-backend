package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.systemMessages.SystemMessagesQueryRequest;
import com.henu.registration.model.entity.SystemMessages;
import com.henu.registration.model.vo.systemMessages.SystemMessagesVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统消息服务
 *
 * @author stephenqiu
 * @description 针对表【system_messages(系统消息表)】的数据库操作Service
 * @createDate 2025-03-27 00:01:59
 */
public interface SystemMessagesService extends IService<SystemMessages> {
	
	/**
	 * 校验数据
	 *
	 * @param systemMessages systemMessages
	 * @param add            对创建的数据进行校验
	 */
	void validSystemMessages(SystemMessages systemMessages, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param systemMessagesQueryRequest systemMessagesQueryRequest
	 * @return {@link QueryWrapper<SystemMessages>}
	 */
	QueryWrapper<SystemMessages> getQueryWrapper(SystemMessagesQueryRequest systemMessagesQueryRequest);
	
	/**
	 * 获取系统消息封装
	 *
	 * @param systemMessages systemMessages
	 * @param request        request
	 * @return {@link SystemMessagesVO}
	 */
	SystemMessagesVO getSystemMessagesVO(SystemMessages systemMessages, HttpServletRequest request);
	
	/**
	 * 分页获取系统消息封装
	 *
	 * @param systemMessagesPage systemMessagesPage
	 * @param request            request
	 * @return {@link Page<SystemMessagesVO>}
	 */
	Page<SystemMessagesVO> getSystemMessagesVOPage(Page<SystemMessages> systemMessagesPage, HttpServletRequest request);
}