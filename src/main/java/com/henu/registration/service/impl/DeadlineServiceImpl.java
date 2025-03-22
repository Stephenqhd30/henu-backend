package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.DeadlineMapper;
import com.henu.registration.model.dto.deadline.DeadlineQueryRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.deadline.DeadlineVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.DeadlineService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
 * 截止时间服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class DeadlineServiceImpl extends ServiceImpl<DeadlineMapper, Deadline> implements DeadlineService {

    @Resource
    private AdminService adminService;
    
    /**
     * 校验数据
     *
     * @param deadline deadline
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validDeadline(Deadline deadline, boolean add) {
        ThrowUtils.throwIf(deadline == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Date deadlineTime = deadline.getDeadlineTime();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(ObjectUtils.isEmpty(deadlineTime), ErrorCode.PARAMS_ERROR, "截止时间不能为空");
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (ObjectUtils.isNotEmpty(deadlineTime)) {
            ThrowUtils.throwIf(deadlineTime.before(new Date()), ErrorCode.PARAMS_ERROR, "截止时间不能早于当前时间");
        }
    }

    /**
     * 获取查询条件
     *
     * @param deadlineQueryRequest deadlineQueryRequest
     * @return {@link QueryWrapper<Deadline>}
     */
    @Override
    public QueryWrapper<Deadline> getQueryWrapper(DeadlineQueryRequest deadlineQueryRequest) {
        QueryWrapper<Deadline> queryWrapper = new QueryWrapper<>();
        if (deadlineQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = deadlineQueryRequest.getId();
        Long notId = deadlineQueryRequest.getNotId();
        Date deadlineTime = deadlineQueryRequest.getDeadlineTime();
        Long adminId = deadlineQueryRequest.getAdminId();
        String sortField = deadlineQueryRequest.getSortField();
        String sortOrder = deadlineQueryRequest.getSortOrder();
        
        // todo 补充需要的查询条件
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(deadlineTime), "deadline_time", deadlineTime);
        queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取截止时间封装
     *
     * @param deadline deadline
     * @param request request
     * @return {@link DeadlineVO}
     */
    @Override
    public DeadlineVO getDeadlineVO(Deadline deadline, HttpServletRequest request) {
        // 对象转封装类
        DeadlineVO deadlineVO = DeadlineVO.objToVo(deadline);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long adminId = deadline.getAdminId();
        Admin admin = null;
        if (adminId != null && adminId > 0) {
            admin = adminService.getById(adminId);
        }
        AdminVO userVO = adminService.getAdminVO(admin, request);
        deadlineVO.setAdminVO(userVO);

        // endregion
        return deadlineVO;
    }

    /**
     * 分页获取截止时间封装
     *
     * @param deadlinePage deadlinePage
     * @param request request
     * @return {@link Page<DeadlineVO>}
     */
    @Override
    public Page<DeadlineVO> getDeadlineVOPage(Page<Deadline> deadlinePage, HttpServletRequest request) {
        List<Deadline> deadlineList = deadlinePage.getRecords();
        Page<DeadlineVO> deadlineVOPage = new Page<>(deadlinePage.getCurrent(), deadlinePage.getSize(), deadlinePage.getTotal());
        if (CollUtil.isEmpty(deadlineList)) {
            return deadlineVOPage;
        }
        // 对象列表 => 封装对象列表
        List<DeadlineVO> deadlineVOList = deadlineList.stream()
                .map(DeadlineVO::objToVo)
                            .collect(Collectors.toList());
        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = deadlineList.stream().map(Deadline::getAdminId).collect(Collectors.toSet());
        // 填充信息
        if (CollUtil.isNotEmpty(userIdSet)) {
            CompletableFuture<Map<Long, List<Admin>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> adminService.listByIds(userIdSet).stream()
                    .collect(Collectors.groupingBy(Admin::getId)));
            try {
                Map<Long, List<Admin>> userIdAdminListMap = mapCompletableFuture.get();
                // 填充信息
                deadlineVOList.forEach(deadlineVO -> {
                    Long userId = deadlineVO.getAdminId();
                    Admin user = null;
                    if (userIdAdminListMap.containsKey(userId)) {
                        user = userIdAdminListMap.get(userId).get(0);
                    }
                    deadlineVO.setAdminVO(adminService.getAdminVO(user, request));
                });
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
            }
        }
        // endregion
        deadlineVOPage.setRecords(deadlineVOList);
        return deadlineVOPage;
    }

}
