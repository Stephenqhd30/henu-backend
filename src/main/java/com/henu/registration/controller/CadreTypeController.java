package com.henu.registration.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.cadreType.CadreTypeAddRequest;
import com.henu.registration.model.dto.cadreType.CadreTypeQueryRequest;
import com.henu.registration.model.dto.cadreType.CadreTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.vo.cadreType.CadreTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.CadreTypeService;
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
 * 干部类型接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/cadreType")
@Slf4j
public class CadreTypeController {
	
	@Resource
	private CadreTypeService cadreTypeService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建干部类型
	 *
	 * @param cadreTypeAddRequest cadreTypeAddRequest
	 * @param request             request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addCadreType(@RequestBody CadreTypeAddRequest cadreTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(cadreTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		CadreType cadreType = new CadreType();
		BeanUtils.copyProperties(cadreTypeAddRequest, cadreType);
		// 数据校验
		cadreTypeService.validCadreType(cadreType, true);
		// todo 填充默认值
		CadreType oldCadreType = cadreTypeService.getOne(Wrappers.lambdaQuery(CadreType.class)
				.eq(CadreType::getType, cadreType.getType()));
		if (oldCadreType != null) {
			cadreType.setId(oldCadreType.getId());
		}
		Admin loginAdmin = adminService.getLoginAdmin(request);
		cadreType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = cadreTypeService.saveOrUpdate(cadreType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newCadreTypeId = cadreType.getId();
		return ResultUtils.success(newCadreTypeId);
	}
	
	/**
	 * 删除干部类型
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteCadreType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		CadreType oldCadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(oldCadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = cadreTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新干部类型（仅系统管理员可用）
	 *
	 * @param cadreTypeUpdateRequest cadreTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateCadreType(@RequestBody CadreTypeUpdateRequest cadreTypeUpdateRequest) {
		if (cadreTypeUpdateRequest == null || cadreTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		CadreType cadreType = new CadreType();
		BeanUtils.copyProperties(cadreTypeUpdateRequest, cadreType);
		// 数据校验
		cadreTypeService.validCadreType(cadreType, false);
		// 判断是否存在
		long id = cadreTypeUpdateRequest.getId();
		CadreType oldCadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(oldCadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = cadreTypeService.updateById(cadreType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取干部类型（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<CadreTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<CadreTypeVO> getCadreTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		CadreType cadreType = cadreTypeService.getById(id);
		ThrowUtils.throwIf(cadreType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(cadreTypeService.getCadreTypeVO(cadreType, request));
	}
	
	/**
	 * 分页获取干部类型列表
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @return {@link BaseResponse<Page<CadreType>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<CadreType>> listCadreTypeByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest) {
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		return ResultUtils.success(cadreTypePage);
	}
	
	/**
	 * 分页获取干部类型列表（封装类）
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<CadreTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<CadreTypeVO>> listCadreTypeVOByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest,
	                                                             HttpServletRequest request) {
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 使用多级缓存优化查询
		// 构建缓存 key（基于查询条件的 MD5 哈希值）
		String queryCondition = JSONUtil.toJsonStr(cadreTypeQueryRequest);
		String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
		String cacheKey = "listCadreTypeVOByPage:" + hashKey;
		// 1. 尝试从本地缓存中获取数据
		String cachedValue = (String) LocalCacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果缓存命中，直接返回缓存中的分页结果
			Page<CadreTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 2. 如果本地缓存未命中，尝试从 Redis 缓存中获取数据
		cachedValue = CacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果 Redis 缓存命中，将其存入本地缓存并返回
			LocalCacheUtils.put(cacheKey, cachedValue);
			Page<CadreTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 3. 如果缓存都未命中，查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		// 4. 将数据库查询结果转换为 VO 页面对象
		Page<CadreTypeVO> cadreTypeVOPage = cadreTypeService.getCadreTypeVOPage(cadreTypePage, request);
		String cacheValue = JSONUtil.toJsonStr(cadreTypeVOPage);
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
		return ResultUtils.success(cadreTypeVOPage);
	}
	
	/**
	 * 分页获取当前登录管理员创建的干部类型列表
	 *
	 * @param cadreTypeQueryRequest cadreTypeQueryRequest
	 * @param request               request
	 * @return {@link BaseResponse<Page<CadreTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<CadreTypeVO>> listMyCadreTypeVOByPage(@RequestBody CadreTypeQueryRequest cadreTypeQueryRequest,
	                                                               HttpServletRequest request) {
		ThrowUtils.throwIf(cadreTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		cadreTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = cadreTypeQueryRequest.getCurrent();
		long size = cadreTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<CadreType> cadreTypePage = cadreTypeService.page(new Page<>(current, size),
				cadreTypeService.getQueryWrapper(cadreTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(cadreTypeService.getCadreTypeVOPage(cadreTypePage, request));
	}
	// endregion
}