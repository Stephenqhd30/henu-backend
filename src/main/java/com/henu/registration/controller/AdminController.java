package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.DeleteRequest;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.constants.UserConstant;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.model.dto.admin.*;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.admin.LoginAdminVO;
import com.henu.registration.model.vo.user.LoginUserVO;
import com.henu.registration.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 管理员接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/admin")
@Slf4j
public class AdminController {
	
	@Resource
	private AdminService adminService;
	
	/**
	 * 用户登录
	 *
	 * @param adminLoginRequest userLoginRequest
	 * @param request           request
	 * @return BaseResponse<LoginUserVO>
	 */
	@PostMapping("/login")
	public BaseResponse<LoginAdminVO> adminLogin(@RequestBody AdminLoginRequest adminLoginRequest, HttpServletRequest request) {
		if (adminLoginRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String adminNumber = adminLoginRequest.getAdminNumber();
		String adminPassword = adminLoginRequest.getAdminPassword();
		ThrowUtils.throwIf(StringUtils.isAnyBlank(adminNumber, adminPassword), ErrorCode.PARAMS_ERROR);
		LoginAdminVO loginAdminVO = adminService.adminLogin(adminNumber, adminPassword, request);
		return ResultUtils.success(loginAdminVO);
	}
	
	/**
	 * 获取当前登录的管理员
	 *
	 * @param request request
	 * @return BaseResponse<LoginUserVO>
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginAdminVO> getLoginAdmin(HttpServletRequest request) {
		Admin admin = adminService.getLoginAdmin(request);
		return ResultUtils.success(adminService.getLoginAdminVO(admin));
	}
	// region 增删改查
	
	/**
	 * 创建管理员
	 *
	 * @param adminAddRequest adminAddRequest
	 * @param request         request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Long> addAdmin(@RequestBody AdminAddRequest adminAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(adminAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminAddRequest, admin);
		// 数据校验
		adminService.validAdmin(admin, true);
		// 对密码进行加密
		admin.setAdminPassword(adminService.getEncryptPassword(admin.getAdminPassword()));
		// 写入数据库
		boolean result = adminService.save(admin);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newAdminId = admin.getId();
		return ResultUtils.success(newAdminId);
	}
	
	/**
	 * 删除管理员
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteAdmin(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		Admin oldAdmin = adminService.getById(id);
		ThrowUtils.throwIf(oldAdmin == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅系统管理员可删除
		if (!adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = adminService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新管理员（仅管理员可用）
	 *
	 * @param adminUpdateRequest adminUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateAdmin(@RequestBody AdminUpdateRequest adminUpdateRequest, HttpServletRequest request) {
		if (adminUpdateRequest == null || adminUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminUpdateRequest, admin);
		// 数据校验
		adminService.validAdmin(admin, false);
		// 仅系统管理员可用
		if (!adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 判断是否存在
		long id = adminUpdateRequest.getId();
		Admin oldAdmin = adminService.getById(id);
		ThrowUtils.throwIf(oldAdmin == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = adminService.updateById(admin);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取管理员（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<AdminVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<AdminVO> getAdminVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Admin admin = adminService.getById(id);
		ThrowUtils.throwIf(admin == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(adminService.getAdminVO(admin, request));
	}
	
	/**
	 * 分页获取管理员列表（仅管理员可用）
	 *
	 * @param adminQueryRequest adminQueryRequest
	 * @return {@link BaseResponse<Page<Admin>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<Admin>> listAdminByPage(@RequestBody AdminQueryRequest adminQueryRequest) {
		long current = adminQueryRequest.getCurrent();
		long size = adminQueryRequest.getPageSize();
		// 查询数据库
		Page<Admin> adminPage = adminService.page(new Page<>(current, size),
				adminService.getQueryWrapper(adminQueryRequest));
		return ResultUtils.success(adminPage);
	}
	
	/**
	 * 分页获取管理员列表（封装类）
	 *
	 * @param adminQueryRequest adminQueryRequest
	 * @param request           request
	 * @return {@link BaseResponse<Page<AdminVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<AdminVO>> listAdminVOByPage(@RequestBody AdminQueryRequest adminQueryRequest,
	                                                     HttpServletRequest request) {
		long current = adminQueryRequest.getCurrent();
		long size = adminQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Admin> adminPage = adminService.page(new Page<>(current, size),
				adminService.getQueryWrapper(adminQueryRequest));
		// 获取封装类
		return ResultUtils.success(adminService.getAdminVOPage(adminPage, request));
	}
	
	/**
	 * 编辑管理员（给自己使用）
	 *
	 * @param adminEditRequest adminEditRequest
	 * @param request          request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editAdmin(@RequestBody AdminEditRequest adminEditRequest, HttpServletRequest request) {
		if (adminEditRequest == null || adminEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminEditRequest, admin);
		// 数据校验
		adminService.validAdmin(admin, false);
		// 判断是否存在
		long id = adminEditRequest.getId();
		Admin oldAdmin = adminService.getById(id);
		ThrowUtils.throwIf(oldAdmin == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = adminService.updateById(admin);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}