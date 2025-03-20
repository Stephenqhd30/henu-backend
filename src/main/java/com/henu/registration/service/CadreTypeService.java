package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.cadreType.CadreTypeQueryRequest;
import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.vo.cadreType.CadreTypeVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 干部类型服务
 *
 * @author stephenqiu
 * @description 针对表【cadre_type(干部类型)】的数据库操作Service
 * @createDate 2025-03-21 00:58:48
 */
public interface CadreTypeService extends IService<CadreType> {
	
	/**
	 * 校验数据
	 *
	 * @param cadreType cadreType
	 * @param add       对创建的数据进行校验
	 */
	void validCadreType(CadreType cadreType, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @return {@link QueryWrapper<CadreType>}
	 */
	QueryWrapper<CadreType> getQueryWrapper(CadreTypeQueryRequest cadreTypeQueryRequest);
	
	/**
	 * 获取干部类型封装
	 *
	 * @param cadreType cadreType
	 * @param request   request
	 * @return {@link CadreTypeVO}
	 */
	CadreTypeVO getCadreTypeVO(CadreType cadreType, HttpServletRequest request);
	
	/**
	 * 分页获取干部类型封装
	 *
	 * @param cadreTypePage cadreTypePage
	 * @param request       request
	 * @return {@link Page<CadreTypeVO>}
	 */
	Page<CadreTypeVO> getCadreTypeVOPage(Page<CadreType> cadreTypePage, HttpServletRequest request);
}