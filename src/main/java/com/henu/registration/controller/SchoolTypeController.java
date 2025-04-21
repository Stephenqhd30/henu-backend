package com.henu.registration.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.schoolType.SchoolTypeAddRequest;
import com.henu.registration.model.dto.schoolType.SchoolTypeQueryRequest;
import com.henu.registration.model.dto.schoolType.SchoolTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.Job;
import com.henu.registration.model.entity.SchoolType;
import com.henu.registration.model.vo.job.JobVO;
import com.henu.registration.model.vo.schoolType.SchoolTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.SchoolTypeService;
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
 * 高校类型接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/schoolType")
@Slf4j
public class SchoolTypeController {
	
	@Resource
	private SchoolTypeService schoolTypeService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建高校类型
	 *
	 * @param schoolTypeAddRequest schoolTypeAddRequest
	 * @param request              request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addSchoolType(@RequestBody SchoolTypeAddRequest schoolTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(schoolTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolTypeAddRequest, schoolType);
		// 数据校验
		schoolTypeService.validSchoolType(schoolType, true);
		
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = schoolTypeService.save(schoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newSchoolTypeId = schoolType.getId();
		return ResultUtils.success(newSchoolTypeId);
	}
	
	/**
	 * 删除高校类型
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSchoolType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		SchoolType oldSchoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新高校类型（仅系统管理员可用）
	 *
	 * @param schoolTypeUpdateRequest schoolTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateSchoolType(@RequestBody SchoolTypeUpdateRequest schoolTypeUpdateRequest) {
		if (schoolTypeUpdateRequest == null || schoolTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolTypeUpdateRequest, schoolType);
		// 数据校验
		schoolTypeService.validSchoolType(schoolType, false);
		// 判断是否存在
		long id = schoolTypeUpdateRequest.getId();
		SchoolType oldSchoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(oldSchoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = schoolTypeService.updateById(schoolType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取高校类型（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<SchoolTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<SchoolTypeVO> getSchoolTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		SchoolType schoolType = schoolTypeService.getById(id);
		ThrowUtils.throwIf(schoolType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(schoolTypeService.getSchoolTypeVO(schoolType, request));
	}
	
	/**
	 * 分页获取高校类型列表（仅系统管理员可用）
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @return {@link BaseResponse<Page<SchoolType>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<SchoolType>> listSchoolTypeByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest) {
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		return ResultUtils.success(schoolTypePage);
	}
	
	/**
	 * 分页获取高校类型列表（封装类）
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse<Page<SchoolTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<SchoolTypeVO>> listSchoolTypeVOByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest,
	                                                               HttpServletRequest request) {
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 使用多级缓存优化查询
		// 构建缓存 key（基于查询条件的 MD5 哈希值）
		String queryCondition = JSONUtil.toJsonStr(schoolTypeQueryRequest);
		String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
		String cacheKey = "listSchoolTypeVOByPage:" + hashKey;
		// 1. 尝试从本地缓存中获取数据
		String cachedValue = (String) LocalCacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果缓存命中，直接返回缓存中的分页结果
			Page<SchoolTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 2. 如果本地缓存未命中，尝试从 Redis 缓存中获取数据
		cachedValue = CacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果 Redis 缓存命中，将其存入本地缓存并返回
			LocalCacheUtils.put(cacheKey, cachedValue);
			Page<SchoolTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 3. 如果缓存都未命中，查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		// 4. 将数据库查询结果转换为 VO 页面对象
		Page<SchoolTypeVO> schoolTypeVOPage = schoolTypeService.getSchoolTypeVOPage(schoolTypePage, request);
		String cacheValue = JSONUtil.toJsonStr(schoolTypeVOPage);
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
		return ResultUtils.success(schoolTypeVOPage);
	}
	
	/**
	 * 分页获取当前登录用户创建的高校类型列表
	 *
	 * @param schoolTypeQueryRequest schoolTypeQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse<Page<SchoolTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<SchoolTypeVO>> listMySchoolTypeVOByPage(@RequestBody SchoolTypeQueryRequest schoolTypeQueryRequest,
	                                                                 HttpServletRequest request) {
		ThrowUtils.throwIf(schoolTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		schoolTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = schoolTypeQueryRequest.getCurrent();
		long size = schoolTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<SchoolType> schoolTypePage = schoolTypeService.page(new Page<>(current, size),
				schoolTypeService.getQueryWrapper(schoolTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(schoolTypeService.getSchoolTypeVOPage(schoolTypePage, request));
	}
	// endregion
}