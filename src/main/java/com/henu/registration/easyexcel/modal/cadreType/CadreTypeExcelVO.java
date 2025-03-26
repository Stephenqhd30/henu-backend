package com.henu.registration.easyexcel.modal.cadreType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 干部类型 Excel 视图
 *
 * @author stephen
 */
@Data
public class CadreTypeExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -797746281005285141L;
	
	/**
	 * 干部类型
	 */
	@ColumnWidth(40)
	@ExcelProperty("干部类型")
	private String type;
}
