package com.henu.registration.easyexcel.modal.schoolSchoolType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 高校与高校类型关联信息Excel视图
 *
 * @author stephen
 */
@Data
public class SchoolSchoolTypeExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 2241706733027810025L;
	
	/**
	 * 高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("高校名称")
	private String schoolName;
	
	/**
	 * 高校类别列表(JSON存储)
	 */
	@ColumnWidth(40)
	@ExcelProperty("高校类别列表")
	private String schoolTypes;
}
