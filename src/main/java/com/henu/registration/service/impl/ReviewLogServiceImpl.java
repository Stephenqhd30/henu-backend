package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.ReviewLogMapper;
import com.henu.registration.model.dto.reviewLog.ReviewLogQueryRequest;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.entity.ReviewLog;
import com.henu.registration.model.enums.ReviewStatusEnum;
import com.henu.registration.model.vo.reviewLog.ReviewLogVO;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.service.ReviewLogService;
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
 * 审核记录服务实现
 * @author stephenqiu
 * @description 针对表【review_log(审核记录表)】的数据库操作Service实现
 * @createDate 2025-03-23 00:32:51
 */
@Service
@Slf4j
public class ReviewLogServiceImpl extends ServiceImpl<ReviewLogMapper, ReviewLog> implements ReviewLogService {
    
    @Resource
    private RegistrationFormService registrationFormService;

    /**
     * 校验数据
     *
     * @param reviewLog reviewLog
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validReviewLog(ReviewLog reviewLog, boolean add) {
        ThrowUtils.throwIf(reviewLog == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        Long registrationId = reviewLog.getRegistrationId();
        Integer reviewStatus = reviewLog.getReviewStatus();
        String reviewComments = reviewLog.getReviewComments();
    
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(ObjectUtils.isEmpty(registrationId), ErrorCode.PARAMS_ERROR, "报名记录不能为空");
            ThrowUtils.throwIf(ObjectUtils.isEmpty(reviewStatus), ErrorCode.PARAMS_ERROR, "审核状态不能为空");
            ThrowUtils.throwIf(StringUtils.isBlank(reviewComments), ErrorCode.PARAMS_ERROR, "审核意见不能为空");
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (ObjectUtils.isNotEmpty(reviewStatus)) {
            ReviewStatusEnum reviewStatusEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
            ThrowUtils.throwIf(reviewStatusEnum == null, ErrorCode.PARAMS_ERROR, "审核状态不合法");
        }
        if (ObjectUtils.isNotEmpty(registrationId)) {
            RegistrationForm registrationForm = registrationFormService.getById(registrationId);
            ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR, "报名记录不存在");
        }
        if (StringUtils.isNotBlank(reviewComments)) {
            ThrowUtils.throwIf(reviewComments.length() > 80, ErrorCode.PARAMS_ERROR, "审核意见不能过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param reviewLogQueryRequest reviewLogQueryRequest
     * @return {@link QueryWrapper<ReviewLog>}
     */
    @Override
    public QueryWrapper<ReviewLog> getQueryWrapper(ReviewLogQueryRequest reviewLogQueryRequest) {
        QueryWrapper<ReviewLog> queryWrapper = new QueryWrapper<>();
        if (reviewLogQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = reviewLogQueryRequest.getId();
        Long notId = reviewLogQueryRequest.getNotId();
        Long registrationId = reviewLogQueryRequest.getRegistrationId();
        String reviewer = reviewLogQueryRequest.getReviewer();
        Integer reviewStatus = reviewLogQueryRequest.getReviewStatus();
        String reviewComments = reviewLogQueryRequest.getReviewComments();
        String sortField = reviewLogQueryRequest.getSortField();
        String sortOrder = reviewLogQueryRequest.getSortOrder();
        
        // todo 补充需要的查询条件
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(reviewComments), "review_comments", reviewComments);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(registrationId), "registration_id", registrationId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewer), "reviewer", reviewer);
        queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "review_status", reviewStatus);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取审核记录封装
     *
     * @param reviewLog reviewLog
     * @param request request
     * @return {@link ReviewLogVO}
     */
    @Override
    public ReviewLogVO getReviewLogVO(ReviewLog reviewLog, HttpServletRequest request) {
        // 对象转封装类
        return ReviewLogVO.objToVo(reviewLog);
    }

    /**
     * 分页获取审核记录封装
     *
     * @param reviewLogPage reviewLogPage
     * @param request request
     * @return {@link Page<ReviewLogVO>}
     */
    @Override
    public Page<ReviewLogVO> getReviewLogVOPage(Page<ReviewLog> reviewLogPage, HttpServletRequest request) {
        List<ReviewLog> reviewLogList = reviewLogPage.getRecords();
        Page<ReviewLogVO> reviewLogVOPage = new Page<>(reviewLogPage.getCurrent(), reviewLogPage.getSize(), reviewLogPage.getTotal());
        if (CollUtil.isEmpty(reviewLogList)) {
            return reviewLogVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ReviewLogVO> reviewLogVOList = reviewLogList.stream()
                .map(ReviewLogVO::objToVo)
                            .collect(Collectors.toList());
        reviewLogVOPage.setRecords(reviewLogVOList);
        return reviewLogVOPage;
    }

}
