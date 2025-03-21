package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.school.SchoolQueryRequest;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.school.SchoolVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 高校信息服务
 *
 * @author stephenqiu
 * @description 针对表【school(高校信息)】的数据库操作Service
 * @createDate 2025-03-21 11:09:15
 */
public interface SchoolService extends IService<School> {
	
	/**
	 * 校验数据
	 *
	 * @param school school
	 * @param add    对创建的数据进行校验
	 */
	void validSchool(School school, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @return {@link QueryWrapper<School>}
	 */
	QueryWrapper<School> getQueryWrapper(SchoolQueryRequest schoolQueryRequest);
	
	/**
	 * 获取高校信息封装
	 *
	 * @param school  school
	 * @param request request
	 * @return {@link SchoolVO}
	 */
	SchoolVO getSchoolVO(School school, HttpServletRequest request);
	
	/**
	 * 分页获取高校信息封装
	 *
	 * @param schoolPage schoolPage
	 * @param request    request
	 * @return {@link Page<SchoolVO>}
	 */
	Page<SchoolVO> getSchoolVOPage(Page<School> schoolPage, HttpServletRequest request);
}