package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.cadreType.CadreTypeAddRequest;
import com.henu.registration.model.dto.cadreType.CadreTypeQueryRequest;
import com.henu.registration.model.dto.cadreType.CadreTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.vo.cadreType.CadreTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.CadreTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 干部类型接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/cadreType")
@Slf4j
public class CadreTypeController {
	
	@Resource
	private CadreTypeService cadreTypeService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建干部类型
	 *
	 * @param cadreTypeAddRequest cadreTypeAddRequest
	 * @param request             request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addCadreType(@RequestBody CadreTypeAddRequest cadreTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(cadreTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		CadreType cadreType = new CadreType();
		BeanUtils.copyProperties(cadreTypeAddRequest, cadreType);
		// 数据校验
		cadreTypeService.validCadreType(cadreType, true);
		
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		cadreType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = cadreTypeService.save(cadreType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newCadreTypeId = cadreType.getId();
		return ResultUtils.success(newCadreTypeId);
	}
	
	/**
	 * 删除干部类型
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteCadreType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin Admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		CadreType oldCadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(oldCadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldCadreType.getAdminId().equals(Admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = cadreTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新干部类型（仅系统管理员可用）
	 *
	 * @param cadreTypeUpdateRequest cadreTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateCadreType(@RequestBody CadreTypeUpdateRequest cadreTypeUpdateRequest) {
		if (cadreTypeUpdateRequest == null || cadreTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		CadreType cadreType = new CadreType();
		BeanUtils.copyProperties(cadreTypeUpdateRequest, cadreType);
		// 数据校验
		cadreTypeService.validCadreType(cadreType, false);
		
		// 判断是否存在
		long id = cadreTypeUpdateRequest.getId();
		CadreType oldCadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(oldCadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = cadreTypeService.updateById(cadreType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取干部类型（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<CadreTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<CadreTypeVO> getCadreTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		CadreType cadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(cadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(cadreTypeService.getCadreTypeVO(cadreType, request));
	}
	
	/**
	 * 分页获取干部类型列表（仅系统管理员可用）
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @return {@link BaseResponse<Page<CadreType>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<CadreType>> listCadreTypeByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest) {
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		return ResultUtils.success(cadreTypePage);
	}
	
	/**
	 * 分页获取干部类型列表（封装类）
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<CadreTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<CadreTypeVO>> listCadreTypeVOByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest,
	                                                             HttpServletRequest request) {
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(cadreTypeService.getCadreTypeVOPage(cadreTypePage, request));
	}
	
	/**
	 * 分页获取当前登录管理员创建的干部类型列表
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<CadreTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<CadreTypeVO>> listMyCadreTypeVOByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest,
	                                                               HttpServletRequest request) {
		ThrowUtils.throwIf(cadreTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		cadreTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(cadreTypeService.getCadreTypeVOPage(cadreTypePage, request));
	}
	
	// endregion
}