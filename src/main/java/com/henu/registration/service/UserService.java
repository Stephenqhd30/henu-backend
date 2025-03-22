package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.user.UserQueryRequest;
import com.henu.registration.model.entity.User;
import com.henu.registration.model.vo.user.LoginUserVO;
import com.henu.registration.model.vo.user.UserVO;


import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author stephen qiu
 */
public interface UserService extends IService<User> {
	
	/**
	 * 校验用户参数
	 *
	 * @param user user
	 * @param add  是否是添加
	 */
	void validUser(User user, boolean add);
	
	/**
	 * 获得加密密码
	 *
	 * @param userPassword userPassword
	 * @return String
	 */
	String getEncryptIdCard(String userPassword);
	
	/**
	 * 用户注册
	 *
	 * @param userIdCard      用户身份证号码
	 * @param userName        用户姓名
	 * @param checkUserIdCard 校验用户身份证号码
	 * @return long 新用户 id
	 */
	long userRegister(String userIdCard, String userName, String checkUserIdCard);
	
	/**
	 * 用户登录
	 *
	 * @param userName   用户账户
	 * @param userIdCard 身份证号
	 * @param request    request
	 * @return {@link LoginUserVO}
	 */
	LoginUserVO userLogin(String userName, String userIdCard, HttpServletRequest request);
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return {@link User}
	 */
	User getLoginUser(HttpServletRequest request);
	
	/**
	 * 获取当前登录用户（允许未登录）
	 *
	 * @param request request
	 * @return {@link User}
	 */
	User getLoginUserPermitNull(HttpServletRequest request);
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return boolean 用户注销
	 */
	boolean userLogout(HttpServletRequest request);
	
	/**
	 * 获取脱敏的已登录用户信息
	 *
	 * @return {@link LoginUserVO}
	 */
	LoginUserVO getLoginUserVO(User user);
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param user    user
	 * @param request request
	 * @return {@link UserVO}
	 */
	UserVO getUserVO(User user, HttpServletRequest request);
	
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param userList userList
	 * @return {@link List<UserVO>}
	 */
	List<UserVO> getUserVO(List<User> userList, HttpServletRequest request);
	
	/**
	 * 分页获取用户视图类
	 *
	 * @param userPage userPage
	 * @param request  request
	 * @return {@link Page {@link UserVO} }
	 */
	Page<UserVO> getUserVOPage(Page<User> userPage, HttpServletRequest request);
	
	/**
	 * 获取查询条件
	 *
	 * @param userQueryRequest userQueryRequest
	 * @return {@link QueryWrapper<User>}
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
}
