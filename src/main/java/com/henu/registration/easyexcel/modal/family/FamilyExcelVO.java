package com.henu.registration.easyexcel.modal.family;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 家庭关系Excel视图
 *
 * @author stephen
 */
@Data
public class FamilyExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -1764919810208416171L;
	
	/**
	 * 称谓
	 */
	@ColumnWidth(20)
	@ExcelProperty("称谓")
	private String appellation;
	
	/**
	 * 姓名
	 */
	@ColumnWidth(20)
	@ExcelProperty("姓名")
	private String familyName;
	
	/**
	 * 工作单位及职务
	 */
	@ColumnWidth(40)
	@ExcelProperty("工作单位及职务")
	private String workDetail;
	
	/**
	 * 用户姓名
	 */
	@ColumnWidth(20)
	@ExcelProperty("用户姓名")
	private String userName;
}
