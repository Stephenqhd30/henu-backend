package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.schoolType.SchoolTypeAddRequest;
import com.henu.registration.model.dto.schoolType.SchoolTypeQueryRequest;
import com.henu.registration.model.dto.schoolType.SchoolTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.schoolType.SchoolTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 高校类型接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/schoolType")
@Slf4j
public class SchoolTypeController {
	
	@Resource
	private SchoolTypeService schoolTypeService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建高校类型
	 *
	 * @param schoolTypeAddRequest schoolTypeAddRequest
	 * @param request              request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Long> addSchoolType(@RequestBody SchoolTypeAddRequest schoolTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(schoolTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolTypeAddRequest, schoolType);
		// 数据校验
		schoolTypeService.validSchoolType(schoolType, true);
		
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = schoolTypeService.save(schoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newSchoolTypeId = schoolType.getId();
		return ResultUtils.success(newSchoolTypeId);
	}
	
	/**
	 * 删除高校类型
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> deleteSchoolType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		SchoolType oldSchoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新高校类型（仅系统管理员可用）
	 *
	 * @param schoolTypeUpdateRequest schoolTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateSchoolType(@RequestBody SchoolTypeUpdateRequest schoolTypeUpdateRequest) {
		if (schoolTypeUpdateRequest == null || schoolTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolTypeUpdateRequest, schoolType);
		// 数据校验
		schoolTypeService.validSchoolType(schoolType, false);
		// 判断是否存在
		long id = schoolTypeUpdateRequest.getId();
		SchoolType oldSchoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolTypeService.updateById(schoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取高校类型（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SchoolTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SchoolTypeVO> getSchoolTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		SchoolType schoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(schoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(schoolTypeService.getSchoolTypeVO(schoolType, request));
	}
	
	/**
	 * 分页获取高校类型列表（仅系统管理员可用）
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @return {@link BaseResponse<Page<SchoolType>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<SchoolType>> listSchoolTypeByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest) {
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		return ResultUtils.success(schoolTypePage);
	}
	
	/**
	 * 分页获取高校类型列表（封装类）
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse<Page<SchoolTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SchoolTypeVO>> listSchoolTypeVOByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest,
	                                                               HttpServletRequest request) {
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolTypeService.getSchoolTypeVOPage(schoolTypePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的高校类型列表
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse<Page<SchoolTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<SchoolTypeVO>> listMySchoolTypeVOByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest,
	                                                                 HttpServletRequest request) {
		ThrowUtils.throwIf(schoolTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolTypeService.getSchoolTypeVOPage(schoolTypePage, request));
	}
	// endregion
}