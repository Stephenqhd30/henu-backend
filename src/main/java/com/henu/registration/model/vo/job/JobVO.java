package com.henu.registration.model.vo.job;

import com.henu.registration.model.entity.Job;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 岗位信息表视图
 *
 * @author stephen
 */
@Data
public class JobVO implements Serializable {
	
	private static final long serialVersionUID = -5441845198915192108L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 岗位名称
	 */
	private String jobName;
	
	/**
	 * 岗位说明
	 */
	private String jobExplanation;
	
	/**
	 * 创建用户 id
	 */
	private Long adminId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户信息
	 */
	private AdminVO adminVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param jobVO jobVO
	 * @return {@link Job}
	 */
	public static Job voToObj(JobVO jobVO) {
		if (jobVO == null) {
			return null;
		}
		Job job = new Job();
		BeanUtils.copyProperties(jobVO, job);
		return job;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param job job
	 * @return {@link JobVO}
	 */
	public static JobVO objToVo(Job job) {
		if (job == null) {
			return null;
		}
		JobVO jobVO = new JobVO();
		BeanUtils.copyProperties(job, jobVO);
		return jobVO;
	}
}
