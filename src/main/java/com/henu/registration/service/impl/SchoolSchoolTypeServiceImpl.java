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
import com.henu.registration.mapper.SchoolSchoolTypeMapper;
import com.henu.registration.model.dto.schoolSchoolType.SchoolSchoolTypeQueryRequest;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.model.vo.schoolSchoolType.SchoolSchoolTypeVO;
import com.henu.registration.service.SchoolSchoolTypeService;
import com.henu.registration.service.SchoolService;
import com.henu.registration.service.SchoolTypeService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
 * 高校与高校类型关联信息服务实现
 *
 * @author stephenqiu
 * @description 针对表【school_school_type(高校与高校类型关联表)】的数据库操作Service实现
 * @createDate 2025-03-21 11:46:54
 */
@Service
@Slf4j
public class SchoolSchoolTypeServiceImpl extends ServiceImpl<SchoolSchoolTypeMapper, SchoolSchoolType> implements SchoolSchoolTypeService {
	
	@Resource
	private SchoolService schoolService;
	
	@Resource
	private SchoolTypeService schoolTypeService;
	
	/**
	 * 校验数据
	 *
	 * @param schoolSchoolType schoolSchoolType
	 * @param add              对创建的数据进行校验
	 */
	@Override
	public void validSchoolSchoolType(SchoolSchoolType schoolSchoolType, boolean add) {
		ThrowUtils.throwIf(schoolSchoolType == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long schoolId = schoolSchoolType.getSchoolId();
		String schoolTypes = schoolSchoolType.getSchoolTypes();
		List<String> schoolTypeList = JSONUtil.toList(schoolTypes, String.class);
		
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(schoolId), ErrorCode.PARAMS_ERROR, "高校id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(schoolTypeList), ErrorCode.PARAMS_ERROR, "高校类别列表不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isEmpty(schoolId) && ObjectUtils.isEmpty(schoolTypeList)) {
			School school = schoolService.getById(schoolId);
			ThrowUtils.throwIf(school == null, ErrorCode.PARAMS_ERROR, "高校不存在");
			for (String schoolType : schoolTypeList) {
				LambdaQueryWrapper<SchoolType> eq = Wrappers.lambdaQuery(SchoolType.class).eq(SchoolType::getTypeName, schoolType);
				SchoolType schoolTypeName = schoolTypeService.getOne(eq);
				ThrowUtils.throwIf(schoolTypeName == null, ErrorCode.PARAMS_ERROR, "高校类别不存在");
			}
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest
	 * @return {@link QueryWrapper<SchoolSchoolType>}
	 */
	@Override
	public QueryWrapper<SchoolSchoolType> getQueryWrapper(SchoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest) {
		QueryWrapper<SchoolSchoolType> queryWrapper = new QueryWrapper<>();
		if (schoolSchoolTypeQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = schoolSchoolTypeQueryRequest.getId();
		Long notId = schoolSchoolTypeQueryRequest.getNotId();
		Long schoolId = schoolSchoolTypeQueryRequest.getSchoolId();
		List<String> schoolTypes = schoolSchoolTypeQueryRequest.getSchoolTypes();
		Long adminId = schoolSchoolTypeQueryRequest.getAdminId();
		String sortField = schoolSchoolTypeQueryRequest.getSortField();
		String sortOrder = schoolSchoolTypeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 遍历查询
		if (CollUtil.isNotEmpty(schoolTypes)) {
			for (String schoolTypeName : schoolTypes) {
				queryWrapper.like("school_types", "\"" + schoolTypeName + "\"");
			}
		}
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(schoolId), "school_id", schoolId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取高校与高校类型关联信息封装
	 *
	 * @param schoolSchoolType schoolSchoolType
	 * @param request          request
	 * @return {@link SchoolSchoolTypeVO}
	 */
	@Override
	public SchoolSchoolTypeVO getSchoolSchoolTypeVO(SchoolSchoolType schoolSchoolType, HttpServletRequest request) {
		// 对象转封装类
		SchoolSchoolTypeVO schoolSchoolTypeVO = SchoolSchoolTypeVO.objToVo(schoolSchoolType);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联高校信息
		Long schoolId = schoolSchoolType.getSchoolId();
		School school = null;
		if (schoolId != null && schoolId > 0) {
			school = schoolService.getById(schoolId);
		}
		SchoolVO schoolVO = schoolService.getSchoolVO(school, request);
		schoolSchoolTypeVO.setSchoolVO(schoolVO);
		// endregion
		return schoolSchoolTypeVO;
	}
	
	/**
	 * 分页获取高校与高校类型关联信息封装
	 *
	 * @param schoolSchoolTypePage schoolSchoolTypePage
	 * @param request              request
	 * @return {@link Page<SchoolSchoolTypeVO>}
	 */
	@Override
	public Page<SchoolSchoolTypeVO> getSchoolSchoolTypeVOPage(Page<SchoolSchoolType> schoolSchoolTypePage, HttpServletRequest request) {
		List<SchoolSchoolType> schoolSchoolTypeList = schoolSchoolTypePage.getRecords();
		Page<SchoolSchoolTypeVO> schoolSchoolTypeVOPage = new Page<>(schoolSchoolTypePage.getCurrent(), schoolSchoolTypePage.getSize(), schoolSchoolTypePage.getTotal());
		if (CollUtil.isEmpty(schoolSchoolTypeList)) {
			return schoolSchoolTypeVOPage;
		}
		// 对象列表 => 封装对象列表
		List<SchoolSchoolTypeVO> schoolSchoolTypeVOList = schoolSchoolTypeList.stream()
				.map(SchoolSchoolTypeVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询高校信息
		Set<Long> schoolIdSet = schoolSchoolTypeList.stream().map(SchoolSchoolType::getSchoolId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(schoolIdSet)) {
			CompletableFuture<Map<Long, List<School>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> schoolService.listByIds(schoolIdSet).stream()
					.collect(Collectors.groupingBy(School::getId)));
			try {
				Map<Long, List<School>> schoolIdSchoolListMap = mapCompletableFuture.get();
				// 填充信息
				schoolSchoolTypeVOList.forEach(schoolSchoolTypeVO -> {
					Long schoolId = schoolSchoolTypeVO.getSchoolId();
					School school = null;
					if (schoolIdSchoolListMap.containsKey(schoolId)) {
						school = schoolIdSchoolListMap.get(schoolId).get(0);
					}
					schoolSchoolTypeVO.setSchoolVO(schoolService.getSchoolVO(school, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		schoolSchoolTypeVOPage.setRecords(schoolSchoolTypeVOList);
		return schoolSchoolTypeVOPage;
	}
	
}
