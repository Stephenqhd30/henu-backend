package com.henu.registration.easyexcel.modal.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.henu.registration.model.entity.Admin;
import com.henu.registration.model.vo.admin.AdminVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 管理员 Excel 封装类
 *
 * @author stephen
 */
@Data
public class AdminExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -475210145722229525L;
	
	/**
	 * 管理员编号
	 */
	@ExcelProperty("管理员编号")
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	@ExcelProperty("管理员姓名")
	private String adminName;
	
	/**
	 * 管理员密码
	 */
	@ExcelProperty("管理员密码")
	private String adminPassword;
	
	/**
	 * 封装类转对象
	 *
	 * @param adminExcelDTO adminExcelDTO
	 * @return {@link Admin}
	 */
	public static Admin dtoToObj(AdminExcelDTO adminExcelDTO) {
		if (adminExcelDTO == null) {
			return null;
		}
		Admin admin = new Admin();
		BeanUtils.copyProperties(adminExcelDTO, admin);
		return admin;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param admin admin
	 * @return {@link AdminVO}
	 */
	public static AdminExcelDTO objToDto(Admin admin) {
		if (admin == null) {
			return null;
		}
		AdminExcelDTO adminExcelDTO = new AdminExcelDTO();
		BeanUtils.copyProperties(admin, adminExcelDTO);
		return adminExcelDTO;
	}
	
	
}
