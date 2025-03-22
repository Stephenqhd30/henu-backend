package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.deadline.DeadlineAddRequest;
import com.henu.registration.model.dto.deadline.DeadlineQueryRequest;
import com.henu.registration.model.dto.deadline.DeadlineUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.vo.deadline.DeadlineVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.DeadlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 截止时间接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/deadline")
@Slf4j
public class DeadlineController {
	
	@Resource
	private DeadlineService deadlineService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建截止时间
	 *
	 * @param deadlineAddRequest deadlineAddRequest
	 * @param request            request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addDeadline(@RequestBody DeadlineAddRequest deadlineAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(deadlineAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Deadline deadline = new Deadline();
		BeanUtils.copyProperties(deadlineAddRequest, deadline);
		// 数据校验
		deadlineService.validDeadline(deadline, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		deadline.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = deadlineService.save(deadline);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newDeadlineId = deadline.getId();
		return ResultUtils.success(newDeadlineId);
	}
	
	/**
	 * 删除截止时间
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteDeadline(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Deadline oldDeadline = deadlineService.getById(id);
		ThrowUtils.throwIf(oldDeadline == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或系统管理员可删除
		if (!oldDeadline.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = deadlineService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新截止时间（仅系统管理员可用）
	 *
	 * @param deadlineUpdateRequest deadlineUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> updateDeadline(@RequestBody DeadlineUpdateRequest deadlineUpdateRequest) {
		if (deadlineUpdateRequest == null || deadlineUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Deadline deadline = new Deadline();
		BeanUtils.copyProperties(deadlineUpdateRequest, deadline);
		// 数据校验
		deadlineService.validDeadline(deadline, false);
		
		// 判断是否存在
		long id = deadlineUpdateRequest.getId();
		Deadline oldDeadline = deadlineService.getById(id);
		ThrowUtils.throwIf(oldDeadline == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = deadlineService.updateById(deadline);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取截止时间（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<DeadlineVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<DeadlineVO> getDeadlineVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Deadline deadline = deadlineService.getById(id);
		ThrowUtils.throwIf(deadline == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(deadlineService.getDeadlineVO(deadline, request));
	}
	
	/**
	 * 分页获取截止时间列表（仅管理员可用）
	 *
	 * @param deadlineQueryRequest deadlineQueryRequest
	 * @return {@link BaseResponse<Page<Deadline>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<Deadline>> listDeadlineByPage(@RequestBody DeadlineQueryRequest deadlineQueryRequest) {
		long current = deadlineQueryRequest.getCurrent();
		long size = deadlineQueryRequest.getPageSize();
		// 查询数据库
		Page<Deadline> deadlinePage = deadlineService.page(new Page<>(current, size),
				deadlineService.getQueryWrapper(deadlineQueryRequest));
		return ResultUtils.success(deadlinePage);
	}
	
	/**
	 * 分页获取截止时间列表（封装类）
	 *
	 * @param deadlineQueryRequest deadlineQueryRequest
	 * @param request              request
	 * @return {@link BaseResponse<Page<DeadlineVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<DeadlineVO>> listDeadlineVOByPage(@RequestBody DeadlineQueryRequest deadlineQueryRequest,
	                                                           HttpServletRequest request) {
		long current = deadlineQueryRequest.getCurrent();
		long size = deadlineQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Deadline> deadlinePage = deadlineService.page(new Page<>(current, size),
				deadlineService.getQueryWrapper(deadlineQueryRequest));
		// 获取封装类
		return ResultUtils.success(deadlineService.getDeadlineVOPage(deadlinePage, request));
	}
	
	/**
	 * 分页获取当前登录管理员创建的截止时间列表
	 *
	 * @param deadlineQueryRequest deadlineQueryRequest
	 * @param request              request
	 * @return {@link BaseResponse<Page<DeadlineVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<DeadlineVO>> listMyDeadlineVOByPage(@RequestBody DeadlineQueryRequest deadlineQueryRequest,
	                                                             HttpServletRequest request) {
		ThrowUtils.throwIf(deadlineQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		deadlineQueryRequest.setAdminId(loginAdmin.getId());
		long current = deadlineQueryRequest.getCurrent();
		long size = deadlineQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Deadline> deadlinePage = deadlineService.page(new Page<>(current, size),
				deadlineService.getQueryWrapper(deadlineQueryRequest));
		// 获取封装类
		return ResultUtils.success(deadlineService.getDeadlineVOPage(deadlinePage, request));
	}
	
	// endregion
}