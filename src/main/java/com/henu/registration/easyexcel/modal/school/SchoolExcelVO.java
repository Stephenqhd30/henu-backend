package com.henu.registration.easyexcel.modal.school;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 高校信息视图
 *
 * @author stephen
 */
@Data
public class SchoolExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5941319198049937390L;

	
	/**
	 * 高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("高校名称")
	private String schoolName;
}
