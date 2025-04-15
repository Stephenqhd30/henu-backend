package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.RegistrationFormMapper;
import com.henu.registration.model.dto.registrationForm.RegistrationFormQueryRequest;
import com.henu.registration.model.entity.*;
import com.henu.registration.model.enums.PoliticalStatusEnum;
import com.henu.registration.model.vo.education.EducationVO;
import com.henu.registration.model.vo.family.FamilyVO;
import com.henu.registration.model.vo.fileLog.FileLogVO;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.model.vo.messageNotice.MessageNoticeVO;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;
import com.henu.registration.service.*;
import com.henu.registration.utils.regex.RegexUtils;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
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
	private FileLogService fileLogService;
	
	@Resource
	private JobService jobService;
	
	@Resource
	private EducationService educationService;
	
	@Resource
	private FamilyService familyService;
	
	@Resource
	@Lazy
	private MessageNoticeService messageNoticeService;
	
	@Resource
	private ThreadPoolExecutor executor;
	
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
		String politicalStatus = registrationForm.getPoliticalStatus();
		String emergencyPhone = registrationForm.getEmergencyPhone();
		Long jobId = registrationForm.getJobId();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(politicalStatus), ErrorCode.PARAMS_ERROR, "政治面貌不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userGender), ErrorCode.PARAMS_ERROR, "性别不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(marryStatus), ErrorCode.PARAMS_ERROR, "婚姻状况不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(jobId), ErrorCode.PARAMS_ERROR, "岗位信息不能为空");
			RegistrationForm isRegistered = this.getOne(Wrappers.lambdaQuery(RegistrationForm.class)
					.eq(RegistrationForm::getUserPhone, userPhone)
					.eq(RegistrationForm::getUserName, userName)
					.eq(RegistrationForm::getJobId, jobId));
			ThrowUtils.throwIf(isRegistered != null, ErrorCode.PARAMS_ERROR, "该用户已报名");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		}
		if (StringUtils.isNotBlank(politicalStatus)) {
			ThrowUtils.throwIf(PoliticalStatusEnum.getEnumByValue(politicalStatus) == null, ErrorCode.PARAMS_ERROR, "政治面貌输入有误");
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
		String userName = registrationFormQueryRequest.getUserName();
		Integer userGender = registrationFormQueryRequest.getUserGender();
		Integer marryStatus = registrationFormQueryRequest.getMarryStatus();
		Integer reviewStatus = registrationFormQueryRequest.getReviewStatus();
		String reviewer = registrationFormQueryRequest.getReviewer();
		Integer registrationStatus = registrationFormQueryRequest.getRegistrationStatus();
		String politicalStatus = registrationFormQueryRequest.getPoliticalStatus();
		String birthDate = registrationFormQueryRequest.getBirthDate();
		String workExperience = registrationFormQueryRequest.getWorkExperience();
		List<String> studentLeaders = registrationFormQueryRequest.getStudentLeaders();
		String studentAwards = registrationFormQueryRequest.getStudentAwards();
		Long jobId = registrationFormQueryRequest.getJobId();
		Long userId = registrationFormQueryRequest.getUserId();
		String sortField = registrationFormQueryRequest.getSortField();
		String sortOrder = registrationFormQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 多字段查询
		if (CollUtil.isNotEmpty(studentLeaders)) {
			for (String leader : studentLeaders) {
				queryWrapper.like("student_leaders", "\"" + leader + "\"");
			}
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
		queryWrapper.like(StringUtils.isNotBlank(workExperience), "work_experience", workExperience);
		queryWrapper.like(StringUtils.isNotBlank(studentAwards), "student_awards", studentAwards);
		queryWrapper.gt(ObjUtil.isNotEmpty(birthDate), "birth_date", birthDate);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "review_status", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "user_gender", userGender);
		queryWrapper.eq(ObjectUtils.isNotEmpty(marryStatus), "marry_status", marryStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(jobId), "job_id", jobId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "review_status", reviewStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewer), "reviewer", reviewer);
		queryWrapper.eq(ObjectUtils.isNotEmpty(registrationStatus), "registration_status", registrationStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(politicalStatus), "political_status", politicalStatus);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @param schoolIdList                 schoolIdList
	 * @return {@link QueryWrapper<RegistrationForm>}
	 */
	@Override
	public QueryWrapper<RegistrationForm> getQueryWrapper(RegistrationFormQueryRequest registrationFormQueryRequest, List<Long> schoolIdList) {
		QueryWrapper<RegistrationForm> queryWrapper = new QueryWrapper<>();
		if (registrationFormQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = registrationFormQueryRequest.getId();
		Long notId = registrationFormQueryRequest.getNotId();
		String userName = registrationFormQueryRequest.getUserName();
		Integer userGender = registrationFormQueryRequest.getUserGender();
		Integer marryStatus = registrationFormQueryRequest.getMarryStatus();
		Integer reviewStatus = registrationFormQueryRequest.getReviewStatus();
		String politicalStatus = registrationFormQueryRequest.getPoliticalStatus();
		String reviewer = registrationFormQueryRequest.getReviewer();
		String workExperience = registrationFormQueryRequest.getWorkExperience();
		Integer registrationStatus = registrationFormQueryRequest.getRegistrationStatus();
		String birthDate = registrationFormQueryRequest.getBirthDate();
		List<String> studentLeaders = registrationFormQueryRequest.getStudentLeaders();
		List<String> educationStages = registrationFormQueryRequest.getEducationStages();
		String studentAwards = registrationFormQueryRequest.getStudentAwards();
		Long jobId = registrationFormQueryRequest.getJobId();
		String sortField = registrationFormQueryRequest.getSortField();
		String sortOrder = registrationFormQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 多字段查询
		if (CollUtil.isNotEmpty(studentLeaders)) {
			for (String leader : studentLeaders) {
				queryWrapper.like("student_leaders", "\"" + leader + "\"");
			}
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
		queryWrapper.like(StringUtils.isNotBlank(workExperience), "work_experience", workExperience);
		queryWrapper.like(StringUtils.isNotBlank(studentAwards), "student_awards", studentAwards);
		queryWrapper.gt(ObjUtil.isNotEmpty(birthDate), "birth_date", birthDate);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "review_status", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "user_gender", userGender);
		queryWrapper.eq(ObjectUtils.isNotEmpty(marryStatus), "marry_status", marryStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "review_status", reviewStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(jobId), "job_id", jobId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewer), "reviewer", reviewer);
		queryWrapper.eq(ObjectUtils.isNotEmpty(registrationStatus), "registration_status", registrationStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(politicalStatus), "political_status", politicalStatus);
		// 过滤符合 schoolIdList 的用户
		if (CollUtil.isNotEmpty(schoolIdList) || CollUtil.isNotEmpty(educationStages)) {
			// 构建 Education 查询条件
			LambdaQueryWrapper<Education> educationWrapper = Wrappers.lambdaQuery(Education.class)
					.select(Education::getUserId);
			if (CollUtil.isNotEmpty(schoolIdList)) {
				educationWrapper.in(Education::getSchoolId, schoolIdList);
			}
			if (CollUtil.isNotEmpty(educationStages)) {
				educationWrapper.in(Education::getEducationalStage, educationStages);
			}
			// 查询符合 schoolIdList 的用户 ID，并去重
			List<Long> userIdList = educationService.list(educationWrapper).stream()
					.map(Education::getUserId)
					.distinct()
					.collect(Collectors.toList());
			// 如果没有匹配的用户 ID，则直接返回一个无效查询条件，确保返回空数据
			if (CollUtil.isEmpty(userIdList)) {
				queryWrapper.eq("id", -1);
				return queryWrapper;
			}
			queryWrapper.in("user_id", userIdList);
		}
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
		// 2. 关联报名用户教育经历信息
		Long userId = registrationForm.getUserId();
		LambdaQueryWrapper<Education> educationLambdaQueryWrapper = Wrappers.lambdaQuery(Education.class)
				.eq(Education::getUserId, userId);
		List<Education> educationList = educationService.list(educationLambdaQueryWrapper);
		List<EducationVO> educationVOList = educationList.stream().map(EducationVO::objToVo).toList();
		registrationFormVO.setEducationVOList(educationVOList);
		// 3. 关系报名用户家庭关系信息
		LambdaQueryWrapper<Family> familyLambdaQueryWrapper = Wrappers.lambdaQuery(Family.class)
				.eq(Family::getUserId, userId);
		List<Family> familyList = familyService.list(familyLambdaQueryWrapper);
		List<FamilyVO> familyVOList = familyList.stream().map(FamilyVO::objToVo).toList();
		registrationFormVO.setFamilyVOList(familyVOList);
		// 3. 关联文件上传附件信息
		LambdaQueryWrapper<FileLog> fileLogLambdaQueryWrapper = Wrappers.lambdaQuery(FileLog.class)
				.eq(FileLog::getUserId, userId);
		List<FileLog> fileLogList = fileLogService.list(fileLogLambdaQueryWrapper);
		List<FileLogVO> fileLogVOList = fileLogList.stream().map(FileLogVO::objToVo).toList();
		registrationFormVO.setFileLogVOList(fileLogVOList);
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
		Set<Long> jobIdSet = registrationFormList.stream().map(RegistrationForm::getJobId).collect(Collectors.toSet());
		Set<Long> userIdSet = registrationFormList.stream().map(RegistrationForm::getUserId).collect(Collectors.toSet());
		// 并行查询 job、education、family
		CompletableFuture<Map<Long, List<Job>>> jobFuture = CompletableFuture.supplyAsync(() ->
				jobService.list(new LambdaQueryWrapper<Job>().in(Job::getId, jobIdSet))
						.stream().collect(Collectors.groupingBy(Job::getId))
				, executor);
		CompletableFuture<Map<Long, List<Education>>> educationFuture = CompletableFuture.supplyAsync(() ->
				educationService.list(new LambdaQueryWrapper<Education>().in(Education::getUserId, userIdSet))
						.stream().collect(Collectors.groupingBy(Education::getUserId))
				, executor);
		CompletableFuture<Map<Long, List<Family>>> familyFuture = CompletableFuture.supplyAsync(() ->
				familyService.list(new LambdaQueryWrapper<Family>().in(Family::getUserId, userIdSet))
						.stream().collect(Collectors.groupingBy(Family::getUserId))
				, executor);
		CompletableFuture<Map<Long, List<FileLog>>> fileLogFuture = CompletableFuture.supplyAsync(() ->
				fileLogService.list(new LambdaQueryWrapper<FileLog>().in(FileLog::getUserId, userIdSet))
						.stream().collect(Collectors.groupingBy(FileLog::getUserId))
				, executor);
		// 等待所有任务完成
		CompletableFuture.allOf(jobFuture, educationFuture, familyFuture).join();
		// 获取结果
		Map<Long, List<Job>> jobIdUserListMap = jobFuture.join();
		Map<Long, List<Education>> userIdEducationListMap = educationFuture.join();
		Map<Long, List<Family>> userIdFamilyListMap = familyFuture.join();
		Map<Long, List<FileLog>> userIdFileLogListMap = fileLogFuture.join();
		// 填充数据
		registrationFormVOList.forEach(registrationFormVO -> {
			registrationFormVO.setJobVO(jobService.getJobVO(jobIdUserListMap.getOrDefault(registrationFormVO.getJobId(), List.of()).stream().findFirst().orElse(null), request));
			registrationFormVO.setEducationVOList(Optional.ofNullable(userIdEducationListMap.get(registrationFormVO.getUserId())).orElse(Collections.emptyList()).stream().map(education -> educationService.getEducationVO(education, request)).toList());
			registrationFormVO.setFamilyVOList(Optional.ofNullable(userIdFamilyListMap.get(registrationFormVO.getUserId())).orElse(Collections.emptyList()).stream().map(family -> familyService.getFamilyVO(family, request)).toList());
			registrationFormVO.setFileLogVOList(Optional.ofNullable(userIdFileLogListMap.get(registrationFormVO.getUserId())).orElse(Collections.emptyList()).stream().map(fileLog -> fileLogService.getFileLogVO(fileLog, request)).toList());
		});
		// endregion
		registrationFormVOPage.setRecords(registrationFormVOList);
		return registrationFormVOPage;
	}
	
}
