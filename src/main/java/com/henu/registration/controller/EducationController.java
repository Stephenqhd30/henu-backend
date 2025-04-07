package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.education.EducationAddRequest;
import com.henu.registration.model.dto.education.EducationEditRequest;
import com.henu.registration.model.dto.education.EducationQueryRequest;
import com.henu.registration.model.dto.education.EducationUpdateRequest;
import com.henu.registration.model.entity.Education;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.education.EducationVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.EducationService;
import com.henu.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 教育经历表接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/education")
@Slf4j
public class EducationController {
	
	@Resource
	private EducationService educationService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建教育经历表
	 *
	 * @param educationAddRequest educationAddRequest
	 * @param request             request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addEducation(@RequestBody EducationAddRequest educationAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(educationAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Education education = new Education();
		BeanUtils.copyProperties(educationAddRequest, education);
		// 数据校验
		educationService.validEducation(education, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		education.setUserId(loginUser.getId());
		LambdaQueryWrapper<Education> eq = Wrappers.lambdaQuery(Education.class)
				.eq(Education::getUserId, loginUser.getId())
				.eq(Education::getEducationalStage, education.getEducationalStage());
		Education oldEducation = educationService.getOne(eq);
		if (oldEducation != null) {
			education.setId(oldEducation.getId());
		}
		// 写入数据库
		boolean result = educationService.saveOrUpdate(education);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newEducationId = education.getId();
		return ResultUtils.success(newEducationId);
	}
	
	/**
	 * 删除教育经历表
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteEducation(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Education oldEducation = educationService.getById(id);
		ThrowUtils.throwIf(oldEducation == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldEducation.getUserId().equals(user.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = educationService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新教育经历表
	 *
	 * @param educationUpdateRequest educationUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateEducation(@RequestBody EducationUpdateRequest educationUpdateRequest) {
		if (educationUpdateRequest == null || educationUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Education education = new Education();
		BeanUtils.copyProperties(educationUpdateRequest, education);
		// 数据校验
		educationService.validEducation(education, false);
		// 判断是否存在
		long id = educationUpdateRequest.getId();
		Education oldEducation = educationService.getById(id);
		ThrowUtils.throwIf(oldEducation == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = educationService.updateById(education);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取教育经历表（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<EducationVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<EducationVO> getEducationVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Education education = educationService.getById(id);
		ThrowUtils.throwIf(education == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(educationService.getEducationVO(education, request));
	}
	
	/**
	 * 分页获取教育经历表列表
	 *
	 * @param educationQueryRequest educationQueryRequest
	 * @return {@link BaseResponse<Page<Education>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<Education>> listEducationByPage(@RequestBody EducationQueryRequest educationQueryRequest) {
		long current = educationQueryRequest.getCurrent();
		long size = educationQueryRequest.getPageSize();
		// 查询数据库
		Page<Education> educationPage = educationService.page(new Page<>(current, size),
				educationService.getQueryWrapper(educationQueryRequest));
		return ResultUtils.success(educationPage);
	}
	
	/**
	 * 分页获取教育经历表列表（封装类）
	 *
	 * @param educationQueryRequest educationQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<EducationVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<EducationVO>> listEducationVOByPage(@RequestBody EducationQueryRequest educationQueryRequest,
	                                                             HttpServletRequest request) {
		long current = educationQueryRequest.getCurrent();
		long size = educationQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Education> educationPage = educationService.page(new Page<>(current, size),
				educationService.getQueryWrapper(educationQueryRequest));
		// 获取封装类
		return ResultUtils.success(educationService.getEducationVOPage(educationPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的教育经历表列表
	 *
	 * @param educationQueryRequest educationQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<EducationVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<EducationVO>> listMyEducationVOByPage(@RequestBody EducationQueryRequest educationQueryRequest,
	                                                               HttpServletRequest request) {
		ThrowUtils.throwIf(educationQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		educationQueryRequest.setUserId(loginUser.getId());
		long current = educationQueryRequest.getCurrent();
		long size = educationQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Education> educationPage = educationService.page(new Page<>(current, size),
				educationService.getQueryWrapper(educationQueryRequest));
		// 获取封装类
		return ResultUtils.success(educationService.getEducationVOPage(educationPage, request));
	}
	
	/**
	 * 编辑教育经历表（给用户使用）
	 *
	 * @param educationEditRequest educationEditRequest
	 * @param request              request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editEducation(@RequestBody EducationEditRequest educationEditRequest, HttpServletRequest request) {
		if (educationEditRequest == null || educationEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Education education = new Education();
		BeanUtils.copyProperties(educationEditRequest, education);
		// 数据校验
		educationService.validEducation(education, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = educationEditRequest.getId();
		Education oldEducation = educationService.getById(id);
		ThrowUtils.throwIf(oldEducation == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldEducation.getUserId().equals(loginUser.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = educationService.updateById(education);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}