package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.messageNotice.MessageNoticeQueryRequest;
import com.henu.registration.model.entity.MessageNotice;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 消息通知服务
 * @author stephenqiu
 * @description 针对表【message_notice(消息通知表)】的数据库操作Service
 * @createDate 2025-03-24 00:56:26
 */
public interface MessageNoticeService extends IService<MessageNotice> {

    /**
     * 校验数据
     *
     * @param messageNotice messageNotice
     * @param add 对创建的数据进行校验
     */
    void validMessageNotice(MessageNotice messageNotice, boolean add);

    /**
     * 获取查询条件
     *
     * @param messageNoticeQueryRequest messageNoticeQueryRequest
     * @return {@link QueryWrapper<MessageNotice>}
     */
    QueryWrapper<MessageNotice> getQueryWrapper(MessageNoticeQueryRequest messageNoticeQueryRequest);

    /**
     * 获取消息通知封装
     *
     * @param messageNotice messageNotice
     * @param request request
     * @return {@link MessageNoticeVO}
     */
    MessageNoticeVO getMessageNoticeVO(MessageNotice messageNotice, HttpServletRequest request);

    /**
     * 分页获取消息通知封装
     *
     * @param messageNoticePage messageNoticePage
     * @param request request
     * @return {@link Page<MessageNoticeVO>}
     */
    Page<MessageNoticeVO> getMessageNoticeVOPage(Page<MessageNotice> messageNoticePage, HttpServletRequest request);
}