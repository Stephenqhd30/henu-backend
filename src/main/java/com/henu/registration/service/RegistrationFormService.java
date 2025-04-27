package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.registrationForm.RegistrationFormQueryRequest;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 报名登记服务
 *
 * @author stephenqiu
 * @description 针对表【registration_form(报名登记表)】的数据库操作Service
 * @createDate 2025-03-22 16:50:24
 */
public interface RegistrationFormService extends IService<RegistrationForm> {
	
	/**
	 * 校验数据
	 *
	 * @param registrationForm registrationForm
	 * @param add              对创建的数据进行校验
	 */
	void validRegistrationForm(RegistrationForm registrationForm, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @return {@link QueryWrapper<RegistrationForm>}
	 */
	QueryWrapper<RegistrationForm> getQueryWrapper(RegistrationFormQueryRequest registrationFormQueryRequest);
	
	/**
	 * 获取查询条件
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @param schoolSchoolTypes            schoolSchoolTypes
	 * @return {@link QueryWrapper<RegistrationForm>}
	 */
	QueryWrapper<RegistrationForm> getQueryWrapper(RegistrationFormQueryRequest registrationFormQueryRequest, List<Long> schoolSchoolTypes);
	
	/**
	 * 获取报名登记封装
	 *
	 * @param registrationForm registrationForm
	 * @param request          request
	 * @return {@link RegistrationFormVO}
	 */
	RegistrationFormVO getRegistrationFormVO(RegistrationForm registrationForm, HttpServletRequest request);
	
	/**
	 * 分页获取报名登记封装
	 *
	 * @param registrationFormPage registrationFormPage
	 * @param request              request
	 * @return {@link Page<RegistrationFormVO>}
	 */
	Page<RegistrationFormVO> getRegistrationFormVOPage(Page<RegistrationForm> registrationFormPage, HttpServletRequest request);
	
	/**
	 * 生成报名编号
	 *
	 * @param id id
	 * @return String
	 */
	String generateRegistrationFormId(Long id);
}