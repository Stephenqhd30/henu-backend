package com.henu.registration.easyexcel.modal.schoolType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.SchoolType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 高校类型视图
 *
 * @author stephen
 */
@Data
public class SchoolTypeExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 5432946341434796121L;
	
	
	/**
	 * 高校类别名称
	 */
	@ColumnWidth(20)
	@ExcelProperty("高校类别名称")
	private String typeName;
}
