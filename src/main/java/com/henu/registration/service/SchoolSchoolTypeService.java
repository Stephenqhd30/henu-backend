package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.schoolSchoolType.SchoolSchoolTypeQueryRequest;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.vo.schoolSchoolType.SchoolSchoolTypeVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 高校与高校类型关联信息服务
 *
 * @author stephenqiu
 * @description 针对表【school_school_type(高校与高校类型关联表)】的数据库操作Service
 * @createDate 2025-03-21 11:46:54
 */
public interface SchoolSchoolTypeService extends IService<SchoolSchoolType> {
	
	/**
	 * 校验数据
	 *
	 * @param schoolSchoolType schoolSchoolType
	 * @param add              对创建的数据进行校验
	 */
	void validSchoolSchoolType(SchoolSchoolType schoolSchoolType, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest
	 * @return {@link QueryWrapper<SchoolSchoolType>}
	 */
	QueryWrapper<SchoolSchoolType> getQueryWrapper(SchoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest);
	
	/**
	 * 获取高校与高校类型关联信息封装
	 *
	 * @param schoolSchoolType schoolSchoolType
	 * @param request          request
	 * @return {@link SchoolSchoolTypeVO}
	 */
	SchoolSchoolTypeVO getSchoolSchoolTypeVO(SchoolSchoolType schoolSchoolType, HttpServletRequest request);
	
	/**
	 * 分页获取高校与高校类型关联信息封装
	 *
	 * @param schoolSchoolTypePage schoolSchoolTypePage
	 * @param request              request
	 * @return {@link Page<SchoolSchoolTypeVO>}
	 */
	Page<SchoolSchoolTypeVO> getSchoolSchoolTypeVOPage(Page<SchoolSchoolType> schoolSchoolTypePage, HttpServletRequest request);
}