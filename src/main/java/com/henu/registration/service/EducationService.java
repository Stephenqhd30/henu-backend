package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.education.EducationQueryRequest;
import com.henu.registration.model.entity.Education;
import com.henu.registration.model.vo.education.EducationVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 教育经历表服务
 *
 * @author stephenqiu
 * @description 针对表【education(教育经历表)】的数据库操作Service
 * @createDate 2025-03-22 13:05:52
 */
public interface EducationService extends IService<Education> {
	
	/**
	 * 校验数据
	 *
	 * @param education education
	 * @param add       对创建的数据进行校验
	 */
	void validEducation(Education education, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param educationQueryRequest educationQueryRequest
	 * @return {@link QueryWrapper<Education>}
	 */
	QueryWrapper<Education> getQueryWrapper(EducationQueryRequest educationQueryRequest);
	
	/**
	 * 获取教育经历表封装
	 *
	 * @param education education
	 * @param request   request
	 * @return {@link EducationVO}
	 */
	EducationVO getEducationVO(Education education, HttpServletRequest request);
	
	/**
	 * 分页获取教育经历表封装
	 *
	 * @param educationPage educationPage
	 * @param request       request
	 * @return {@link Page<EducationVO>}
	 */
	Page<EducationVO> getEducationVOPage(Page<Education> educationPage, HttpServletRequest request);
}