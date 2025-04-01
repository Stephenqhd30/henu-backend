package com.henu.registration.service.impl;

import cn.dev33.satoken.stp.StpUtil;
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
import com.henu.registration.constants.AdminConstant;
import com.henu.registration.constants.CommonConstant;
import com.henu.registration.constants.SaltConstant;
import com.henu.registration.mapper.AdminMapper;
import com.henu.registration.model.dto.admin.AdminQueryRequest;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.enums.AdminTyprEnum;
import com.henu.registration.model.vo.admin.AdminVO;
import com.henu.registration.model.vo.admin.LoginAdminVO;
import com.henu.registration.service.AdminService;
import com.henu.registration.utils.sql.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author stephenqiu
 * @description 针对表【admin】的数据库操作Service实现
 * @createDate 2025-03-20 23:23:57
 */
@Service
@Slf4j
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin>
		implements AdminService {
	
	/**
	 * 管理员登录
	 *
	 * @param adminNumber   工号
	 * @param adminPassword 密码
	 * @param request       request
	 * @return {@link LoginAdminVO}
	 */
	@Override
	public LoginAdminVO adminLogin(String adminNumber, String adminPassword, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(adminNumber, adminPassword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 2. 加密
		String encryptPassword = this.getEncryptPassword(adminPassword);
		// 3. 查询用户是否存在
		LambdaQueryWrapper<Admin> eq = Wrappers.lambdaQuery(Admin.class)
				.eq(Admin::getAdminNumber, adminNumber)
				.eq(Admin::getAdminPassword, encryptPassword);
		Admin admin = this.getOne(eq);
		// 用户不存在
		if (admin == null) {
			log.info("user login failed, userAccount cannot match userPassword");
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		// 4. 记录管理员的登录态（避免和普通用户冲突）
		StpUtil.login(admin.getId(), DeviceUtils.getRequestDevice(request));
		StpUtil.getSession().set(AdminConstant.ADMIN_LOGIN_STATE, admin);
		return this.getLoginAdminVO(admin);
	}
	
	
	/**
	 * 获取当前登录的管理员
	 *
	 * @param request request
	 * @return {@link Admin}
	 */
	@Override
	public Admin getLoginAdmin(HttpServletRequest request) {
		// 先判断管理员是否已经登录
		Object loginAdminId = StpUtil.getLoginIdDefaultNull();
		if (loginAdminId == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "管理员未登录");
		}
		// 从数据库查询（可优化为缓存）
		Admin currentAdmin = this.getById((String) loginAdminId);
		if (currentAdmin == null || currentAdmin.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentAdmin;
	}
	
	/**
	 * 获取当前登录管理员（允许未登录）
	 *
	 * @param request request
	 * @return {@link Admin}
	 */
	@Override
	public Admin getLoginAdminPermitNull(HttpServletRequest request) {
		// 先判断是否登录
		if (!StpUtil.isLogin()) {
			return null;
		}
		// 直接获取用户 ID
		long adminId = StpUtil.getLoginIdAsLong();
		return this.getById(adminId);
	}
	
	
	/**
	 * 是否为系统管理员
	 *
	 * @param request request
	 * @return boolean 是否为管理员
	 */
	@Override
	public boolean isAdmin(HttpServletRequest request) {
		Admin admin = (Admin) StpUtil.getSession().get(AdminConstant.ADMIN_LOGIN_STATE);
		return admin != null && isAdmin(admin);
	}
	
	/**
	 * 是否为管理员
	 *
	 * @param admin admin
	 * @return boolean 是否为管理员
	 */
	@Override
	public boolean isAdmin(Admin admin) {
		return !AdminTyprEnum.SYSTEM_ADMIN.getValue().equals(admin.getAdminType());
	}
	
	/**
	 * 管理员注销
	 *
	 * @param request request
	 * @return boolean 是否退出成功
	 */
	@Override
	public boolean adminLogout(HttpServletRequest request) {
		// 先检查管理员是否登录
		StpUtil.checkLogin();
		// 移除管理员登录态
		StpUtil.logout();
		return true;
	}
	
	/**
	 * 校验数据
	 *
	 * @param admin admin
	 * @param add   对创建的数据进行校验
	 */
	@Override
	public void validAdmin(Admin admin, boolean add) {
		ThrowUtils.throwIf(admin == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String adminNumber = admin.getAdminNumber();
		String adminName = admin.getAdminName();
		String adminType = admin.getAdminType();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(adminNumber), ErrorCode.PARAMS_ERROR, "管理员编号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(adminName), ErrorCode.PARAMS_ERROR, "管理员名不能为空");
			if (StringUtils.isNotBlank(adminNumber)) {
				LambdaQueryWrapper<Admin> eq = Wrappers.lambdaQuery(Admin.class)
						.eq(Admin::getAdminNumber, adminNumber)
						.eq(Admin::getIsDelete, false);
				Admin one = this.getOne(eq);
				ThrowUtils.throwIf(one != null, ErrorCode.PARAMS_ERROR, "管理员编号已存在");
			}
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(adminName)) {
			ThrowUtils.throwIf(adminName.length() > 20, ErrorCode.PARAMS_ERROR, "管理员名过长");
		}
		if (StringUtils.isNotBlank(adminType)) {
			ThrowUtils.throwIf(AdminTyprEnum.getEnumByValue(adminType) == null, ErrorCode.PARAMS_ERROR, "管理员类型错误");
		}
	}
	
	/**
	 * 获得加密密码
	 *
	 * @param adminPassword userPassword
	 * @return String
	 */
	@Override
	public String getEncryptPassword(String adminPassword) {
		return DigestUtils.md5DigestAsHex((SaltConstant.ADMIN_SALT + adminPassword).getBytes());
	}
	
	/**
	 * 获取当前登录管理员视图类
	 *
	 * @param admin admin
	 * @return {@link LoginAdminVO}
	 */
	@Override
	public LoginAdminVO getLoginAdminVO(Admin admin) {
		if (admin == null) {
			return null;
		}
		// todo 在此处将实体类和 DTO 进行转换
		LoginAdminVO loginAdminVO = new LoginAdminVO();
		BeanUtils.copyProperties(admin, loginAdminVO);
		// 设置将token保存到登录用户信息中
		loginAdminVO.setToken(StpUtil.getTokenInfo().getTokenValue());
		return loginAdminVO;
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param adminQueryRequest adminQueryRequest
	 * @return {@link QueryWrapper<Admin>}
	 */
	@Override
	public QueryWrapper<Admin> getQueryWrapper(AdminQueryRequest adminQueryRequest) {
		QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
		if (adminQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = adminQueryRequest.getId();
		Long notId = adminQueryRequest.getNotId();
		String adminNumber = adminQueryRequest.getAdminNumber();
		String adminName = adminQueryRequest.getAdminName();
		String adminType = adminQueryRequest.getAdminType();
		String sortField = adminQueryRequest.getSortField();
		String sortOrder = adminQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(adminNumber), "admin_number", adminNumber);
		queryWrapper.like(StringUtils.isNotBlank(adminName), "admin_name", adminName);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(adminType), "admin_type", adminType);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取管理员封装
	 *
	 * @param admin   admin
	 * @param request request
	 * @return {@link AdminVO}
	 */
	@Override
	public AdminVO getAdminVO(Admin admin, HttpServletRequest request) {
		// 对象转封装类
		return AdminVO.objToVo(admin);
	}
	
	/**
	 * 分页获取管理员封装
	 *
	 * @param adminPage adminPage
	 * @param request   request
	 * @return {@link Page<AdminVO>}
	 */
	@Override
	public Page<AdminVO> getAdminVOPage(Page<Admin> adminPage, HttpServletRequest request) {
		List<Admin> adminList = adminPage.getRecords();
		Page<AdminVO> adminVOPage = new Page<>(adminPage.getCurrent(), adminPage.getSize(), adminPage.getTotal());
		if (CollUtil.isEmpty(adminList)) {
			return adminVOPage;
		}
		// 对象列表 => 封装对象列表
		List<AdminVO> adminVOList = adminList.stream()
				.map(AdminVO::objToVo)
				.collect(Collectors.toList());
		adminVOPage.setRecords(adminVOList);
		return adminVOPage;
	}
	
}




