package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.SchoolTypeMapper;
import com.henu.registration.model.dto.schoolType.SchoolTypeQueryRequest;
import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.schoolType.SchoolTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolTypeService;
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
 * 高校类型服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class SchoolTypeServiceImpl extends ServiceImpl<SchoolTypeMapper, SchoolType> implements SchoolTypeService {
	
	/**
	 * 校验数据
	 *
	 * @param schoolType schoolType
	 * @param add        对创建的数据进行校验
	 */
	@Override
	public void validSchoolType(SchoolType schoolType, boolean add) {
		ThrowUtils.throwIf(schoolType == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String typeName = schoolType.getTypeName();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(typeName), ErrorCode.PARAMS_ERROR, "高校类型不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(typeName)) {
			ThrowUtils.throwIf(typeName.length() > 80, ErrorCode.PARAMS_ERROR, "高校类型过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @return {@link QueryWrapper<SchoolType>}
	 */
	@Override
	public QueryWrapper<SchoolType> getQueryWrapper(SchoolTypeQueryRequest schoolTypeQueryRequest) {
		QueryWrapper<SchoolType> queryWrapper = new QueryWrapper<>();
		if (schoolTypeQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = schoolTypeQueryRequest.getId();
		Long notId = schoolTypeQueryRequest.getNotId();
		String typeName = schoolTypeQueryRequest.getTypeName();
		Long adminId = schoolTypeQueryRequest.getAdminId();
		String sortField = schoolTypeQueryRequest.getSortField();
		String sortOrder = schoolTypeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(typeName), "type_name", typeName);
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
	 * 获取高校类型封装
	 *
	 * @param schoolType schoolType
	 * @param request    request
	 * @return {@link SchoolTypeVO}
	 */
	@Override
	public SchoolTypeVO getSchoolTypeVO(SchoolType schoolType, HttpServletRequest request) {
		// 对象转封装类
		return SchoolTypeVO.objToVo(schoolType);
	}
	
	/**
	 * 分页获取高校类型封装
	 *
	 * @param schoolTypePage schoolTypePage
	 * @param request        request
	 * @return {@link Page<SchoolTypeVO>}
	 */
	@Override
	public Page<SchoolTypeVO> getSchoolTypeVOPage(Page<SchoolType> schoolTypePage, HttpServletRequest request) {
		List<SchoolType> schoolTypeList = schoolTypePage.getRecords();
		Page<SchoolTypeVO> schoolTypeVOPage = new Page<>(schoolTypePage.getCurrent(), schoolTypePage.getSize(), schoolTypePage.getTotal());
		if (CollUtil.isEmpty(schoolTypeList)) {
			return schoolTypeVOPage;
		}
		// 对象列表 => 封装对象列表
		List<SchoolTypeVO> schoolTypeVOList = schoolTypeList.stream()
				.map(SchoolTypeVO::objToVo)
				.collect(Collectors.toList());
		schoolTypeVOPage.setRecords(schoolTypeVOList);
		return schoolTypeVOPage;
	}
	
}
