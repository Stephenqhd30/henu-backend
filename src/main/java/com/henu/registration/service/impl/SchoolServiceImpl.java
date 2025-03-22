package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.mapper.SchoolMapper;
import com.henu.registration.model.dto.school.SchoolQueryRequest;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.service.SchoolService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 高校信息服务实现
 *
 * @author stephenqiu
 * @description 针对表【school(高校信息)】的数据库操作Service实现
 * @createDate 2025-03-21 11:09:15
 */
@Service
@Slf4j
public class SchoolServiceImpl extends ServiceImpl<SchoolMapper, School> implements SchoolService {
	
	/**
	 * 校验数据
	 *
	 * @param school school
	 * @param add    对创建的数据进行校验
	 */
	@Override
	public void validSchool(School school, boolean add) {
		ThrowUtils.throwIf(school == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String schoolName = school.getSchoolName();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(schoolName), ErrorCode.PARAMS_ERROR, "高校名称不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(schoolName)) {
			ThrowUtils.throwIf(schoolName.length() > 80, ErrorCode.PARAMS_ERROR, "高校名称过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @return {@link QueryWrapper<School>}
	 */
	@Override
	public QueryWrapper<School> getQueryWrapper(SchoolQueryRequest schoolQueryRequest) {
		QueryWrapper<School> queryWrapper = new QueryWrapper<>();
		if (schoolQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = schoolQueryRequest.getId();
		Long notId = schoolQueryRequest.getNotId();
		String schoolName = schoolQueryRequest.getSchoolName();
		Long adminId = schoolQueryRequest.getAdminId();
		String sortField = schoolQueryRequest.getSortField();
		String sortOrder = schoolQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(schoolName), "school_name", schoolName);
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
	 * 获取高校信息封装
	 *
	 * @param school  school
	 * @param request request
	 * @return {@link SchoolVO}
	 */
	@Override
	public SchoolVO getSchoolVO(School school, HttpServletRequest request) {
		// 对象转封装类
		return SchoolVO.objToVo(school);
	}
	
	/**
	 * 分页获取高校信息封装
	 *
	 * @param schoolPage schoolPage
	 * @param request    request
	 * @return {@link Page<SchoolVO>}
	 */
	@Override
	public Page<SchoolVO> getSchoolVOPage(Page<School> schoolPage, HttpServletRequest request) {
		List<School> schoolList = schoolPage.getRecords();
		Page<SchoolVO> schoolVOPage = new Page<>(schoolPage.getCurrent(), schoolPage.getSize(), schoolPage.getTotal());
		if (CollUtil.isEmpty(schoolList)) {
			return schoolVOPage;
		}
		// 对象列表 => 封装对象列表
		List<SchoolVO> schoolVOList = schoolList.stream()
				.map(SchoolVO::objToVo)
				.collect(Collectors.toList());
		schoolVOPage.setRecords(schoolVOList);
		return schoolVOPage;
	}
	
}
