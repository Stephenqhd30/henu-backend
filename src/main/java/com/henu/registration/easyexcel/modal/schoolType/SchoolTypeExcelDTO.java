package com.henu.registration.easyexcel.modal.schoolType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.SchoolType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 高校信息DTO
 *
 * @author stephen
 */
@Data
public class SchoolTypeExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5941319198049937390L;
	
	/**
	 * 高校类别名称
	 */
	@ColumnWidth(20)
	@ExcelProperty("高校类别名称")
	private String typeName;
	
	
	/**
	 * 封装类转对象
	 *
	 * @param schoolExcelDTO schoolExcelDTO
	 * @return {@link School}
	 */
	public static SchoolType dtoToObj(SchoolTypeExcelDTO schoolExcelDTO) {
		if (schoolExcelDTO == null) {
			return null;
		}
		SchoolType schoolType = new SchoolType();
		BeanUtils.copyProperties(schoolExcelDTO, schoolType);
		return schoolType;
	}
}
