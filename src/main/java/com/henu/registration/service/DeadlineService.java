package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.deadline.DeadlineQueryRequest;
import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.vo.deadline.DeadlineVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 截止时间服务
 *
 * @author stephen qiu
 */
public interface DeadlineService extends IService<Deadline> {

    /**
     * 校验数据
     *
     * @param deadline deadline
     * @param add 对创建的数据进行校验
     */
    void validDeadline(Deadline deadline, boolean add);

    /**
     * 获取查询条件
     *
     * @param deadlineQueryRequest deadlineQueryRequest
     * @return {@link QueryWrapper<Deadline>}
     */
    QueryWrapper<Deadline> getQueryWrapper(DeadlineQueryRequest deadlineQueryRequest);

    /**
     * 获取截止时间封装
     *
     * @param deadline deadline
     * @param request request
     * @return {@link DeadlineVO}
     */
    DeadlineVO getDeadlineVO(Deadline deadline, HttpServletRequest request);

    /**
     * 分页获取截止时间封装
     *
     * @param deadlinePage deadlinePage
     * @param request request
     * @return {@link Page<DeadlineVO>}
     */
    Page<DeadlineVO> getDeadlineVOPage(Page<Deadline> deadlinePage, HttpServletRequest request);
}