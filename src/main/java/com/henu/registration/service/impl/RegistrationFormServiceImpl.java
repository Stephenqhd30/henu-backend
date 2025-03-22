package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.mapper.RegistrationFormMapper;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.registrationForm.RegistrationFormQueryRequest;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;
import com.henu.registration.service.JobService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.regex.RegexUtils;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * 报名登记服务实现
 *
 * @author stephenqiu
 * @description 针对表【registration_form(报名登记表)】的数据库操作Service实现
 * @createDate 2025-03-22 16:50:24
 */
@Service
@Slf4j
public class RegistrationFormServiceImpl extends ServiceImpl<RegistrationFormMapper, RegistrationForm> implements RegistrationFormService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private JobService jobService;
	
	/**
	 * 校验数据
	 *
	 * @param registrationForm registrationForm
	 * @param add              对创建的数据进行校验
	 */
	@Override
	public void validRegistrationForm(RegistrationForm registrationForm, boolean add) {
		ThrowUtils.throwIf(registrationForm == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String userIdCard = registrationForm.getUserIdCard();
		String userName = registrationForm.getUserName();
		String userEmail = registrationForm.getUserEmail();
		String userPhone = registrationForm.getUserPhone();
		Integer userGender = registrationForm.getUserGender();
		Integer marryStatus = registrationForm.getMarryStatus();
		String emergencyPhone = registrationForm.getEmergencyPhone();
		Long jobId = registrationForm.getJobId();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userEmail), ErrorCode.PARAMS_ERROR, "邮箱不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userGender), ErrorCode.PARAMS_ERROR, "性别不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(marryStatus), ErrorCode.PARAMS_ERROR, "婚姻状况不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(jobId), ErrorCode.PARAMS_ERROR, "岗位信息不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		}
		if (StringUtils.isNotBlank(userEmail)) {
			ThrowUtils.throwIf(!RegexUtils.checkEmail(userEmail), ErrorCode.PARAMS_ERROR, "邮箱输入有误");
		}
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
		if (StringUtils.isNotBlank(emergencyPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(emergencyPhone), ErrorCode.PARAMS_ERROR, "手机号码有误");
		}
		if (ObjectUtils.isNotEmpty(jobId)) {
			Job job = jobService.getById(jobId);
			ThrowUtils.throwIf(job == null, ErrorCode.PARAMS_ERROR, "岗位信息不存在");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @return {@link QueryWrapper<RegistrationForm>}
	 */
	@Override
	public QueryWrapper<RegistrationForm> getQueryWrapper(RegistrationFormQueryRequest registrationFormQueryRequest) {
		QueryWrapper<RegistrationForm> queryWrapper = new QueryWrapper<>();
		if (registrationFormQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = registrationFormQueryRequest.getId();
		Long notId = registrationFormQueryRequest.getNotId();
		String userIdCard = registrationFormQueryRequest.getUserIdCard();
		if (StringUtils.isNotBlank(userIdCard)) {
			userIdCard = userService.getEncryptIdCard(userIdCard);
		}
		String userName = registrationFormQueryRequest.getUserName();
		String userEmail = registrationFormQueryRequest.getUserEmail();
		String userPhone = registrationFormQueryRequest.getUserPhone();
		Integer userGender = registrationFormQueryRequest.getUserGender();
		String ethnic = registrationFormQueryRequest.getEthnic();
		String partyTime = registrationFormQueryRequest.getPartyTime();
		String birthDate = registrationFormQueryRequest.getBirthDate();
		Integer marryStatus = registrationFormQueryRequest.getMarryStatus();
		String emergencyPhone = registrationFormQueryRequest.getEmergencyPhone();
		String address = registrationFormQueryRequest.getAddress();
		String workExperience = registrationFormQueryRequest.getWorkExperience();
		String studentLeaderAwards = registrationFormQueryRequest.getStudentLeaderAwards();
		Long jobId = registrationFormQueryRequest.getJobId();
		Long userId = registrationFormQueryRequest.getUserId();
		String sortField = registrationFormQueryRequest.getSortField();
		String sortOrder = registrationFormQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "user_email", userEmail);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "user_email", userEmail);
		queryWrapper.like(StringUtils.isNotBlank(partyTime), "party_time", partyTime);
		queryWrapper.like(StringUtils.isNotBlank(birthDate), "birth_date", birthDate);
		queryWrapper.like(StringUtils.isNotBlank(address), "address", address);
		queryWrapper.like(StringUtils.isNotBlank(workExperience), "work_experience", workExperience);
		queryWrapper.like(StringUtils.isNotBlank(studentLeaderAwards), "student_leader_awards", studentLeaderAwards);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userPhone), "user_phone", userPhone);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "user_gender", userGender);
		queryWrapper.eq(ObjectUtils.isNotEmpty(ethnic), "ethnic", ethnic);
		queryWrapper.eq(ObjectUtils.isNotEmpty(marryStatus), "marry_status", marryStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(emergencyPhone), "emergency_phone", emergencyPhone);
		queryWrapper.eq(ObjectUtils.isNotEmpty(jobId), "job_id", jobId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userIdCard), "user_id_card", userIdCard);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取报名登记封装
	 *
	 * @param registrationForm registrationForm
	 * @param request          request
	 * @return {@link RegistrationFormVO}
	 */
	@Override
	public RegistrationFormVO getRegistrationFormVO(RegistrationForm registrationForm, HttpServletRequest request) {
		// 对象转封装类
		RegistrationFormVO registrationFormVO = RegistrationFormVO.objToVo(registrationForm);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询岗位信息
		Long jobId = registrationForm.getJobId();
		Job job = null;
		if (jobId != null && jobId > 0) {
			job = jobService.getById(jobId);
		}
		JobVO jobVO = jobService.getJobVO(job, request);
		registrationFormVO.setJobVO(jobVO);
		// endregion
		return registrationFormVO;
	}
	
	/**
	 * 分页获取报名登记封装
	 *
	 * @param registrationFormPage registrationFormPage
	 * @param request              request
	 * @return {@link Page<RegistrationFormVO>}
	 */
	@Override
	public Page<RegistrationFormVO> getRegistrationFormVOPage(Page<RegistrationForm> registrationFormPage, HttpServletRequest request) {
		List<RegistrationForm> registrationFormList = registrationFormPage.getRecords();
		Page<RegistrationFormVO> registrationFormVOPage = new Page<>(registrationFormPage.getCurrent(), registrationFormPage.getSize(), registrationFormPage.getTotal());
		if (CollUtil.isEmpty(registrationFormList)) {
			return registrationFormVOPage;
		}
		// 对象列表 => 封装对象列表
		List<RegistrationFormVO> registrationFormVOList = registrationFormList.stream()
				.map(RegistrationFormVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询岗位信息
		Set<Long> jobIdSet = registrationFormList.stream().map(RegistrationForm::getJobId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(jobIdSet)) {
			CompletableFuture<Map<Long, List<Job>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> jobService.listByIds(jobIdSet).stream()
					.collect(Collectors.groupingBy(Job::getId)));
			try {
				Map<Long, List<Job>> jobIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				registrationFormVOList.forEach(registrationFormVO -> {
					Long jobId = registrationFormVO.getJobId();
					Job job = null;
					if (jobIdUserListMap.containsKey(jobId)) {
						job = jobIdUserListMap.get(jobId).get(0);
					}
					registrationFormVO.setJobVO(jobService.getJobVO(job, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		registrationFormVOPage.setRecords(registrationFormVOList);
		return registrationFormVOPage;
	}
	
}
