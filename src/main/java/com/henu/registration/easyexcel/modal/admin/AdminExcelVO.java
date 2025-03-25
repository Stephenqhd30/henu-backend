package com.henu.registration.easyexcel.modal.admin;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.Admin;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 管理员 Excel 封装类
 *
 * @author stephen
 */
@Data
public class AdminExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -475210145722229525L;
	/**
	 * 管理员编号
	 */
	@ColumnWidth(40)
	@ExcelProperty("管理员编号")
	private String adminNumber;
	
	/**
	 * 管理员姓名
	 */
	@ColumnWidth(40)
	@ExcelProperty("管理员姓名")
	private String adminName;
	
	/**
	 * 管理员类型
	 */
	@ColumnWidth(40)
	@ExcelProperty("管理员类型")
	private String adminType;
}
