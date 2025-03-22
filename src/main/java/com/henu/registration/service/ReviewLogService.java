package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.reviewLog.ReviewLogQueryRequest;
import com.henu.registration.model.entity.ReviewLog;
import com.henu.registration.model.vo.reviewLog.ReviewLogVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 审核记录服务
 *
 * @author stephenqiu
 * @description 针对表【review_log(审核记录表)】的数据库操作Service
 * @createDate 2025-03-23 00:32:51
 */
public interface ReviewLogService extends IService<ReviewLog> {
	
	/**
	 * 校验数据
	 *
	 * @param reviewLog reviewLog
	 * @param add       对创建的数据进行校验
	 */
	void validReviewLog(ReviewLog reviewLog, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param reviewLogQueryRequest reviewLogQueryRequest
	 * @return {@link QueryWrapper<ReviewLog>}
	 */
	QueryWrapper<ReviewLog> getQueryWrapper(ReviewLogQueryRequest reviewLogQueryRequest);
	
	/**
	 * 获取审核记录封装
	 *
	 * @param reviewLog reviewLog
	 * @param request   request
	 * @return {@link ReviewLogVO}
	 */
	ReviewLogVO getReviewLogVO(ReviewLog reviewLog, HttpServletRequest request);
	
	/**
	 * 分页获取审核记录封装
	 *
	 * @param reviewLogPage reviewLogPage
	 * @param request       request
	 * @return {@link Page<ReviewLogVO>}
	 */
	Page<ReviewLogVO> getReviewLogVOPage(Page<ReviewLog> reviewLogPage, HttpServletRequest request);
}