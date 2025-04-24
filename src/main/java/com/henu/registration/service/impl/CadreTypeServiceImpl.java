package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.CadreTypeMapper;
import com.henu.registration.model.dto.cadreType.CadreTypeQueryRequest;
import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.vo.cadreType.CadreTypeVO;
import com.henu.registration.service.CadreTypeService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 干部类型服务实现
 *
 * @author stephenqiu
 * @description 针对表【cadre_type(干部类型)】的数据库操作Service实现
 * @createDate 2025-03-21 00:58:48
 */
@Service
@Slf4j
public class CadreTypeServiceImpl extends ServiceImpl<CadreTypeMapper, CadreType> implements CadreTypeService {
	
	/**
	 * 校验数据
	 *
	 * @param cadreType cadreType
	 * @param add       对创建的数据进行校验
	 */
	@Override
	public void validCadreType(CadreType cadreType, boolean add) {
		ThrowUtils.throwIf(cadreType == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String type = cadreType.getType();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR, "干部类型不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(type)) {
			ThrowUtils.throwIf(type.length() > 80, ErrorCode.PARAMS_ERROR, "干部类型过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @return {@link QueryWrapper<CadreType>}
	 */
	@Override
	public QueryWrapper<CadreType> getQueryWrapper(CadreTypeQueryRequest cadreTypeQueryRequest) {
		QueryWrapper<CadreType> queryWrapper = new QueryWrapper<>();
		if (cadreTypeQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = cadreTypeQueryRequest.getId();
		Long notId = cadreTypeQueryRequest.getNotId();
		String type = cadreTypeQueryRequest.getType();
		Long adminId = cadreTypeQueryRequest.getAdminId();
		String sortField = cadreTypeQueryRequest.getSortField();
		String sortOrder = cadreTypeQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(type), "type", type);
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
	 * 获取干部类型封装
	 *
	 * @param cadreType cadreType
	 * @param request   request
	 * @return {@link CadreTypeVO}
	 */
	@Override
	public CadreTypeVO getCadreTypeVO(CadreType cadreType, HttpServletRequest request) {
		// 对象转封装类
		return CadreTypeVO.objToVo(cadreType);
	}
	
	/**
	 * 分页获取干部类型封装
	 *
	 * @param cadreTypePage cadreTypePage
	 * @param request       request
	 * @return {@link Page<CadreTypeVO>}
	 */
	@Override
	public Page<CadreTypeVO> getCadreTypeVOPage(Page<CadreType> cadreTypePage, HttpServletRequest request) {
		List<CadreType> cadreTypeList = cadreTypePage.getRecords();
		Page<CadreTypeVO> cadreTypeVOPage = new Page<>(cadreTypePage.getCurrent(), cadreTypePage.getSize(), cadreTypePage.getTotal());
		if (CollUtil.isEmpty(cadreTypeList)) {
			return cadreTypeVOPage;
		}
		// 对象列表 => 封装对象列表
		List<CadreTypeVO> cadreTypeVOList = cadreTypeList.stream()
				.map(CadreTypeVO::objToVo)
				.collect(Collectors.toList());
		cadreTypeVOPage.setRecords(cadreTypeVOList);
		return cadreTypeVOPage;
	}
	
}
