package com.henu.registration.easyexcel.modal.schoolSchoolType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.entity.SchoolSchoolType;
import com.henu.registration.model.entity.SchoolType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 高校与高校类型关联信息Excel视图DTO
 *
 * @author stephen
 */
@Data
public class SchoolSchoolTypeExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5941319198049937390L;
	
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
	
	
	/**
	 * 封装类转对象
	 *
	 * @param schoolExcelDTO schoolExcelDTO
	 * @return {@link School}
	 */
	public static SchoolSchoolType dtoToObj(SchoolSchoolTypeExcelDTO schoolExcelDTO) {
		// 判断cadreTypeVO是否为空
		if (schoolExcelDTO == null) {
			return null;
		}
		SchoolSchoolType schoolSchoolType = new SchoolSchoolType();
		BeanUtils.copyProperties(schoolExcelDTO, schoolSchoolType);
		return schoolSchoolType;
	}
}
