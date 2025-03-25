package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.school.SchoolAddRequest;
import com.henu.registration.model.dto.school.SchoolQueryRequest;
import com.henu.registration.model.dto.school.SchoolUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 高校信息接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/school")
@Slf4j
public class SchoolController {
	
	@Resource
	private SchoolService schoolService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建高校信息
	 *
	 * @param schoolAddRequest schoolAddRequest
	 * @param request          request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Long> addSchool(@RequestBody SchoolAddRequest schoolAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(schoolAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		School school = new School();
		BeanUtils.copyProperties(schoolAddRequest, school);
		// 数据校验
		schoolService.validSchool(school, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		school.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = schoolService.save(school);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newSchoolId = school.getId();
		return ResultUtils.success(newSchoolId);
	}
	
	/**
	 * 删除高校信息
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSchool(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		School oldSchool = schoolService.getById(id);
		ThrowUtils.throwIf(oldSchool == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldSchool.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = schoolService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新高校信息（仅系统管理员可用）
	 *
	 * @param schoolUpdateRequest schoolUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateSchool(@RequestBody SchoolUpdateRequest schoolUpdateRequest) {
		if (schoolUpdateRequest == null || schoolUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		School school = new School();
		BeanUtils.copyProperties(schoolUpdateRequest, school);
		// 数据校验
		schoolService.validSchool(school, false);
		// 判断是否存在
		long id = schoolUpdateRequest.getId();
		School oldSchool = schoolService.getById(id);
		ThrowUtils.throwIf(oldSchool == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolService.updateById(school);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取高校信息（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SchoolVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SchoolVO> getSchoolVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		School school = schoolService.getById(id);
		ThrowUtils.throwIf(school == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(schoolService.getSchoolVO(school, request));
	}
	
	/**
	 * 分页获取高校信息列表（仅系统管理员可用）
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @return {@link BaseResponse<Page<School>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<School>> listSchoolByPage(@RequestBody SchoolQueryRequest schoolQueryRequest) {
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		return ResultUtils.success(schoolPage);
	}
	
	/**
	 * 分页获取高校信息列表（封装类）
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<SchoolVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SchoolVO>> listSchoolVOByPage(@RequestBody SchoolQueryRequest schoolQueryRequest,
	                                                       HttpServletRequest request) {
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolService.getSchoolVOPage(schoolPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的高校信息列表
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<SchoolVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<SchoolVO>> listMySchoolVOByPage(@RequestBody SchoolQueryRequest schoolQueryRequest,
	                                                         HttpServletRequest request) {
		ThrowUtils.throwIf(schoolQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolQueryRequest.setAdminId(loginAdmin.getId());
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolService.getSchoolVOPage(schoolPage, request));
	}
	
	// endregion
}