package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.family.FamilyQueryRequest;
import com.henu.registration.model.entity.Family;
import com.henu.registration.model.vo.family.FamilyVO;

import javax.servlet.http.HttpServletRequest;


/**
 * 家庭关系服务
 * @author stephenqiu
 * @description 针对表【family(家庭关系表)】的数据库操作Service
 * @createDate 2025-03-23 00:15:52
 */
public interface FamilyService extends IService<Family> {
	
	/**
	 * 校验数据
	 *
	 * @param family family
	 * @param add    对创建的数据进行校验
	 */
	void validFamily(Family family, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param familyQueryRequest familyQueryRequest
	 * @return {@link QueryWrapper<Family>}
	 */
	QueryWrapper<Family> getQueryWrapper(FamilyQueryRequest familyQueryRequest);
	
	/**
	 * 获取家庭关系封装
	 *
	 * @param family  family
	 * @param request request
	 * @return {@link FamilyVO}
	 */
	FamilyVO getFamilyVO(Family family, HttpServletRequest request);
	
	/**
	 * 分页获取家庭关系封装
	 *
	 * @param familyPage familyPage
	 * @param request    request
	 * @return {@link Page<FamilyVO>}
	 */
	Page<FamilyVO> getFamilyVOPage(Page<Family> familyPage, HttpServletRequest request);
}