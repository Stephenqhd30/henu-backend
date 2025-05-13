package com.henu.registration.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.model.dto.registrationForm.RegistrationFormAddRequest;
import com.henu.registration.model.dto.registrationForm.RegistrationFormEditRequest;
import com.henu.registration.model.dto.registrationForm.RegistrationFormQueryRequest;
import com.henu.registration.model.dto.registrationForm.RegistrationFormUpdateRequest;
import com.henu.registration.model.entity.RegistrationForm;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.registrationForm.RegistrationFormVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.RegistrationFormService;
import com.henu.registration.service.SchoolSchoolTypeService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.caffeine.LocalCacheUtils;
import com.henu.registration.utils.redisson.cache.CacheUtils;
import com.henu.registration.utils.redisson.lock.LockUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 报名登记接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/registrationForm")
@Slf4j
public class RegistrationFormController {
	
	@Resource
	private RegistrationFormService registrationFormService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private SchoolSchoolTypeService schoolSchoolTypeService;
	
	// region 增删改查
	
	/**
	 * 创建报名登记
	 *
	 * @param registrationFormAddRequest registrationFormAddRequest
	 * @param request                    request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addRegistrationForm(@RequestBody RegistrationFormAddRequest registrationFormAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(registrationFormAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		RegistrationForm registrationForm = new RegistrationForm();
		BeanUtils.copyProperties(registrationFormAddRequest, registrationForm);
		List<String> studentLeaderList = registrationFormAddRequest.getStudentLeaders();
		if (CollUtil.isNotEmpty(studentLeaderList)) {
			registrationForm.setStudentLeaders(JSONUtil.toJsonStr(studentLeaderList));
		}
		// 数据校验
		registrationFormService.validRegistrationForm(registrationForm, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		registrationForm.setUserId(loginUser.getId());
		return LockUtils.lockEvent(loginUser.getId().toString(), () -> {
			// 判断该用户是否已经创建过相关信息
			RegistrationForm oldRegistrationForm = registrationFormService.getOne(Wrappers.lambdaQuery(RegistrationForm.class)
					.eq(RegistrationForm::getUserId, loginUser.getId())
					.eq(RegistrationForm::getJobId, registrationFormAddRequest.getJobId()));
			if (oldRegistrationForm != null) {
				registrationForm.setId(oldRegistrationForm.getId());
			}
			// 对身份证号进行加密
			String encryptIdCard = userService.getEncryptIdCard(registrationForm.getUserIdCard());
			RegistrationForm one = registrationFormService.getOne(Wrappers.lambdaQuery(RegistrationForm.class)
					.eq(RegistrationForm::getUserIdCard, encryptIdCard));
			ThrowUtils.throwIf(one != null && !Objects.equals(one.getId(), registrationForm.getUserId()), ErrorCode.OPERATION_ERROR, "该身份证号已存在");
			registrationForm.setUserIdCard(encryptIdCard);
			// 写入数据库
			boolean result = registrationFormService.saveOrUpdate(registrationForm);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
			// 返回新写入的数据 id
			long newRegistrationFormId = registrationForm.getId();
			return ResultUtils.success(newRegistrationFormId);
		}, () -> {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "请勿重复提交");
		});
	}
	
	/**
	 * 删除报名登记
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteRegistrationForm(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUserPermitNull(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		RegistrationForm oldRegistrationForm = registrationFormService.getById(id);
		ThrowUtils.throwIf(oldRegistrationForm == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldRegistrationForm.getUserId().equals(user.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = registrationFormService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新报名登记信息
	 *
	 * @param registrationFormUpdateRequest registrationFormUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateRegistrationForm(@RequestBody RegistrationFormUpdateRequest registrationFormUpdateRequest) {
		if (registrationFormUpdateRequest == null || registrationFormUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		RegistrationForm registrationForm = new RegistrationForm();
		BeanUtils.copyProperties(registrationFormUpdateRequest, registrationForm);
		List<String> studentLeaderList = registrationFormUpdateRequest.getStudentLeaders();
		if (CollUtil.isNotEmpty(studentLeaderList)) {
			registrationForm.setStudentLeaders(JSONUtil.toJsonStr(studentLeaderList));
		}
		// 判断是否存在
		long id = registrationFormUpdateRequest.getId();
		RegistrationForm oldRegistrationForm = registrationFormService.getById(id);
		ThrowUtils.throwIf(oldRegistrationForm == null, ErrorCode.NOT_FOUND_ERROR);
		// 数据校验
		registrationFormService.validRegistrationForm(registrationForm, false);
		// 对身份证号进行加密
		String userIdCard = registrationForm.getUserIdCard();
		if (StringUtils.isNotBlank(userIdCard)) {
			registrationForm.setUserIdCard(userService.getEncryptIdCard(userIdCard));
		}
		// 操作数据库
		boolean result = registrationFormService.updateById(registrationForm);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取报名登记（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<RegistrationFormVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<RegistrationFormVO> getRegistrationFormVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		RegistrationForm registrationForm = registrationFormService.getById(id);
		ThrowUtils.throwIf(registrationForm == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(registrationFormService.getRegistrationFormVO(registrationForm, request));
	}
	
	/**
	 * 分页获取报名登记列表（仅管理员可用）
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @return {@link BaseResponse<Page<RegistrationForm>>}
	 */
	@PostMapping("/list/page")
	public BaseResponse<Page<RegistrationForm>> listRegistrationFormByPage(@RequestBody RegistrationFormQueryRequest registrationFormQueryRequest) {
		long current = registrationFormQueryRequest.getCurrent();
		long size = registrationFormQueryRequest.getPageSize();
		// 查询数据库
		Page<RegistrationForm> registrationFormPage = registrationFormService.page(new Page<>(current, size),
				registrationFormService.getQueryWrapper(registrationFormQueryRequest));
		registrationFormPage.getRecords().forEach(registrationForm -> {
			registrationForm.setUserIdCard(userService.getDecryptIdCard(registrationForm.getUserIdCard()));
		});
		return ResultUtils.success(registrationFormPage);
	}
	
