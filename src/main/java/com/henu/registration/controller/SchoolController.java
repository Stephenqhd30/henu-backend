package com.henu.registration.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.school.SchoolAddRequest;
import com.henu.registration.model.dto.school.SchoolQueryRequest;
import com.henu.registration.model.dto.school.SchoolUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.school.SchoolVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolService;
import com.henu.registration.utils.caffeine.LocalCacheUtils;
import com.henu.registration.utils.redisson.cache.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 高校信息接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/school")
@Slf4j
public class SchoolController {
	
	@Resource
	private SchoolService schoolService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建高校信息
	 *
	 * @param schoolAddRequest schoolAddRequest
	 * @param request          request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addSchool(@RequestBody SchoolAddRequest schoolAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(schoolAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		School school = new School();
		BeanUtils.copyProperties(schoolAddRequest, school);
		// 数据校验
		schoolService.validSchool(school, true);
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		school.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = schoolService.save(school);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newSchoolId = school.getId();
		return ResultUtils.success(newSchoolId);
	}
	
	/**
	 * 删除高校信息
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSchool(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		School oldSchool = schoolService.getById(id);
		ThrowUtils.throwIf(oldSchool == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldSchool.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = schoolService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新高校信息（仅系统管理员可用）
	 *
	 * @param schoolUpdateRequest schoolUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateSchool(@RequestBody SchoolUpdateRequest schoolUpdateRequest) {
		if (schoolUpdateRequest == null || schoolUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		School school = new School();
		BeanUtils.copyProperties(schoolUpdateRequest, school);
		// 数据校验
		schoolService.validSchool(school, false);
		// 判断是否存在
		long id = schoolUpdateRequest.getId();
		School oldSchool = schoolService.getById(id);
		ThrowUtils.throwIf(oldSchool == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolService.updateById(school);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取高校信息（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SchoolVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SchoolVO> getSchoolVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		School school = schoolService.getById(id);
		ThrowUtils.throwIf(school == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(schoolService.getSchoolVO(school, request));
	}
	
	/**
	 * 分页获取高校信息列表（仅系统管理员可用）
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @return {@link BaseResponse<Page<School>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<School>> listSchoolByPage(@RequestBody SchoolQueryRequest schoolQueryRequest) {
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		return ResultUtils.success(schoolPage);
	}
	
	/**
	 * 分页获取高校信息列表（封装类）
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<SchoolVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SchoolVO>> listSchoolVOByPage(@RequestBody SchoolQueryRequest schoolQueryRequest,
	                                                       HttpServletRequest request) {
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 使用多级缓存优化查询
		// 构建缓存 key（基于查询条件的 MD5 哈希值）
		String queryCondition = JSONUtil.toJsonStr(schoolQueryRequest);
		String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
		String cacheKey = "listSchoolVOByPage:" + hashKey;
		// 1. 尝试从本地缓存中获取数据
		String cachedValue = (String) LocalCacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果缓存命中，直接返回缓存中的分页结果
			Page<SchoolVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 2. 如果本地缓存未命中，尝试从 Redis 缓存中获取数据
		cachedValue = CacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果 Redis 缓存命中，将其存入本地缓存并返回
			LocalCacheUtils.put(cacheKey, cachedValue);
			Page<SchoolVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 3. 如果缓存都未命中，查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		// 4. 将数据库查询结果转换为 VO 页面对象
		Page<SchoolVO> schoolVOPage = schoolService.getSchoolVOPage(schoolPage, request);
		String cacheValue = JSONUtil.toJsonStr(schoolVOPage);
		// 5. 更新本地缓存和 Redis 缓存
		try {
			// 更新本地缓存
			LocalCacheUtils.put(cacheKey, cacheValue);
			// 更新 Redis 缓存, 并设置随机过期时间为 2~5 分钟
			CacheUtils.put(cacheKey, cacheValue, TimeUnit.MINUTES.toMinutes(RandomUtil.randomLong(2, 5)));
		} catch (Exception e) {
			// 如果 Redis 缓存更新失败，记录日志以便排查问题
			log.error("更新缓存失败, cacheKey: {}", cacheKey, e);
		}
		// 获取封装类
		return ResultUtils.success(schoolVOPage);
	}
	
	/**
	 * 分页获取当前登录用户创建的高校信息列表
	 *
	 * @param schoolQueryRequest schoolQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse<Page<SchoolVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<SchoolVO>> listMySchoolVOByPage(@RequestBody SchoolQueryRequest schoolQueryRequest,
	                                                         HttpServletRequest request) {
		ThrowUtils.throwIf(schoolQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolQueryRequest.setAdminId(loginAdmin.getId());
		long current = schoolQueryRequest.getCurrent();
		long size = schoolQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<School> schoolPage = schoolService.page(new Page<>(current, size),
				schoolService.getQueryWrapper(schoolQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolService.getSchoolVOPage(schoolPage, request));
	}
	
	// endregion
}