package com.henu.registration.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.henu.registration.common.*;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.model.dto.user.*;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.user.LoginUserVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.encrypt.EncryptionUtils;
import com.henu.registration.utils.regex.RegexUtils;
import com.henu.registration.utils.sms.SMSUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 用户接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
	
	@Resource
	private UserService userService;
	
	@Resource
	private AdminService adminService;
	
	@Resource
	private SMSUtils smsUtils;
	
	// region 登录相关
	
	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return BaseResponse<Long> 注册是否成功
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		long result = userService.userRegister(userRegisterRequest);
		// 调用用户注册服务
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户登录
	 *
	 * @param userLoginRequest userLoginRequest
	 * @param request          request
	 * @return BaseResponse<LoginUserVO>
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
		if (userLoginRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		String userPassword = userLoginRequest.getUserPassword();
		String userAccount = userLoginRequest.getUserAccount();
		ThrowUtils.throwIf(StringUtils.isAnyBlank(userPassword, userAccount), ErrorCode.PARAMS_ERROR);
		LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
		return ResultUtils.success(loginUserVO);
	}
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
		if (request == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		boolean result = userService.userLogout(request);
		return ResultUtils.success(result);
	}
	
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return BaseResponse<LoginUserVO>
	 */
	@GetMapping("/get/login")
	public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
		User user = userService.getLoginUser(request);
		return ResultUtils.success(userService.getLoginUserVO(user));
	}
	
	// endregion
	
	// region 增删改查
	
	/**
	 * 创建用户
	 *
	 * @param userAddRequest userAddRequest
	 * @param request        request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userAddRequest, user);
		// 数据校验
		userService.validUser(user, true);
		// 数据加密
		user.setUserIdCard(userService.getDecryptIdCard(user.getUserIdCard()));
		user.setUserPassword(userService.getEncryptPassword(user.getUserPassword()));
		// 写入数据库
		boolean result = userService.save(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newTagId = user.getId();
		return ResultUtils.success(newTagId);
		
	}
	
	/**
	 * 删除用户
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return /ioBaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
		// 1. 获取当前登录用户和管理员
		// 普通用户（可能为空）
		User user = userService.getLoginUserPermitNull(request);
		// 管理员（可能为空）
		Admin loginAdmin = adminService.getLoginAdmin(request);
		long id = deleteRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 2. 权限校验
		if (!oldUser.getId().equals(user.getId()) && !adminService.isAdmin(loginAdmin)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest userUpdateRequest
	 * @param request           request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest,
	                                        HttpServletRequest request) {
		if (userUpdateRequest == null || userUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userUpdateRequest, user);
		// 数据校验
		userService.validUser(user, false);
		// 判断是否存在
		long id = userUpdateRequest.getId();
		User oldUser = userService.getById(id);
		ThrowUtils.throwIf(oldUser == null, ErrorCode.NOT_FOUND_ERROR);
		// 如果用户需要修改密码
		if (StringUtils.isNotBlank(userUpdateRequest.getUserIdCard())) {
			// todo 身份证加密
			String encryptPassword = userService.getEncryptIdCard(userUpdateRequest.getUserIdCard());
			user.setUserIdCard(encryptPassword);
		}
		// 操作数据库
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户（仅系统管理员）
	 *
	 * @param id      用户id
	 * @param request request
	 * @return BaseResponse<User>
	 */
	@GetMapping("/get")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<User> getUserById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		user.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
		return ResultUtils.success(user);
	}
	
	/**
	 * 根据 id 获取包装类
	 *
	 * @param id      用户id
	 * @param request request
	 * @return 查询得到的用户包装类
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserVO> getUserVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		User user = userService.getById(id);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userService.getUserVO(user, request));
	}
	
	
	/**
	 * 分页获取用户列表（仅管理员）
	 *
	 * @param userQueryRequest userQueryRequest
	 * @param request          request
	 * @return BaseResponse<Page < User>>
	 */
	@PostMapping("/list/page")
	@SaCheckRole(AdminConstant.SYSTEM_ADMIN)
	public BaseResponse<Page<User>> listUserByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                               HttpServletRequest request) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		// todo 在此处将实体类和 DTO 进行转换
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		userPage.getRecords().forEach(user -> {
			if (StringUtils.isNotBlank(user.getUserIdCard())) {
				user.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
			}
		});
		return ResultUtils.success(userPage);
	}
	
	/**
	 * 分页获取用户封装列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @param request          request
	 * @return BaseResponse<Page < UserVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest,
	                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
		long current = userQueryRequest.getCurrent();
		long size = userQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		Page<User> userPage = userService.page(new Page<>(current, size),
				userService.getQueryWrapper(userQueryRequest));
		Page<UserVO> userVOPage = new Page<>(current, size, userPage.getTotal());
		List<UserVO> userVO = userService.getUserVO(userPage.getRecords(), request);
		userVOPage.setRecords(userVO);
		return ResultUtils.success(userVOPage);
	}
	
	// endregion
	
	/**
	 * 更新个人信息
	 *
	 * @param userEditRequest userEditRequest
	 * @param request         request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update/my")
	public BaseResponse<Boolean> updateMyUser(@RequestBody UserEditRequest userEditRequest,
	                                          HttpServletRequest request) {
		ThrowUtils.throwIf(userEditRequest == null, ErrorCode.PARAMS_ERROR);
		User loginUser = userService.getLoginUser(request);
		// todo 在此处将实体类和 DTO 进行转换
		User user = new User();
		BeanUtils.copyProperties(userEditRequest, user);
		// 对用户数据进行校验
		try {
			userService.validUser(user, false);
		} catch (Exception e) {
			return ResultUtils.error(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// 如果用户需要修改密码
		if (StringUtils.isNotBlank(userEditRequest.getUserIdCard())) {
			// todo 身份证加密
			String encryptIdCard = userService.getEncryptIdCard(userEditRequest.getUserIdCard());
			user.setUserIdCard(encryptIdCard);
		}
		user.setId(loginUser.getId());
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 发送验证码(短信验证)
	 *
	 * @param userUpdatePasswordRequest userUpdatePasswordRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/recovery/code")
	public BaseResponse<Boolean> sentRecoveryCode(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
		ThrowUtils.throwIf(userUpdatePasswordRequest == null, ErrorCode.PARAMS_ERROR);
		// 1. 校验手机号格式
		String userPhone = userUpdatePasswordRequest.getUserPhone();
		ThrowUtils.throwIf(!RegexUtils.checkPhone(userPhone), ErrorCode.PARAMS_ERROR, "手机号格式不正确");
		LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class).eq(User::getUserPhone, userPhone);
		User user = userService.getOne(eq);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "该手机号未注册");
		// 2. 生成验证码
		String verificationCode = RandomUtil.randomNumbers(6);
		// 3. 发送验证码到手机号
		try {
			smsUtils.sendRecoveryCode(userPhone, verificationCode);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "验证码发送失败");
		}
		return ResultUtils.success(true);
	}
	
	/**
	 * 验证验证码
	 *
	 * @param userUpdatePasswordRequest userUpdatePasswordRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/recovery/verify")
	public BaseResponse<Boolean> verifyRecoveryCode(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
		ThrowUtils.throwIf(userUpdatePasswordRequest == null, ErrorCode.PARAMS_ERROR);
		// 1. 校验手机号格式
		String userPhone = userUpdatePasswordRequest.getUserPhone();
		ThrowUtils.throwIf(!RegexUtils.checkPhone(userPhone), ErrorCode.PARAMS_ERROR, "手机号格式不正确");
		// 2. 获取验证码
		String inputCode = userUpdatePasswordRequest.getVerificationCode();
		ThrowUtils.throwIf(inputCode == null || inputCode.isEmpty(), ErrorCode.PARAMS_ERROR, "验证码不能为空");
		// 3. 从缓存中获取验证码
		smsUtils.verifyRecoveryCode(userPhone, inputCode);
		return ResultUtils.success(true);
	}
	
	
	/**
	 * 修改密码(短信验证)
	 *
	 * @param userUpdatePasswordRequest userUpdatePasswordRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update/password")
	public BaseResponse<Boolean> updatePassword(@RequestBody UserUpdatePasswordRequest userUpdatePasswordRequest) {
		ThrowUtils.throwIf(userUpdatePasswordRequest == null, ErrorCode.PARAMS_ERROR);
		// 对用户数据进行校验
		String userPhone = userUpdatePasswordRequest.getUserPhone();
		String userPassword = userUpdatePasswordRequest.getUserPassword();
		String checkUserPassword = userUpdatePasswordRequest.getCheckUserPassword();
		ThrowUtils.throwIf(!RegexUtils.checkPhone(userPhone), ErrorCode.PARAMS_ERROR, "手机号格式不正确");
		if (!userPassword.equals(checkUserPassword)) {
			return ResultUtils.error(ErrorCode.PARAMS_ERROR, "两次密码不一致");
		}
		LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class).eq(User::getUserPhone, userPhone);
		User user = userService.getOne(eq);
		ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "该手机号未注册");
		user.setUserPassword(userService.getEncryptPassword(userPassword));
		boolean result = userService.updateById(user);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
}