	/**
	 * 分页获取报名登记列表（封装类）
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @param request                      request
	 * @return {@link BaseResponse<Page<RegistrationFormVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<RegistrationFormVO>> listRegistrationFormVOByPage(@RequestBody RegistrationFormQueryRequest registrationFormQueryRequest,
	                                                                           HttpServletRequest request) {
		long current = registrationFormQueryRequest.getCurrent();
		long size = registrationFormQueryRequest.getPageSize();
		List<String> schoolTypes = registrationFormQueryRequest.getSchoolTypes();
		List<Long> schoolIdList = null;
		// 获取符合条件的学校 ID 列表
		if (CollUtil.isNotEmpty(schoolTypes)) {
			// 使用多级缓存优化查询
			// 构建缓存 key（基于查询条件的 MD5 哈希值）
			String queryCondition = JSONUtil.toJsonStr(schoolTypes);
			String hashKey = DigestUtils.md5DigestAsHex(queryCondition.getBytes());
			String cacheKey = "schoolTypes:" + hashKey;
			boolean cacheHit = false;
			// 本地缓存
			String cachedValue = (String) LocalCacheUtils.get(cacheKey);
			if (ObjUtil.isNotEmpty(cachedValue)) {
				schoolIdList = JSONUtil.toList(JSONUtil.parseArray(cachedValue), Long.class);
				cacheHit = true;
			}
			// Redis缓存
			if (!cacheHit) {
				cachedValue = CacheUtils.getString(cacheKey);
				if (ObjUtil.isNotEmpty(cachedValue)) {
					LocalCacheUtils.put(cacheKey, cachedValue);
					schoolIdList = JSONUtil.toList(JSONUtil.parseArray(cachedValue), Long.class);
					cacheHit = true;
				}
			}
			// 数据库
			if (!cacheHit) {
				QueryWrapper<SchoolSchoolType> queryWrapper = new QueryWrapper<>();
				if (CollUtil.isNotEmpty(schoolTypes)) {
					queryWrapper.and(wrapper -> {
						for (String schoolTypeName : schoolTypes) {
							wrapper.or().like("school_types", "\"" + schoolTypeName + "\"");
						}
					});
				}
				schoolIdList = schoolSchoolTypeService.list(queryWrapper).stream()
						.map(SchoolSchoolType::getSchoolId)
						.toList();
				// 放入缓存
				String toCache = JSONUtil.toJsonStr(schoolIdList);
				LocalCacheUtils.put(cacheKey, toCache);
				CacheUtils.putString(cacheKey, toCache, 3600);
			}
		}
		// 查询数据库
		Page<RegistrationForm> registrationFormPage = registrationFormService.page(new Page<>(current, size),
				registrationFormService.getQueryWrapper(registrationFormQueryRequest, schoolIdList));
		// 获取封装类
		registrationFormPage.getRecords().forEach(registrationForm -> registrationForm.setUserIdCard(userService.getDecryptIdCard(registrationForm.getUserIdCard())));
		return ResultUtils.success(registrationFormService.getRegistrationFormVOPage(registrationFormPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的报名登记列表
	 *
	 * @param registrationFormQueryRequest registrationFormQueryRequest
	 * @param request                      request
	 * @return {@link BaseResponse<Page<RegistrationFormVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<RegistrationFormVO>> listMyRegistrationFormVOByPage(@RequestBody RegistrationFormQueryRequest registrationFormQueryRequest,
	                                                                             HttpServletRequest request) {
		ThrowUtils.throwIf(registrationFormQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		registrationFormQueryRequest.setUserId(loginUser.getId());
		long current = registrationFormQueryRequest.getCurrent();
		long size = registrationFormQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<RegistrationForm> registrationFormPage = registrationFormService.page(new Page<>(current, size),
				registrationFormService.getQueryWrapper(registrationFormQueryRequest));
		// 获取封装类
		registrationFormPage.getRecords().forEach(registrationForm -> registrationForm.setUserIdCard(userService.getDecryptIdCard(registrationForm.getUserIdCard())));
		// 获取封装类
		return ResultUtils.success(registrationFormService.getRegistrationFormVOPage(registrationFormPage, request));
	}
	
	/**
	 * 编辑报名登记（给用户使用）
	 *
	 * @param registrationFormEditRequest registrationFormEditRequest
	 * @param request                     request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editRegistrationForm(@RequestBody RegistrationFormEditRequest registrationFormEditRequest, HttpServletRequest request) {
		if (registrationFormEditRequest == null || registrationFormEditRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		RegistrationForm registrationForm = new RegistrationForm();
		BeanUtils.copyProperties(registrationFormEditRequest, registrationForm);
		List<String> studentLeaderList = registrationFormEditRequest.getStudentLeaders();
		if (CollUtil.isNotEmpty(studentLeaderList)) {
			registrationForm.setStudentLeaders(JSONUtil.toJsonStr(studentLeaderList));
		}
		// 数据校验
		registrationFormService.validRegistrationForm(registrationForm, false);
		User loginUser = userService.getLoginUser(request);
		// 判断是否存在
		long id = registrationFormEditRequest.getId();
		RegistrationForm oldRegistrationForm = registrationFormService.getById(id);
		ThrowUtils.throwIf(oldRegistrationForm == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可编辑
		if (!oldRegistrationForm.getUserId().equals(loginUser.getId()) && !adminService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 对身份证号进行加密
		String userIdCard = registrationForm.getUserIdCard();
		if (StringUtils.isNotBlank(userIdCard)) {
			String encryptIdCard = userService.getEncryptIdCard(userIdCard);
			RegistrationForm one = registrationFormService.getOne(Wrappers.lambdaQuery(RegistrationForm.class)
					.eq(RegistrationForm::getUserIdCard, encryptIdCard));
			ThrowUtils.throwIf(one != null && !Objects.equals(one.getUserId(), loginUser.getId()), ErrorCode.OPERATION_ERROR, "该身份证号已存在");
			registrationForm.setUserIdCard(encryptIdCard);
		}
		// 操作数据库
		boolean result = registrationFormService.updateById(registrationForm);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	// endregion
}