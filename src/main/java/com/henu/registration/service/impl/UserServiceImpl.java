package com.henu.registration.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.security.utils.DeviceUtils;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.constants.SaltConstant;
import com.henu.registration.constants.UserConstant;
import com.henu.registration.mapper.UserMapper;
import com.henu.registration.model.dto.user.UserQueryRequest;
import com.henu.registration.model.dto.user.UserRegisterRequest;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.user.LoginUserVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.encrypt.EncryptionUtils;
import com.henu.registration.utils.encrypt.MD5Utils;
import com.henu.registration.utils.redisson.lock.LockUtils;
import com.henu.registration.utils.regex.RegexUtils;
import com.henu.registration.utils.satoken.StpKit;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 用户服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
	
	/**
	 * 校验数据
	 *
	 * @param user user
	 * @param add  对创建的数据进行校验
	 */
	@Override
	public void validUser(User user, boolean add) {
		ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String userAccount = user.getUserAccount();
		String userPassword = user.getUserPassword();
		String userIdCard = user.getUserIdCard();
		String userName = user.getUserName();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "账号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "密码不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userAccount)) {
			ThrowUtils.throwIf(userAccount.length() < 4, ErrorCode.PARAMS_ERROR, "账号过短");
			// 账户不能重复
			LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class)
					.eq(User::getUserAccount, userAccount);
			long count = this.count(eq);
			if (count > 0) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
			}
		}
		if (StringUtils.isNotBlank(userPassword)) {
			ThrowUtils.throwIf(userPassword.length() < 8, ErrorCode.PARAMS_ERROR, "密码过短");
		}
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		}
		if (StringUtils.isNotBlank(userEmail)) {
			ThrowUtils.throwIf(!RegexUtils.checkEmail(userEmail), ErrorCode.PARAMS_ERROR, "邮箱输入有误");
		}
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
	}
	
	/**
	 * @param userPassword 用户密码
	 * @return String
	 */
	@Override
	public String getEncryptPassword(String userPassword) {
		return MD5Utils.encrypt(SaltConstant.USER_SALT + userPassword);
	}
	
	/**
	 * 获得加密身份证号
	 *
	 * @param userIdCard userIdCard
	 * @return String
	 */
	@Override
	public String getEncryptIdCard(String userIdCard) {
		return EncryptionUtils.encrypt(userIdCard);
	}
	
	/**
	 * 获得解密身份证号
	 *
	 * @param userIdCard userIdCard
	 * @return String
	 */
	@Override
	public String getDecryptIdCard(String userIdCard) {
		return EncryptionUtils.decrypt(userIdCard);
	}
	
	/**
	 * 用户注册
	 * @param userRegisterRequest userRegisterRequest
	 * @return long 注册成功之后的id
	 */
	@Override
	public long userRegister(UserRegisterRequest userRegisterRequest) {
		// 1. 校验
		String userAccount = userRegisterRequest.getUserAccount();
		String userPassword = userRegisterRequest.getUserPassword();
		String checkUserPassword = userRegisterRequest.getCheckUserPassword();
		if (StringUtils.isAnyBlank(userAccount, userPassword, checkUserPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
		}
		if (userPassword.length() < 8 || checkUserPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
		}
		// 密码和校验密码相同
		if (!userPassword.equals(checkUserPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
		}
		return LockUtils.lockEvent(userAccount.intern(), () -> {
			// 账户不能重复
			LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class)
					.eq(User::getUserAccount, userAccount);
			long count = this.count(eq);
			if (count > 0) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
			}
			// 2. 加密
			String encryptPassword = this.getEncryptPassword(userPassword);
			// 3. 插入数据
			User user = new User();
			user.setUserAccount(userAccount);
			user.setUserPassword(encryptPassword);
			boolean saveResult = this.save(user);
			if (!saveResult) {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}, () -> {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，用户账户已被注册");
		});
	}
	
	/**
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param request      request
	 * @return {@link LoginUserVO}
	 */
	@Override
	public LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userAccount, userPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		if (userAccount.length() < 4) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
		}
		if (userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
		}
		// 2. 加密
		String encryptPassword = this.getEncryptPassword(userPassword);
		// 查询用户是否存在
		LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class)
				.eq(User::getUserAccount, userAccount)
				.eq(User::getUserPassword, encryptPassword);
		User user = this.getOne(eq);
		// 用户不存在
		if (user == null) {
			log.info("user login failed, userAccount cannot match userPassword");
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		// 3. 记录用户的登录态
		// 使用Sa-Token登录，并指定设备同端登录互斥
		StpKit.USER.login(user.getId(), DeviceUtils.getRequestDevice(request));
		StpKit.USER.getSession().set(UserConstant.USER_LOGIN_STATE, user);
		return this.getLoginUserVO(user);
	}
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return {@link User}
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		// 先判断是否已经登录
		Object loginUserId = StpKit.USER.getLoginIdDefaultNull();
		if (loginUserId == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
		}
		User currentUser = this.getById((String) loginUserId);
		if (currentUser == null || currentUser.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentUser;
	}
	
	/**
	 * 获取当前登录用户（允许未登录）
	 *
	 * @param request request
	 * @return {@link User}
	 */
	@Override
	public User getLoginUserPermitNull(HttpServletRequest request) {
		// 先判断是否登录
		if (!StpKit.USER.isLogin()) {
			return null;
		}
		// 直接获取用户 ID
		long userId = StpKit.USER.getLoginIdAsLong();
		return this.getById(userId);
	}
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return boolean 是否退出成功
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		// 判断是否登录
		StpKit.USER.checkLogin();
		// 移除登录态
		StpKit.USER.logout();
		return true;
	}
	
	/**
	 * 获取登录用户视图类
	 *
	 * @param user user
	 * @return {@link LoginUserVO}
	 */
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		if (user == null) {
			return null;
		}
		// todo 在此处将实体类和 DTO 进行转换
		LoginUserVO loginUserVO = new LoginUserVO();
		BeanUtils.copyProperties(user, loginUserVO);
		// 设置将token保存到登录用户信息中
		loginUserVO.setToken(StpKit.USER.getTokenInfo().getTokenValue());
		return loginUserVO;
	}
	
	/**
	 * 获取用户VO封装类
	 *
	 * @param user    user
	 * @param request request
	 * @return {@link UserVO}
	 */
	@Override
	public UserVO getUserVO(User user, HttpServletRequest request) {
		// 对象转封装类
		return UserVO.objToVo(user);
	}
	
	
	/**
	 * 获得用户视图类列表
	 *
	 * @param userList userList
	 * @param request  request
	 * @return {@link List<UserVO>}
	 */
	@Override
	public List<UserVO> getUserVO(List<User> userList, HttpServletRequest request) {
		if (CollUtil.isEmpty(userList)) {
			return new ArrayList<>();
		}
		return userList.stream().map(user -> getUserVO(user, request)).collect(Collectors.toList());
	}
	
	/**
	 * 分页获取用户视图类
	 *
	 * @param userPage userPage
	 * @param request  request
	 * @return {@link Page {@link UserVO} }
	 */
	@Override
	public Page<UserVO> getUserVOPage(Page<User> userPage, HttpServletRequest request) {
		List<User> userList = userPage.getRecords();
		Page<UserVO> userVOPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
		if (CollUtil.isEmpty(userList)) {
			return userVOPage;
		}
		// 填充信息
		List<UserVO> userVOList = userList.stream().map(UserVO::objToVo).collect(Collectors.toList());
		userVOPage.setRecords(userVOList);
		
		return userVOPage;
	}
	
	/**
	 * 获取查询封装类
	 *
	 * @param userQueryRequest userQueryRequest
	 * @return {@link QueryWrapper<User>}
	 */
	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		Long id = userQueryRequest.getId();
		Long notId = userQueryRequest.getNotId();
		String userAccount = userQueryRequest.getUserAccount();
		String userPassword = userQueryRequest.getUserPassword();
		String userIdCard = userQueryRequest.getUserIdCard();
		if (StringUtils.isNotBlank(userIdCard)) {
			userIdCard = this.getEncryptIdCard(userIdCard);
		}
		String userName = userQueryRequest.getUserName();
		String userEmail = userQueryRequest.getUserEmail();
		String userPhone = userQueryRequest.getUserPhone();
		Integer userGender = userQueryRequest.getUserGender();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		// 精准查询
		queryWrapper.eq(id != null, "id", id);
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(StringUtils.isNotBlank(userIdCard), "user_id_card", userIdCard);
		queryWrapper.eq(StringUtils.isNotBlank(userPhone), "user_phone", userPhone);
		queryWrapper.eq(StringUtils.isNotBlank(userAccount), "user_account", userAccount);
		queryWrapper.eq(StringUtils.isNotBlank(userPassword), "user_password", userPassword);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "user_gender", userGender);
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "user_email", userEmail);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
}
