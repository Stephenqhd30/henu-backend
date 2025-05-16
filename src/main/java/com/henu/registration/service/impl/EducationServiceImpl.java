package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.EducationMapper;
import com.henu.registration.model.dto.education.EducationQueryRequest;
import com.henu.registration.model.entity.Education;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.enums.EducationalStageEnum;
import com.henu.registration.model.vo.education.EducationVO;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.EducationService;
import com.henu.registration.service.SchoolService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 教育经历表服务实现
 *
 * @author stephenqiu
 * @description 针对表【education(教育经历表)】的数据库操作Service实现
 * @createDate 2025-03-22 13:05:52
 */
@Service
@Slf4j
public class EducationServiceImpl extends ServiceImpl<EducationMapper, Education> implements EducationService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private SchoolService schoolService;
	
	/**
	 * 校验数据
	 *
	 * @param education education
	 * @param add       对创建的数据进行校验
	 */
	@Override
	public void validEducation(Education education, boolean add) {
		ThrowUtils.throwIf(education == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long schoolId = education.getSchoolId();
		String educationalStage = education.getEducationalStage();
		String studyTime = education.getStudyTime();
		String certifier = education.getCertifier();
		String certifierPhone = education.getCertifierPhone();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(schoolId), ErrorCode.PARAMS_ERROR, "学校不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(educationalStage), ErrorCode.PARAMS_ERROR, "教育阶段不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(studyTime), ErrorCode.PARAMS_ERROR, "学习起止年月不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certifier), ErrorCode.PARAMS_ERROR, "证明人不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certifierPhone), ErrorCode.PARAMS_ERROR, "证明人手机号不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(educationalStage)) {
			ThrowUtils.throwIf(EducationalStageEnum.getEnumByValue(educationalStage) == null, ErrorCode.PARAMS_ERROR, "教育阶段值不合法");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param educationQueryRequest educationQueryRequest
	 * @return {@link QueryWrapper<Education>}
	 */
	@Override
	public QueryWrapper<Education> getQueryWrapper(EducationQueryRequest educationQueryRequest) {
		QueryWrapper<Education> queryWrapper = new QueryWrapper<>();
		if (educationQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = educationQueryRequest.getId();
		Long notId = educationQueryRequest.getNotId();
		Long schoolId = educationQueryRequest.getSchoolId();
		String educationalStage = educationQueryRequest.getEducationalStage();
		String major = educationQueryRequest.getMajor();
		String studyTime = educationQueryRequest.getStudyTime();
		String certifier = educationQueryRequest.getCertifier();
		String certifierPhone = educationQueryRequest.getCertifierPhone();
		Long userId = educationQueryRequest.getUserId();
		String sortField = educationQueryRequest.getSortField();
		String sortOrder = educationQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(major), "major", major);
		queryWrapper.like(StringUtils.isNotBlank(certifier), "certifier", certifier);
		queryWrapper.like(StringUtils.isNotBlank(studyTime), "study_time", studyTime);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "user_id", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(schoolId), "school_id", schoolId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(educationalStage), "educational_stage", educationalStage);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certifierPhone), "certifier_phone", certifierPhone);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取教育经历表封装
	 *
	 * @param education education
	 * @param request   request
	 * @return {@link EducationVO}
	 */
	@Override
	public EducationVO getEducationVO(Education education, HttpServletRequest request) {
		// 对象转封装类
		EducationVO educationVO = EducationVO.objToVo(education);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = education.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		educationVO.setUserVO(userVO);
		// 2. 关联查询学校信息
		Long schoolId = education.getSchoolId();
		School school = null;
		if (schoolId != null && schoolId > 0) {
			school = schoolService.getById(schoolId);
		}
		SchoolVO schoolVO = schoolService.getSchoolVO(school, request);
		educationVO.setSchoolVO(schoolVO);
		// endregion
		return educationVO;
	}
	
	/**
	 * 分页获取教育经历表封装
	 *
	 * @param educationPage educationPage
	 * @param request       request
	 * @return {@link Page<EducationVO>}
	 */
	@Override
	public Page<EducationVO> getEducationVOPage(Page<Education> educationPage, HttpServletRequest request) {
		List<Education> educationList = educationPage.getRecords();
		Page<EducationVO> educationVOPage = new Page<>(educationPage.getCurrent(), educationPage.getSize(), educationPage.getTotal());
		if (CollUtil.isEmpty(educationList)) {
			return educationVOPage;
		}
		// 对象列表 => 封装对象列表
		List<EducationVO> educationVOList = educationList.stream()
				.map(EducationVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = educationList.stream().map(Education::getUserId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(userIdSet)) {
			CompletableFuture<Map<Long, List<User>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> userService.listByIds(userIdSet).stream()
					.collect(Collectors.groupingBy(User::getId)));
			try {
				Map<Long, List<User>> userIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				educationVOList.forEach(educationVO -> {
					Long userId = educationVO.getUserId();
					User user = null;
					if (userIdUserListMap.containsKey(userId)) {
						user = userIdUserListMap.get(userId).get(0);
					}
					educationVO.setUserVO(userService.getUserVO(user, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// 2. 关联查询学校信息
		Set<Long> schoolIdSet = educationList.stream().map(Education::getSchoolId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(schoolIdSet)) {
			CompletableFuture<Map<Long, List<School>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> schoolService.listByIds(schoolIdSet).stream()
					.collect(Collectors.groupingBy(School::getId)));
			try {
				Map<Long, List<School>> schoolIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				educationVOList.forEach(educationVO -> {
					Long schoolId = educationVO.getSchoolId();
					School school = null;
					if (schoolIdUserListMap.containsKey(schoolId)) {
						school = schoolIdUserListMap.get(schoolId).get(0);
					}
					educationVO.setSchoolVO(schoolService.getSchoolVO(school, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		educationVOPage.setRecords(educationVOList);
		return educationVOPage;
	}
	
}
