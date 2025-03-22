package com.henu.registration.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.user.LoginUserVO;
import com.henu.registration.model.vo.user.UserVO;
import com.henu.registration.service.UserService;
import com.henu.registration.utils.redisson.lock.LockUtils;
import com.henu.registration.utils.regex.RegexUtils;
import com.henu.registration.utils.satoken.StpKit;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

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
		String userIdCard = user.getUserIdCard();
		String userName = user.getUserName();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		String emergencyPhone = user.getEmergencyPhone();
		
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		}
		if (StringUtils.isNotBlank(userEmail)) {
			ThrowUtils.throwIf(!RegexUtils.checkEmail(userEmail), ErrorCode.PARAMS_ERROR, "邮箱输入有误");
		}
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
		if (StringUtils.isNotBlank(emergencyPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(emergencyPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
	}
	
	/**
	 * 获得加密密码
	 *
	 * @param userIdCard userIdCard
	 * @return String
	 */
	@Override
	public String getEncryptIdCard(String userIdCard) {
		return DigestUtils.md5DigestAsHex((SaltConstant.SALT + userIdCard).getBytes());
	}
	
	/**
	 * 用户注册
	 *
	 * @param userIdCard      用户身份证号码
	 * @param userName        用户姓名
	 * @param checkUserIdCard 校验用户身份证号码
	 * @return long 新用户 id
	 */
	@Override
	public long userRegister(String userIdCard, String userName, String checkUserIdCard) {
		// 1. 校验
		// 密码和校验密码相同
		if (!userIdCard.equals(checkUserIdCard)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的身份证号不一致");
		}
		return LockUtils.lockEvent(userIdCard.intern(), () -> {
			// 账户不能重复
			QueryWrapper<User> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("user_id_card", userIdCard);
			long count = this.baseMapper.selectCount(queryWrapper);
			if (count > 0) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户身份证号已被注册");
			}
			// 2. 加密
			String encryptIdCard = this.getEncryptIdCard(userIdCard);
			// 3. 插入数据
			User user = new User();
			user.setUserName(userName);
			user.setUserIdCard(encryptIdCard);
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
	 * 用户登录
	 *
	 * @param userName   用户账户
	 * @param userIdCard 身份证号
	 * @param request    request
	 * @return {@link LoginUserVO}
	 */
	@Override
	public LoginUserVO userLogin(String userName, String userIdCard, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userName, userIdCard)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		// 2. 加密
		String encryptIdCard = this.getEncryptIdCard(userIdCard);
		// 查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_name", userName);
		queryWrapper.eq("user_id_card", encryptIdCard);
		User user = this.baseMapper.selectOne(queryWrapper);
		// 用户不存在
		if (user == null) {
			log.info("user login failed, userName cannot match userIdCard");
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
		String userIdCard = userQueryRequest.getUserIdCard();
		String userName = userQueryRequest.getUserName();
		String userEmail = userQueryRequest.getUserEmail();
		String userPhone = userQueryRequest.getUserPhone();
		Integer userGender = userQueryRequest.getUserGender();
		String ethnic = userQueryRequest.getEthnic();
		String partyTime = userQueryRequest.getPartyTime();
		String birthDate = userQueryRequest.getBirthDate();
		Integer marryStatus = userQueryRequest.getMarryStatus();
		String emergencyPhone = userQueryRequest.getEmergencyPhone();
		String address = userQueryRequest.getAddress();
		String workExperience = userQueryRequest.getWorkExperience();
		String studentLeaderAwards = userQueryRequest.getStudentLeaderAwards();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		// 精准查询
		queryWrapper.eq(id != null, "id", id);
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(StringUtils.isNotBlank(userIdCard), "user_id_card", this.getEncryptIdCard(userIdCard));
		queryWrapper.eq(StringUtils.isNotBlank(userPhone), "user_phone", userPhone);
		queryWrapper.eq(StringUtils.isNotBlank(emergencyPhone), "emergency_phone", emergencyPhone);
		queryWrapper.eq(StringUtils.isNotBlank(ethnic), "ethnic", ethnic);
		queryWrapper.eq(userGender != null, "user_gender", userGender);
		queryWrapper.eq(marryStatus != null, "marry_status", marryStatus);
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "user_email", userEmail);
		queryWrapper.like(StringUtils.isNotBlank(address), "address", address);
		queryWrapper.like(StringUtils.isNotBlank(workExperience), "work_experience", workExperience);
		queryWrapper.like(StringUtils.isNotBlank(studentLeaderAwards), "student_leader_awards", studentLeaderAwards);
		queryWrapper.like(StringUtils.isNotBlank(partyTime), "party_time", partyTime);
		queryWrapper.like(StringUtils.isNotBlank(birthDate), "birth_date", birthDate);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
}
