package com.henu.registration.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.family.FamilyAddRequest;
import com.henu.registration.model.dto.family.FamilyEditRequest;
import com.henu.registration.model.dto.family.FamilyQueryRequest;
import com.henu.registration.model.dto.family.FamilyUpdateRequest;
import com.henu.registration.model.entity.Family;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.family.FamilyVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.FamilyService;
import com.henu.registration.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 家庭关系接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/family")
@Slf4j
public class FamilyController {
	
	@Resource
	private FamilyService familyService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建家庭关系
	 *
	 * @param familyAddRequest familyAddRequest
	 * @param request          request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addFamily(@RequestBody FamilyAddRequest familyAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(familyAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Family family = new Family();
		BeanUtils.copyProperties(familyAddRequest, family);
		// 数据校验
		familyService.validFamily(family, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		family.setUserId(loginUser.getId());
		LambdaQueryWrapper<Family> eq = Wrappers.lambdaQuery(Family.class).eq(Family::getUserId, loginUser.getId()).eq(Family::getAppellation, family.getAppellation());
		Family oldFamily = familyService.getOne(eq);
		if (oldFamily != null) {
			family.setId(oldFamily.getId());
		}
		// 写入数据库
		boolean result = familyService.saveOrUpdate(family);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newFamilyId = family.getId();
		return ResultUtils.success(newFamilyId);
	}
	
	/**
	 * 删除家庭关系
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteFamily(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUserPermitNull(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Family oldFamily = familyService.getById(id);
		ThrowUtils.throwIf(oldFamily == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldFamily.getUserId().equals(user.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = familyService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新家庭关系（仅管理员可用）
	 *
	 * @param familyUpdateRequest familyUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateFamily(@RequestBody FamilyUpdateRequest familyUpdateRequest) {
		if (familyUpdateRequest == null || familyUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Family family = new Family();
		BeanUtils.copyProperties(familyUpdateRequest, family);
		// 数据校验
		familyService.validFamily(family, false);
		
		// 判断是否存在
		long id = familyUpdateRequest.getId();
		Family oldFamily = familyService.getById(id);
		ThrowUtils.throwIf(oldFamily == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = familyService.updateById(family);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取家庭关系（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<FamilyVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<FamilyVO> getFamilyVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Family family = familyService.getById(id);
		ThrowUtils.throwIf(family == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(familyService.getFamilyVO(family, request));
	}
	
	/**
	 * 分页获取家庭关系列表（仅管理员可用）
	 *
	 * @param familyQueryRequest familyQueryRequest
	 * @return {@link BaseResponse<Page<Family>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<Family>> listFamilyByPage(@RequestBody FamilyQueryRequest familyQueryRequest) {
		long current = familyQueryRequest.getCurrent();
		long size = familyQueryRequest.getPageSize();
		// 查询数据库
		Page<Family> familyPage = familyService.page(new Page<>(current, size),
				familyService.getQueryWrapper(familyQueryRequest));
		return ResultUtils.success(familyPage);
	}
	
	/**
	 * 分页获取家庭关系列表（封装类）
	 *
	 * @param familyQueryRequest familyQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<FamilyVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<FamilyVO>> listFamilyVOByPage(@RequestBody FamilyQueryRequest familyQueryRequest,
	                                                       HttpServletRequest request) {
		long current = familyQueryRequest.getCurrent();
		long size = familyQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Family> familyPage = familyService.page(new Page<>(current, size),
				familyService.getQueryWrapper(familyQueryRequest));
		// 获取封装类
		return ResultUtils.success(familyService.getFamilyVOPage(familyPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的家庭关系列表
	 *
	 * @param familyQueryRequest familyQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<FamilyVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<FamilyVO>> listMyFamilyVOByPage(@RequestBody FamilyQueryRequest familyQueryRequest,
	                                                         HttpServletRequest request) {
		ThrowUtils.throwIf(familyQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		familyQueryRequest.setUserId(loginUser.getId());
		long current = familyQueryRequest.getCurrent();
		long size = familyQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Family> familyPage = familyService.page(new Page<>(current, size),
				familyService.getQueryWrapper(familyQueryRequest));
		// 获取封装类
		return ResultUtils.success(familyService.getFamilyVOPage(familyPage, request));
	}
	
	/**
	 * 编辑家庭关系（给用户使用）
	 *
	 * @param familyEditRequest familyEditRequest
	 * @param request           request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editFamily(@RequestBody FamilyEditRequest familyEditRequest, HttpServletRequest request) {
		if (familyEditRequest == null || familyEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Family family = new Family();
		BeanUtils.copyProperties(familyEditRequest, family);
		// 数据校验
		familyService.validFamily(family, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = familyEditRequest.getId();
		Family oldFamily = familyService.getById(id);
		ThrowUtils.throwIf(oldFamily == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldFamily.getUserId().equals(loginUser.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = familyService.updateById(family);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}