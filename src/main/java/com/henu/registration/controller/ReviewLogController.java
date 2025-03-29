package com.henu.registration.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.DeleteRequest;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.model.dto.reviewLog.ReviewLogAddRequest;
import com.henu.registration.model.dto.reviewLog.ReviewLogQueryRequest;
import com.henu.registration.model.dto.reviewLog.ReviewLogUpdateRequest;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.entity.ReviewLog;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.enums.ReviewStatusEnum;
import com.henu.registration.model.vo.reviewLog.ReviewLogVO;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.service.ReviewLogService;
import com.henu.registration.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 审核记录接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/reviewLog")
@Slf4j
public class ReviewLogController {
	
	@Resource
	private ReviewLogService reviewLogService;
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	// region 增删改查
	
	/**
	 * 创建审核记录
	 *
	 * @param reviewLogAddRequest reviewLogAddRequest
	 * @param request             request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@Transactional
	public BaseResponse<Long> addReviewLog(@RequestBody ReviewLogAddRequest reviewLogAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(reviewLogAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		ReviewLog reviewLog = new ReviewLog();
		BeanUtils.copyProperties(reviewLogAddRequest, reviewLog);
		// 数据校验
		reviewLogService.validReviewLog(reviewLog, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		reviewLog.setReviewerId(loginAdmin.getId());
		reviewLog.setReviewTime(new Date());
		// 写入数据库
		boolean result = reviewLogService.save(reviewLog);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 同步修改登记表的审核状态信息
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setId(reviewLog.getRegistrationId());
		registrationForm.setReviewer(loginAdmin.getAdminName());
		registrationForm.setReviewStatus(reviewLog.getReviewStatus());
		registrationForm.setReviewTime(new Date());
		registrationForm.setReviewComments(reviewLog.getReviewComments());
		boolean b = registrationFormService.updateById(registrationForm);
		ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newReviewLogId = reviewLog.getId();
		return ResultUtils.success(newReviewLogId);
	}
	
	/**
	 * 批量创建审核记录
	 *
	 * @param reviewLogAddRequest reviewLogAddRequest
	 * @param request             request
	 * @return {@link BaseResponse<String>}
	 */
	@PostMapping("/add/batch")
	@Transactional
	public BaseResponse<String> batchAddReviewLogs(@RequestBody ReviewLogAddRequest reviewLogAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(reviewLogAddRequest == null, ErrorCode.PARAMS_ERROR);
		// 校验批量审核的 registrationIds
		List<Long> registrationIds = reviewLogAddRequest.getRegistrationIds();
		ThrowUtils.throwIf(registrationIds == null || registrationIds.isEmpty(), ErrorCode.PARAMS_ERROR);
		Admin loginAdmin = adminService.getLoginAdmin(request);
		// 逐个处理每个 registrationId
		for (Long registrationId : registrationIds) {
			// 创建审核记录
			ReviewLog reviewLog = new ReviewLog();
			reviewLog.setRegistrationId(registrationId);
			reviewLog.setReviewerId(loginAdmin.getId());
			reviewLog.setReviewStatus(reviewLogAddRequest.getReviewStatus());
			reviewLog.setReviewComments(reviewLogAddRequest.getReviewComments());
			reviewLog.setReviewTime(new Date());
			// 数据校验
			reviewLogService.validReviewLog(reviewLog, true);
			// 保存审核记录
			boolean result = reviewLogService.save(reviewLog);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
			// 同步修改登记表的审核状态信息
			RegistrationForm registrationForm = new RegistrationForm();
			registrationForm.setId(registrationId);
			registrationForm.setReviewer(loginAdmin.getAdminName());
			registrationForm.setReviewStatus(reviewLog.getReviewStatus());
			registrationForm.setReviewTime(new Date());
			registrationForm.setReviewComments(reviewLog.getReviewComments());
			boolean b = registrationFormService.updateById(registrationForm);
			ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
		}
		
		// 返回批量审核完成的成功消息
		return ResultUtils.success("批量审核完成");
	}
	
