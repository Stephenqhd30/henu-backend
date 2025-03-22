package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.JobMapper;
import com.henu.registration.model.dto.job.JobQueryRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.JobService;
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
 * 岗位信息表服务实现
 *
 * @author stephenqiu
 * @description 针对表【job(岗位信息表)】的数据库操作Service实现
 * @createDate 2025-03-22 12:45:34
 */
@Service
@Slf4j
public class JobServiceImpl extends ServiceImpl<JobMapper, Job> implements JobService {
	
	@Resource
	private AdminService adminService;
	
	/**
	 * 校验数据
	 *
	 * @param job job
	 * @param add 对创建的数据进行校验
	 */
	@Override
	public void validJob(Job job, boolean add) {
		ThrowUtils.throwIf(job == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String jobName = job.getJobName();
		String jobExplanation = job.getJobExplanation();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(jobName), ErrorCode.PARAMS_ERROR, "岗位名称不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(jobExplanation), ErrorCode.PARAMS_ERROR, "岗位说明不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(jobName)) {
			ThrowUtils.throwIf(jobName.length() > 80, ErrorCode.PARAMS_ERROR, "岗位名称过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param jobQueryRequest jobQueryRequest
	 * @return {@link QueryWrapper<Job>}
	 */
	@Override
	public QueryWrapper<Job> getQueryWrapper(JobQueryRequest jobQueryRequest) {
		QueryWrapper<Job> queryWrapper = new QueryWrapper<>();
		if (jobQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = jobQueryRequest.getId();
		Long notId = jobQueryRequest.getNotId();
		String searchText = jobQueryRequest.getSearchText();
		String jobName = jobQueryRequest.getJobName();
		String jobExplanation = jobQueryRequest.getJobExplanation();
		Long adminId = jobQueryRequest.getAdminId();
		String sortField = jobQueryRequest.getSortField();
		String sortOrder = jobQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		if (StringUtils.isNotBlank(searchText)) {
			// 需要拼接查询条件
			queryWrapper.and(qw -> qw.like("job_name", jobName).or().like("job_explanation", jobExplanation));
		}
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(jobName), "job_name", jobName);
		queryWrapper.like(StringUtils.isNotBlank(jobExplanation), "job_explanation", jobExplanation);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminId), "admin_id", adminId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取岗位信息表封装
	 *
	 * @param job     job
	 * @param request request
	 * @return {@link JobVO}
	 */
	@Override
	public JobVO getJobVO(Job job, HttpServletRequest request) {
		// 对象转封装类
		JobVO jobVO = JobVO.objToVo(job);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long adminId = job.getAdminId();
		Admin admin = null;
		if (adminId != null && adminId > 0) {
			admin = adminService.getById(adminId);
		}
		AdminVO adminVO = adminService.getAdminVO(admin, request);
		jobVO.setAdminVO(adminVO);
		
		// endregion
		return jobVO;
	}
	
	/**
	 * 分页获取岗位信息表封装
	 *
	 * @param jobPage jobPage
	 * @param request request
	 * @return {@link Page<JobVO>}
	 */
	@Override
	public Page<JobVO> getJobVOPage(Page<Job> jobPage, HttpServletRequest request) {
		List<Job> jobList = jobPage.getRecords();
		Page<JobVO> jobVOPage = new Page<>(jobPage.getCurrent(), jobPage.getSize(), jobPage.getTotal());
		if (CollUtil.isEmpty(jobList)) {
			return jobVOPage;
		}
		// 对象列表 => 封装对象列表
		List<JobVO> jobVOList = jobList.stream()
				.map(JobVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> adminIdSet = jobList.stream().map(Job::getAdminId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(adminIdSet)) {
			CompletableFuture<Map<Long, List<Admin>>> mapCompletableFuture = CompletableFuture.supplyAsync(() -> adminService.listByIds(adminIdSet).stream()
					.collect(Collectors.groupingBy(Admin::getId)));
			try {
				Map<Long, List<Admin>> adminIdAdminListMap = mapCompletableFuture.get();
				// 填充信息
				jobVOList.forEach(jobVO -> {
					Long adminId = jobVO.getAdminId();
					Admin admin = null;
					if (adminIdAdminListMap.containsKey(adminId)) {
						admin = adminIdAdminListMap.get(adminId).get(0);
					}
					jobVO.setAdminVO(adminService.getAdminVO(admin, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		jobVOPage.setRecords(jobVOList);
		return jobVOPage;
	}
	
}
