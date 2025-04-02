package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.SchoolMapper;
import com.henu.registration.model.dto.school.SchoolQueryRequest;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.service.SchoolSchoolTypeService;
import com.henu.registration.service.SchoolService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
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
 * 高校信息服务实现
 *
 * @author stephenqiu
 * @description 针对表【school(高校信息)】的数据库操作Service实现
 * @createDate 2025-03-21 11:09:15
 */
@Service
@Slf4j
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {
	
	@Resource
	@Lazy
	private SchoolSchoolTypeService schoolSchoolTypeService;
	/**
	 * 校验数据
	 *
	 * @param school school
	 * @param add    对创建的数据进行校验
	 */
	@Override
	public void validSchool(School school, boolean add) {
		ThrowUtils.throwIf(school == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String schoolName = school.getSchoolName();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(schoolName), ErrorCode.PARAMS_ERROR, "高校名称不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(schoolName)) {
			ThrowUtils.throwIf(schoolName.length() > 80, ErrorCode.PARAMS_ERROR, "高校名称过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @return {@link QueryWrapper<School>}
	 */
	@Override
	public QueryWrapper<School> getQueryWrapper(SchoolQueryRequest schoolQueryRequest) {
		QueryWrapper<School> queryWrapper = new QueryWrapper<>();
		if (schoolQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = schoolQueryRequest.getId();
		Long notId = schoolQueryRequest.getNotId();
		String schoolName = schoolQueryRequest.getSchoolName();
		Long adminId = schoolQueryRequest.getAdminId();
		String sortField = schoolQueryRequest.getSortField();
		String sortOrder = schoolQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(schoolName), "school_name", schoolName);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取高校信息封装
	 *
	 * @param school  school
	 * @param request request
	 * @return {@link SchoolVO}
	 */
	@Override
	public SchoolVO getSchoolVO(School school, HttpServletRequest request) {
		// 对象转封装类
		SchoolVO schoolVO = SchoolVO.objToVo(school);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询学校类型信息
		LambdaQueryWrapper<SchoolSchoolType> eq = Wrappers.lambdaQuery(SchoolSchoolType.class).eq(SchoolSchoolType::getSchoolId, school.getId());
		SchoolSchoolType schoolSchoolType = schoolSchoolTypeService.getOne(eq);
		if (schoolSchoolType != null && schoolSchoolType.getSchoolId() > 0) {
			List<String> schoolTypeList = JSONUtil.toList(schoolSchoolType.getSchoolTypes(), String.class);
			schoolVO.setSchoolTypes(schoolTypeList);
		}
		// endregion
		return schoolVO;
	}
	
	@Override
	public Page<SchoolVO> getSchoolVOPage(Page<School> schoolPage, HttpServletRequest request) {
		List<School> schoolList = schoolPage.getRecords();
		Page<SchoolVO> schoolVOPage = new Page<>(schoolPage.getCurrent(), schoolPage.getSize(), schoolPage.getTotal());
		if (CollUtil.isEmpty(schoolList)) {
			return schoolVOPage;
		}
		// 对象列表 => 封装对象列表
		List<SchoolVO> schoolVOList = schoolList.stream()
				.map(SchoolVO::objToVo)
				.collect(Collectors.toList());
		// 关联查询学校类型信息
		Set<Long> schoolIdSet = schoolList.stream()
				.map(School::getId)
				.collect(Collectors.toSet());
		// 异步查询学校类型
		if (CollUtil.isNotEmpty(schoolIdSet)) {
			CompletableFuture<Map<Long, List<String>>> schoolTypeMapFuture = CompletableFuture.supplyAsync(() -> {
				// 关联查询学校类型信息
				List<SchoolSchoolType> schoolSchoolTypeList = schoolSchoolTypeService.list(
						Wrappers.lambdaQuery(SchoolSchoolType.class).in(SchoolSchoolType::getSchoolId, schoolIdSet)
				);
				// 正确地将查询结果转化为 Map<Long, List<String>> 的结构
				return schoolSchoolTypeList.stream()
						.collect(Collectors.toMap(
								SchoolSchoolType::getSchoolId,
								schoolSchoolType -> JSONUtil.toList(schoolSchoolType.getSchoolTypes(), String.class)
						));
			});
			try {
				// 填充学校类型信息
				Map<Long, List<String>> schoolTypeMap = schoolTypeMapFuture.get();
				schoolVOList.forEach(schoolVO -> {
					Long schoolId = schoolVO.getId();
					if (schoolTypeMap.containsKey(schoolId)) {
						// 直接设置学校类型
						schoolVO.setSchoolTypes(schoolTypeMap.get(schoolId));
					}
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取学校类型信息失败：" + e.getMessage());
			}
		}
		// 设置最终的结果
		schoolVOPage.setRecords(schoolVOList);
		return schoolVOPage;
	}
	
}
