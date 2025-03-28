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
		// 判断cadreTypeVO是否为空
		if (schoolExcelDTO == null) {
			return null;
		}
		// 创建CadreType对象
		School school = new School();
		// 将cadreTypeVO中的属性值复制到cadreType中
		BeanUtils.copyProperties(schoolExcelDTO, school);
		// 返回CadreType对象
		return school;
	}
}
