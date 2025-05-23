package com.henu.registration.easyexcel.modal.school;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.CadreType;
import com.henu.registration.model.entity.School;
import com.henu.registration.model.vo.cadreType.CadreTypeVO;
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
public class SchoolExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5941319198049937390L;
	
	
	/**
	 * 高校名称
	 */
	@ExcelProperty("高校名称")
	private String schoolName;
	
	/**
	 * 封装类转对象
	 *
	 * @param schoolExcelDTO schoolExcelDTO
	 * @return {@link School}
	 */
	public static School dtoToObj(SchoolExcelDTO schoolExcelDTO) {
		if (schoolExcelDTO == null) {
			return null;
		}
		School school = new School();
		BeanUtils.copyProperties(schoolExcelDTO, school);
		return school;
	}
}
