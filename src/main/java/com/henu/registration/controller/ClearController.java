package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 数据清理接口
 * <p>
 * 所有操作均为危险操作，请谨慎调用！
 * </p>
 *
 * @author stephenqiu
 */
@RestController
@RequestMapping("/clear")
@Slf4j
public class ClearController {
	
	@Resource
	private OperationLogService operationLogService;
	
	@Resource
	private DeadlineService deadlineService;
	
	@Resource
	private JobService jobService;
	
	@Resource
	private EducationService educationService;
	
	@Resource
	private FamilyService familyService;
	
	@Resource
	private FileLogService fileLogService;
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	@Resource
	private ReviewLogService reviewLogService;
	
	@Resource
	private MessageService messageService;
	
	@Resource
	private MessageNoticeService messageNoticeService;
	
	@Resource
	private MessagePushService messagePushService;
	
	@Resource
	private SystemMessagesService systemMessagesService;
	
	/**
	 * 清理报名登记表信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/registration/form")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearRegistrationForm() {
		long count = registrationFormService.count();
		if (count > 0) {
			boolean success = registrationFormService.remove(null);
			log.info("清理报名登记表信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无报名登记表信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理文件日志信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/file/log")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearFileLog() {
		long count = fileLogService.count();
		if (count > 0) {
			boolean success = fileLogService.remove(null);
			log.info("清理文件日志信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无文件日志信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理教育经历信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/education")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearEducation() {
		long count = educationService.count();
		if (count > 0) {
			boolean success = educationService.remove(null);
			log.info("清理教育经历信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无教育经历信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理家庭成员信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/family")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearFamily() {
		long count = familyService.count();
		if (count > 0) {
			boolean success = familyService.remove(null);
			log.info("清理家庭成员信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无家庭成员信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理操作日志信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/operation/log")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearOperationLog() {
		long count = operationLogService.count();
		if (count > 0) {
			boolean success = operationLogService.remove(null);
			log.info("清理操作日志信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无操作日志信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理截止日期信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/deadline")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearDeadline() {
		long count = deadlineService.count();
		if (count > 0) {
			boolean success = deadlineService.remove(null);
			log.info("清理截止日期信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无截止日期信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理岗位信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/job")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearJob() {
		long count = jobService.count();
		if (count > 0) {
			boolean success = jobService.remove(null);
			log.info("清理岗位信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无岗位信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理审核记录信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/review/log")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearReviewLog() {
		long count = reviewLogService.count();
		if (count > 0) {
			boolean success = reviewLogService.remove(null);
			log.info("清理审核记录信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无审核记录信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理通知信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/message")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearMessage() {
		long count = messageService.count();
		if (count > 0) {
			boolean success = messageService.remove(null);
			log.info("清理通知信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无通知信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理通知消息信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/message/notice")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearMessageNotice() {
		long count = messageNoticeService.count();
		if (count > 0) {
			boolean success = messageNoticeService.remove(null);
			log.info("清理通知消息信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无通知消息信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理推送消息信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/message/push")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearMessagePush() {
		long count = messagePushService.count();
		if (count > 0) {
			boolean success = messagePushService.remove(null);
			log.info("清理推送消息信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无推送消息信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 清理系统消息信息
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/system/message")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearSystemMessage() {
		long count = systemMessagesService.count();
		if (count > 0) {
			boolean success = systemMessagesService.remove(null);
			log.info("清理系统消息信息，共删除 {} 条记录", count);
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无系统消息信息");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 一键清除所有业务数据
	 *
	 * @return BaseResponse<Boolean>
	 */
	@GetMapping("/all")
	@SaCheckRole(value = AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Boolean> clearAllData() {
		// 文件日志
		if (fileLogService.count() > 0) {
			boolean success = fileLogService.remove(null);
			log.info("清理文件日志信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "文件日志清理失败");
		} else {
			log.info("文件日志信息为空，跳过清理");
		}
		
		// 教育经历
		if (educationService.count() > 0) {
			boolean success = educationService.remove(null);
			log.info("清理教育经历信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "教育经历清理失败");
		} else {
			log.info("教育经历信息为空，跳过清理");
		}
		
		// 家庭成员
		if (familyService.count() > 0) {
			boolean success = familyService.remove(null);
			log.info("清理家庭成员信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "家庭成员清理失败");
		} else {
			log.info("家庭成员信息为空，跳过清理");
		}
		
		// 审核记录
		if (reviewLogService.count() > 0) {
			boolean success = reviewLogService.remove(null);
			log.info("清理审核记录信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "审核记录清理失败");
		} else {
			log.info("审核记录信息为空，跳过清理");
		}
		
		// 截止日期
		if (deadlineService.count() > 0) {
			boolean success = deadlineService.remove(null);
			log.info("清理截止日期信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "截止日期清理失败");
		} else {
			log.info("截止日期信息为空，跳过清理");
		}
		
		// 岗位信息
		if (jobService.count() > 0) {
			boolean success = jobService.remove(null);
			log.info("清理岗位信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "岗位信息清理失败");
		} else {
			log.info("岗位信息为空，跳过清理");
		}
		
		// 操作日志
		if (operationLogService.count() > 0) {
			boolean success = operationLogService.remove(null);
			log.info("清理操作日志信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "操作日志清理失败");
		} else {
			log.info("操作日志信息为空，跳过清理");
		}
		
		if (messageService.count() > 0) {
			boolean success = messageService.remove(null);
			log.info("清理通知信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "清理失败");
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "暂无通知信息");
		}
		
		// 推送消息
		if (messagePushService.count() > 0) {
			boolean success = messagePushService.remove(null);
			log.info("清理推送消息信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "推送消息清理失败");
		} else {
			log.info("推送消息信息为空，跳过清理");
		}
		
		// 通知消息
		if (messageNoticeService.count() > 0) {
			boolean success = messageNoticeService.remove(null);
			log.info("清理通知消息信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "通知消息清理失败");
		} else {
			log.info("通知消息信息为空，跳过清理");
		}
		
		// 系统消息
		if (systemMessagesService.count() > 0) {
			boolean success = systemMessagesService.remove(null);
			log.info("清理系统消息信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "系统消息清理失败");
		} else {
			log.info("系统消息信息为空，跳过清理");
		}
		
		// 报名登记表
		if (registrationFormService.count() > 0) {
			boolean success = registrationFormService.remove(null);
			log.info("清理报名登记表信息");
			ThrowUtils.throwIf(!success, ErrorCode.OPERATION_ERROR, "报名登记表清理失败");
		} else {
			log.info("报名登记表信息为空，跳过清理");
		}
		
		return ResultUtils.success(true);
	}
	
}
