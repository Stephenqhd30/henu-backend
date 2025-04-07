package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.schoolSchoolType.SchoolSchoolTypeAddRequest;
import com.henu.registration.model.dto.schoolSchoolType.SchoolSchoolTypeQueryRequest;
import com.henu.registration.model.dto.schoolSchoolType.SchoolSchoolTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.vo.schoolSchoolType.SchoolSchoolTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolSchoolTypeService;
import com.henu.registration.service.SchoolService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 高校与高校类型关联信息接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/schoolSchoolType")
@Slf4j
public class SchoolSchoolTypeController {
	
	@Resource
	private SchoolSchoolTypeService schoolSchoolTypeService;
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private SchoolService schoolService;
	
	// region 增删改查
	
	/**
	 * 创建高校与高校类型关联信息
	 *
	 * @param schoolSchoolTypeAddRequest schoolSchoolTypeAddRequest
	 * @param request                    request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addSchoolSchoolType(@RequestBody SchoolSchoolTypeAddRequest schoolSchoolTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(schoolSchoolTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// 数据校验
		List<String> schoolTypeList = schoolSchoolTypeAddRequest.getSchoolTypes();
		// 检查学校类型是否为空
		ThrowUtils.throwIf(schoolTypeList.isEmpty(), ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		SchoolSchoolType schoolSchoolType = new SchoolSchoolType();
		BeanUtils.copyProperties(schoolSchoolTypeAddRequest, schoolSchoolType);
		String schoolTypes = JSONUtil.toJsonStr(schoolSchoolTypeAddRequest.getSchoolTypes());
		schoolSchoolType.setSchoolTypes(schoolTypes);
		String schoolName = schoolSchoolTypeAddRequest.getSchoolName();
		if (StringUtils.isNotBlank(schoolName)) {
			LambdaQueryWrapper<School> eq = Wrappers.lambdaQuery(School.class).eq(School::getSchoolName, schoolName);
			School school = schoolService.getOne(eq);
			ThrowUtils.throwIf(school == null, ErrorCode.PARAMS_ERROR, "高校不存在");
			schoolSchoolType.setSchoolId(school.getId());
		}
		// 数据校验
		schoolSchoolTypeService.validSchoolSchoolType(schoolSchoolType, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolSchoolType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = schoolSchoolTypeService.save(schoolSchoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 获取插入的ID列表
		Long id = schoolSchoolType.getId();
		// 返回新写入的数据ID
		return ResultUtils.success(id);
	}
	
	/**
	 * 删除高校与高校类型关联信息
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSchoolSchoolType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		SchoolSchoolType oldSchoolSchoolType = schoolSchoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolSchoolTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新高校与高校类型关联信息（仅系统管理员可用）
	 *
	 * @param schoolSchoolTypeUpdateRequest schoolSchoolTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateSchoolSchoolType(@RequestBody SchoolSchoolTypeUpdateRequest schoolSchoolTypeUpdateRequest) {
		if (schoolSchoolTypeUpdateRequest == null || schoolSchoolTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		SchoolSchoolType schoolSchoolType = new SchoolSchoolType();
		BeanUtils.copyProperties(schoolSchoolTypeUpdateRequest, schoolSchoolType);
		String schoolTypes = JSONUtil.toJsonStr(schoolSchoolTypeUpdateRequest.getSchoolTypes());
		schoolSchoolType.setSchoolTypes(schoolTypes);
		String schoolName = schoolSchoolTypeUpdateRequest.getSchoolName();
		if (StringUtils.isNotBlank(schoolName)) {
			LambdaQueryWrapper<School> eq = Wrappers.lambdaQuery(School.class).eq(School::getSchoolName, schoolName);
			School school = schoolService.getOne(eq);
			ThrowUtils.throwIf(school == null, ErrorCode.PARAMS_ERROR, "高校不存在");
			schoolSchoolType.setSchoolId(school.getId());
		}
		// 数据校验
		schoolSchoolTypeService.validSchoolSchoolType(schoolSchoolType, false);
		
		// 判断是否存在
		long id = schoolSchoolTypeUpdateRequest.getId();
		SchoolSchoolType oldSchoolSchoolType = schoolSchoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolSchoolTypeService.updateById(schoolSchoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取高校与高校类型关联信息（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SchoolSchoolTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SchoolSchoolTypeVO> getSchoolSchoolTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		SchoolSchoolType schoolSchoolType = schoolSchoolTypeService.getById(id);
		ThrowUtils.throwIf(schoolSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(schoolSchoolTypeService.getSchoolSchoolTypeVO(schoolSchoolType, request));
	}
	
	/**
	 * 分页获取高校与高校类型关联信息列表（仅系统管理员可用）
	 *
	 * @param schoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest
	 * @return {@link BaseResponse<Page<SchoolSchoolType>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<SchoolSchoolType>> listSchoolSchoolTypeByPage(@RequestBody SchoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest) {
		long current = schoolSchoolTypeQueryRequest.getCurrent();
		long size = schoolSchoolTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<SchoolSchoolType> schoolSchoolTypePage = schoolSchoolTypeService.page(new Page<>(current, size),
				schoolSchoolTypeService.getQueryWrapper(schoolSchoolTypeQueryRequest));
		return ResultUtils.success(schoolSchoolTypePage);
	}
	
	/**
	 * 分页获取高校与高校类型关联信息列表（封装类）
	 *
	 * @param schoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest
	 * @param request                      request
	 * @return {@link BaseResponse<Page<SchoolSchoolTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SchoolSchoolTypeVO>> listSchoolSchoolTypeVOByPage(@RequestBody SchoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest,
	                                                                           HttpServletRequest request) {
		long current = schoolSchoolTypeQueryRequest.getCurrent();
		long size = schoolSchoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SchoolSchoolType> schoolSchoolTypePage = schoolSchoolTypeService.page(new Page<>(current, size),
				schoolSchoolTypeService.getQueryWrapper(schoolSchoolTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolSchoolTypeService.getSchoolSchoolTypeVOPage(schoolSchoolTypePage, request));
	}
	
	/**
	 * 分页获取当前登录管理员创建的高校与高校类型关联信息列表
	 *
	 * @param schoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest
	 * @param request                      request
	 * @return {@link BaseResponse<Page<SchoolSchoolTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<SchoolSchoolTypeVO>> listMySchoolSchoolTypeVOByPage(@RequestBody SchoolSchoolTypeQueryRequest schoolSchoolTypeQueryRequest,
	                                                                             HttpServletRequest request) {
		ThrowUtils.throwIf(schoolSchoolTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录管理员的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolSchoolTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = schoolSchoolTypeQueryRequest.getCurrent();
		long size = schoolSchoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SchoolSchoolType> schoolSchoolTypePage = schoolSchoolTypeService.page(new Page<>(current, size),
				schoolSchoolTypeService.getQueryWrapper(schoolSchoolTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolSchoolTypeService.getSchoolSchoolTypeVOPage(schoolSchoolTypePage, request));
	}
	// endregion
}