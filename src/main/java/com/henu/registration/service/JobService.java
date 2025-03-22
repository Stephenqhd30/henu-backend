package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.job.JobQueryRequest;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.vo.job.JobVO;


import javax.servlet.http.HttpServletRequest;

/**
 * 岗位信息表服务
 *
 * @author stephenqiu
 * @description 针对表【job(岗位信息表)】的数据库操作Service
 * @createDate 2025-03-22 12:45:34
 */
public interface JobService extends IService<Job> {
	
	/**
	 * 校验数据
	 *
	 * @param job job
	 * @param add 对创建的数据进行校验
	 */
	void validJob(Job job, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param jobQueryRequest jobQueryRequest
	 * @return {@link QueryWrapper<Job>}
	 */
	QueryWrapper<Job> getQueryWrapper(JobQueryRequest jobQueryRequest);
	
	/**
	 * 获取岗位信息表封装
	 *
	 * @param job     job
	 * @param request request
	 * @return {@link JobVO}
	 */
	JobVO getJobVO(Job job, HttpServletRequest request);
	
	/**
	 * 分页获取岗位信息表封装
	 *
	 * @param jobPage jobPage
	 * @param request request
	 * @return {@link Page<JobVO>}
	 */
	Page<JobVO> getJobVOPage(Page<Job> jobPage, HttpServletRequest request);
}