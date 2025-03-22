package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.FamilyMapper;
import com.henu.registration.model.dto.family.FamilyQueryRequest;
import com.henu.registration.model.entity.Family;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.family.FamilyVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.FamilyService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * 家庭关系服务实现
 *
 * @author stephenqiu
 * @description 针对表【family(家庭关系表)】的数据库操作Service实现
 * @createDate 2025-03-23 00:15:52
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
	 * @param add    对创建的数据进行校验
	 */
	@Override
	public void validFamily(Family family, boolean add) {
		ThrowUtils.throwIf(family == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String appellation = family.getAppellation();
		String familyName = family.getFamilyName();
		String workDetail = family.getWorkDetail();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(appellation), ErrorCode.PARAMS_ERROR, "称谓不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(familyName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(workDetail), ErrorCode.PARAMS_ERROR, "工作单位及职务不能为空");
			
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(workDetail)) {
			ThrowUtils.throwIf(workDetail.length() > 80, ErrorCode.PARAMS_ERROR, "工作单位及职务过长");
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
		String appellation = familyQueryRequest.getAppellation();
		String familyName = familyQueryRequest.getFamilyName();
		String workDetail = familyQueryRequest.getWorkDetail();
		Long userId = familyQueryRequest.getUserId();
		String sortField = familyQueryRequest.getSortField();
		String sortOrder = familyQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(workDetail), "work_detail", workDetail);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(appellation), "appellation", appellation);
		queryWrapper.eq(ObjectUtils.isNotEmpty(familyName), "family_name", familyName);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取家庭关系封装
	 *
	 * @param family  family
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
	 * @param request    request
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
