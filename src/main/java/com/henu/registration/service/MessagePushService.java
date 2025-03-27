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
 * @author stephen qiu
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