package com.henu.registration.model.vo.admin;

import com.henu.registration.model.entity.Admin;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理员视图
 *
 * @author stephen
 */
@Data
public class LoginAdminVO implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 管理员编号
	 */
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	private String adminName;
	
	/**
	 * 管理员类型
	 */
	private String adminType;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * token
	 */
	private String token;
	
	
	/**
	 * 封装类转对象
	 *
	 * @param adminVO adminVO
	 * @return {@link Admin}
	 */
	public static Admin voToObj(LoginAdminVO adminVO) {
		if (adminVO == null) {
			return null;
		}
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminVO, admin);
		return admin;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param admin admin
	 * @return {@link LoginAdminVO}
	 */
	public static LoginAdminVO objToVo(Admin admin) {
		if (admin == null) {
			return null;
		}
		LoginAdminVO adminVO = new LoginAdminVO();
		BeanUtils.copyProperties(admin, adminVO);
		return adminVO;
	}
}
