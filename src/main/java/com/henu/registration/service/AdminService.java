package com.henu.registration.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.henu.registration.model.dto.admin.AdminQueryRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.admin.LoginAdminVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author stephenqiu
 * @description 针对表【admin】的数据库操作Service
 * @createDate 2025-03-20 23:23:57
 */
public interface AdminService extends IService<Admin> {
	/**
	 * 管理员登录
	 *
	 * @param adminNumber   工号
	 * @param adminPassword 密码
	 * @param request       request
	 * @return {@link LoginAdminVO}
	 */
	LoginAdminVO adminLogin(String adminNumber, String adminPassword, HttpServletRequest request);
	
	/**
	 * 校验数据
	 *
	 * @param admin admin
	 * @param add   对创建的数据进行校验
	 */
	void validAdmin(Admin admin, boolean add);
	
	String getEncryptPassword(String userPassword);
	
	/**
	 * 获取当前登录的管理员
	 *
	 * @param request request
	 * @return {@link Admin}
	 */
	Admin getLoginAdmin(HttpServletRequest request);
	
	/**
	 * 获取当前登录管理员（允许未登录）
	 *
	 * @param request request
	 * @return {@link Admin}
	 */
	Admin getLoginAdminPermitNull(HttpServletRequest request);
	
	/**
	 * 是否为系统管理员
	 *
	 * @param request request
	 * @return boolean 是否为管理员
	 */
	boolean isAdmin(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param admin admin
	 * @return boolean 是否为管理员
	 */
	boolean isAdmin(Admin admin);
	
	/**
	 * 管理员注销
	 *
	 * @param request request
	 * @return boolean 是否退出成功
	 */
	boolean AdminLogout(HttpServletRequest request);
	
	/**
	 * 获取当前登录管理员视图类
	 *
	 * @param admin admin
	 * @return {@link LoginAdminVO}
	 */
	LoginAdminVO getLoginAdminVO(Admin admin);
	
	/**
	 * 获取查询条件
	 *
	 * @param adminQueryRequest adminQueryRequest
	 * @return {@link QueryWrapper<Admin>}
	 */
	QueryWrapper<Admin> getQueryWrapper(AdminQueryRequest adminQueryRequest);
	
	/**
	 * 获取管理员封装
	 *
	 * @param admin   admin
	 * @param request request
	 * @return {@link AdminVO}
	 */
	AdminVO getAdminVO(Admin admin, HttpServletRequest request);
	
	/**
	 * 分页获取管理员封装
	 *
	 * @param adminPage adminPage
	 * @param request   request
	 * @return {@link Page<AdminVO>}
	 */
	Page<AdminVO> getAdminVOPage(Page<Admin> adminPage, HttpServletRequest request);
}