	/**
	 * 删除审核记录
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	@Transactional
	public BaseResponse<Boolean> deleteReviewLog(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		ReviewLog oldReviewLog = reviewLogService.getById(id);
		ThrowUtils.throwIf(oldReviewLog == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldReviewLog.getReviewerId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = reviewLogService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 同步修改登记表的审核状态信息
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setId(oldReviewLog.getRegistrationId());
		registrationForm.setReviewer(null);
		registrationForm.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
		registrationForm.setReviewTime(new Date());
		registrationForm.setReviewComments("报名登记信息修改，需管理员重新审核");
		boolean b = registrationFormService.updateById(registrationForm);
		ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新审核记录（仅管理员可用）
	 *
	 * @param reviewLogUpdateRequest reviewLogUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	@Transactional
	public BaseResponse<Boolean> updateReviewLog(@RequestBody ReviewLogUpdateRequest reviewLogUpdateRequest, HttpServletRequest request) {
		if (reviewLogUpdateRequest == null || reviewLogUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		ReviewLog reviewLog = new ReviewLog();
		BeanUtils.copyProperties(reviewLogUpdateRequest, reviewLog);
		// 数据校验
		reviewLogService.validReviewLog(reviewLog, false);
		// 判断是否存在
		long id = reviewLogUpdateRequest.getId();
		ReviewLog oldReviewLog = reviewLogService.getById(id);
		ThrowUtils.throwIf(oldReviewLog == null, ErrorCode.NOT_FOUND_ERROR);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		reviewLog.setReviewerId(loginAdmin.getId());
		reviewLog.setReviewTime(new Date());
		// 操作数据库
		boolean result = reviewLogService.updateById(reviewLog);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 同步修改登记表的审核状态信息
		RegistrationForm registrationForm = new RegistrationForm();
		registrationForm.setId(reviewLog.getRegistrationId());
		registrationForm.setReviewer(loginAdmin.getAdminName());
		registrationForm.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
		registrationForm.setReviewTime(new Date());
		registrationForm.setReviewComments("报名登记信息修改，需管理员重新审核");
		boolean b = registrationFormService.updateById(registrationForm);
		ThrowUtils.throwIf(!b, ErrorCode.OPERATION_ERROR);
		
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取审核记录（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<ReviewLogVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<ReviewLogVO> getReviewLogVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		ReviewLog reviewLog = reviewLogService.getById(id);
		ThrowUtils.throwIf(reviewLog == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(reviewLogService.getReviewLogVO(reviewLog, request));
	}
	
	/**
	 * 分页获取审核记录列表（仅管理员可用）
	 *
	 * @param reviewLogQueryRequest reviewLogQueryRequest
	 * @return {@link BaseResponse<Page<ReviewLog>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<ReviewLog>> listReviewLogByPage(@RequestBody ReviewLogQueryRequest reviewLogQueryRequest) {
		long current = reviewLogQueryRequest.getCurrent();
		long size = reviewLogQueryRequest.getPageSize();
		// 查询数据库
		Page<ReviewLog> reviewLogPage = reviewLogService.page(new Page<>(current, size),
				reviewLogService.getQueryWrapper(reviewLogQueryRequest));
		return ResultUtils.success(reviewLogPage);
	}
	
	/**
	 * 分页获取审核记录列表（封装类）
	 *
	 * @param reviewLogQueryRequest reviewLogQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<ReviewLogVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<ReviewLogVO>> listReviewLogVOByPage(@RequestBody ReviewLogQueryRequest reviewLogQueryRequest,
	                                                             HttpServletRequest request) {
		long current = reviewLogQueryRequest.getCurrent();
		long size = reviewLogQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<ReviewLog> reviewLogPage = reviewLogService.page(new Page<>(current, size),
				reviewLogService.getQueryWrapper(reviewLogQueryRequest));
		// 获取封装类
		return ResultUtils.success(reviewLogService.getReviewLogVOPage(reviewLogPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的审核记录列表
	 *
	 * @param reviewLogQueryRequest reviewLogQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<ReviewLogVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<ReviewLogVO>> listMyReviewLogVOByPage(@RequestBody ReviewLogQueryRequest reviewLogQueryRequest,
	                                                               HttpServletRequest request) {
		ThrowUtils.throwIf(reviewLogQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		reviewLogQueryRequest.setReviewerId(loginAdmin.getId());
		long current = reviewLogQueryRequest.getCurrent();
		long size = reviewLogQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<ReviewLog> reviewLogPage = reviewLogService.page(new Page<>(current, size),
				reviewLogService.getQueryWrapper(reviewLogQueryRequest));
		// 获取封装类
		return ResultUtils.success(reviewLogService.getReviewLogVOPage(reviewLogPage, request));
	}
	
	// endregion
}