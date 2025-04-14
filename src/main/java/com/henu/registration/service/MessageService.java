package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.message.MessageQueryRequest;
import com.henu.registration.model.entity.Message;
import com.henu.registration.model.vo.message.MessageVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 消息通知服务
 *
 * @author stephenqiu
 * @description 针对表【message(消息通知表)】的数据库操作Service
 * @createDate 2025-04-14 23:41:31
 */
public interface MessageService extends IService<Message> {
	
	/**
	 * 校验数据
	 *
	 * @param message message
	 * @param add     对创建的数据进行校验
	 */
	void validMessage(Message message, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param messageQueryRequest messageQueryRequest
	 * @return {@link QueryWrapper<Message>}
	 */
	QueryWrapper<Message> getQueryWrapper(MessageQueryRequest messageQueryRequest);
	
	/**
	 * 获取消息通知封装
	 *
	 * @param message message
	 * @param request request
	 * @return {@link MessageVO}
	 */
	MessageVO getMessageVO(Message message, HttpServletRequest request);
	
	/**
	 * 分页获取消息通知封装
	 *
	 * @param messagePage messagePage
	 * @param request     request
	 * @return {@link Page<MessageVO>}
	 */
	Page<MessageVO> getMessageVOPage(Page<Message> messagePage, HttpServletRequest request);
}