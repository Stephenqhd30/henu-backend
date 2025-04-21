package com.henu.registration.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.fileType.FileTypeAddRequest;
import com.henu.registration.model.dto.fileType.FileTypeQueryRequest;
import com.henu.registration.model.dto.fileType.FileTypeUpdateRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.FileType;
import com.henu.registration.model.vo.fileType.FileTypeVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.FileTypeService;
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
 * 文件上传类型接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/fileType")
@Slf4j
public class FileTypeController {
	
	@Resource
	private FileTypeService fileTypeService;
	
	@Resource
	private AdminService adminService;
	
	// region 增删改查
	
	/**
	 * 创建文件上传类型
	 *
	 * @param fileTypeAddRequest fileTypeAddRequest
	 * @param request            request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addFileType(@RequestBody FileTypeAddRequest fileTypeAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(fileTypeAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		FileType fileType = new FileType();
		BeanUtils.copyProperties(fileTypeAddRequest, fileType);
		String typeValues = JSONUtil.toJsonStr(fileTypeAddRequest.getTypeValues());
		fileType.setTypeValues(typeValues);
		// 数据校验
		fileTypeService.validFileType(fileType, true);
		LambdaQueryWrapper<FileType> eq = Wrappers.lambdaQuery(FileType.class)
				.eq(FileType::getTypeName, fileType.getTypeName());
		ThrowUtils.throwIf(fileTypeService.count(eq) > 0, ErrorCode.PARAMS_ERROR, "类型名称已存在");
		// todo 填充默认值
		Admin loginAdmin = adminService.getLoginAdmin(request);
		fileType.setAdminId(loginAdmin.getId());
		// 写入数据库
		boolean result = fileTypeService.save(fileType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newFileTypeId = fileType.getId();
		return ResultUtils.success(newFileTypeId);
	}
	
	/**
	 * 删除文件上传类型
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteFileType(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		Admin admin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		FileType oldFileType = fileTypeService.getById(id);
		ThrowUtils.throwIf(oldFileType == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldFileType.getAdminId().equals(admin.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = fileTypeService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新文件上传类型（仅管理员可用）
	 *
	 * @param fileTypeUpdateRequest fileTypeUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateFileType(@RequestBody FileTypeUpdateRequest fileTypeUpdateRequest) {
		if (fileTypeUpdateRequest == null || fileTypeUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		FileType fileType = new FileType();
		BeanUtils.copyProperties(fileTypeUpdateRequest, fileType);
		String typeValues = JSONUtil.toJsonStr(fileTypeUpdateRequest.getTypeValues());
		fileType.setTypeValues(typeValues);
		// 数据校验
		fileTypeService.validFileType(fileType, false);
		// 判断是否存在
		long id = fileTypeUpdateRequest.getId();
		FileType oldFileType = fileTypeService.getById(id);
		ThrowUtils.throwIf(oldFileType == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = fileTypeService.updateById(fileType);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取文件上传类型（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<FileTypeVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<FileTypeVO> getFileTypeVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		FileType fileType = fileTypeService.getById(id);
		ThrowUtils.throwIf(fileType == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(fileTypeService.getFileTypeVO(fileType, request));
	}
	
	/**
	 * 分页获取文件上传类型列表
	 *
	 * @param fileTypeQueryRequest fileTypeQueryRequest
	 * @return {@link BaseResponse<Page<FileType>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<FileType>> listFileTypeByPage(@RequestBody FileTypeQueryRequest fileTypeQueryRequest) {
		long current = fileTypeQueryRequest.getCurrent();
		long size = fileTypeQueryRequest.getPageSize();
		// 查询数据库
		Page<FileType> fileTypePage = fileTypeService.page(new Page<>(current, size),
				fileTypeService.getQueryWrapper(fileTypeQueryRequest));
		return ResultUtils.success(fileTypePage);
	}
	
	/**
	 * 分页获取文件上传类型列表（封装类）
	 *
	 * @param fileTypeQueryRequest fileTypeQueryRequest
	 * @param request              request
	 * @return {@link BaseResponse<Page<FileTypeVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<FileTypeVO>> listFileTypeVOByPage(@RequestBody FileTypeQueryRequest fileTypeQueryRequest,
	                                                           HttpServletRequest request) {
		long current = fileTypeQueryRequest.getCurrent();
		long size = fileTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 使用多级缓存优化查询
		// 构建缓存 key（基于查询条件的 MD5 哈希值）
		String queryCondition = JSONUtil.toJsonStr(fileTypeQueryRequest);
		String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
		String cacheKey = "listFileTypeVOByPage:" + hashKey;
		// 1. 尝试从本地缓存中获取数据
		String cachedValue = (String) LocalCacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果缓存命中，直接返回缓存中的分页结果
			Page<FileTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 2. 如果本地缓存未命中，尝试从 Redis 缓存中获取数据
		cachedValue = CacheUtils.get(cacheKey);
		if (ObjUtil.isNotEmpty(cachedValue)) {
			// 如果 Redis 缓存命中，将其存入本地缓存并返回
			LocalCacheUtils.put(cacheKey, cachedValue);
			Page<FileTypeVO> cachedPage = JSONUtil.toBean(cachedValue, Page.class);
			return ResultUtils.success(cachedPage);
		}
		// 3. 如果缓存都未命中，查询数据库
		Page<FileType> fileTypePage = fileTypeService.page(new Page<>(current, size),
				fileTypeService.getQueryWrapper(fileTypeQueryRequest));
		// 4. 将数据库查询结果转换为 VO 页面对象
		Page<FileTypeVO> fileTypeVOPage = fileTypeService.getFileTypeVOPage(fileTypePage, request);
		String cacheValue = JSONUtil.toJsonStr(fileTypeVOPage);
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
		return ResultUtils.success(fileTypeVOPage);
	}
	
	/**
	 * 分页获取当前登录用户创建的文件上传类型列表
	 *
	 * @param fileTypeQueryRequest fileTypeQueryRequest
	 * @param request              request
	 * @return {@link BaseResponse<Page<FileTypeVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<FileTypeVO>> listMyFileTypeVOByPage(@RequestBody FileTypeQueryRequest fileTypeQueryRequest,
	                                                             HttpServletRequest request) {
		ThrowUtils.throwIf(fileTypeQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		Admin loginAdmin = adminService.getLoginAdmin(request);
		fileTypeQueryRequest.setAdminId(loginAdmin.getId());
		long current = fileTypeQueryRequest.getCurrent();
		long size = fileTypeQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<FileType> fileTypePage = fileTypeService.page(new Page<>(current, size),
				fileTypeService.getQueryWrapper(fileTypeQueryRequest));
		// 获取封装类
		return ResultUtils.success(fileTypeService.getFileTypeVOPage(fileTypePage, request));
	}
	
	// endregion
}