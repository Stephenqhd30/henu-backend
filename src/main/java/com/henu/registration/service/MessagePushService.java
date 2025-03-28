package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.messagePush.MessagePushQueryRequest;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.vo.messagePush.MessagePushVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 消息推送服务
 *
 * @author stephenqiu
 * @description 针对表【message_push(消息推送表)】的数据库操作Service
 * @createDate 2025-03-28 00:11:11
 */
public interface MessagePushService extends IService<MessagePush> {

    /**
     * 校验数据
     *
     * @param messagePush messagePush
     * @param add 对创建的数据进行校验
     */
    void validMessagePush(MessagePush messagePush, boolean add);

    /**
     * 获取查询条件
     *
     * @param messagePushQueryRequest messagePushQueryRequest
     * @return {@link QueryWrapper<MessagePush>}
     */
    QueryWrapper<MessagePush> getQueryWrapper(MessagePushQueryRequest messagePushQueryRequest);

    /**
     * 获取消息推送封装
     *
     * @param messagePush messagePush
     * @param request request
     * @return {@link MessagePushVO}
     */
    MessagePushVO getMessagePushVO(MessagePush messagePush, HttpServletRequest request);

    /**
     * 分页获取消息推送封装
     *
     * @param messagePushPage messagePushPage
     * @param request request
     * @return {@link Page<MessagePushVO>}
     */
    Page<MessagePushVO> getMessagePushVOPage(Page<MessagePush> messagePushPage, HttpServletRequest request);
}