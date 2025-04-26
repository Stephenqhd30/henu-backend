package com.henu.registration.easyexcel.modal.cadreType;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 干部类型 Excel 视图
 *
 * @author stephen
 */
@Data
public class CadreTypeExcelDTO implements Serializable {
	
	
	@Serial
	private static final long serialVersionUID = -1539554853294209727L;
	/**
	 * 干部类型
	 */
	@ExcelProperty("干部类型")
	private String type;
}
