package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.mapper.FamilyMapper;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.family.FamilyQueryRequest;
import com.henu.registration.model.entity.Family;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.FamilyVO;
import com.henu.registration.model.vo.UserVO;
import com.henu.registration.service.FamilyService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 家庭关系服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class FamilyServiceImpl extends ServiceImpl<FamilyMapper, Family> implements FamilyService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param family family
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validFamily(Family family, boolean add) {
        ThrowUtils.throwIf(family == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = family.getTitle();
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
     * @param familyQueryRequest familyQueryRequest
     * @return {@link QueryWrapper<Family>}
     */
    @Override
    public QueryWrapper<Family> getQueryWrapper(FamilyQueryRequest familyQueryRequest) {
        QueryWrapper<Family> queryWrapper = new QueryWrapper<>();
        if (familyQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = familyQueryRequest.getId();
        Long notId = familyQueryRequest.getNotId();
        String title = familyQueryRequest.getTitle();
        String content = familyQueryRequest.getContent();
        String searchText = familyQueryRequest.getSearchText();
        String sortField = familyQueryRequest.getSortField();
        String sortOrder = familyQueryRequest.getSortOrder();
        List<String> tagList = familyQueryRequest.getTags();
        Long userId = familyQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取家庭关系封装
     *
     * @param family family
     * @param request request
     * @return {@link FamilyVO}
     */
    @Override
    public FamilyVO getFamilyVO(Family family, HttpServletRequest request) {
        // 对象转封装类
        FamilyVO familyVO = FamilyVO.objToVo(family);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = family.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user, request);
        familyVO.setUserVO(userVO);

        // endregion
        return familyVO;
    }

    /**
     * 分页获取家庭关系封装
     *
     * @param familyPage familyPage
     * @param request request
     * @return {@link Page<FamilyVO>}
     */
    @Override
    public Page<FamilyVO> getFamilyVOPage(Page<Family> familyPage, HttpServletRequest request) {
        List<Family> familyList = familyPage.getRecords();
        Page<FamilyVO> familyVOPage = new Page<>(familyPage.getCurrent(), familyPage.getSize(), familyPage.getTotal());
        if (CollUtil.isEmpty(familyList)) {
            return familyVOPage;
        }
        // 对象列表 => 封装对象列表
        List<FamilyVO> familyVOList = familyList.stream()
                            .map(FamilyVO::objToVo)
                            .collect(Collectors.toList());
        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = familyList.stream().map(Family::getUserId).collect(Collectors.toSet());
        // 填充信息
        if (CollUtil.isNotEmpty(userIdSet)) {
        CompletableFuture<Map<Long, List<User>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> userService.listByIds(userIdSet).stream()
            .collect(Collectors.groupingBy(User::getId)));
            try { 
                Map<Long, List<User>> userIdUserListMap = mapCompletableFuture.get();
                // 填充信息
                familyVOList.forEach(familyVO -> {
                    Long userId = familyVO.getUserId();
                    User user = null;
                    if (userIdUserListMap.containsKey(userId)) {
                        user = userIdUserListMap.get(userId).get(0);
                    }
                    familyVO.setUserVO(userService.getUserVO(user, request));
                });
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
            }
        }
        // endregion
        familyVOPage.setRecords(familyVOList);
        return familyVOPage;
    }

}
