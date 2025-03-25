package com.henu.registration.easyexcel.modal.education;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 教育经历表视图
 *
 * @author stephen
 */
@Data
public class EducationExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 243184720008858972L;
	
	/**
	 * 高校名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("高校名称")
	private String schoolName;
	
	/**
	 * 用户姓名
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户姓名")
	private String userName;
	
	/**
	 * 教育阶段
	 */
	@ColumnWidth(40)
	@ExcelProperty("教育阶段")
	private String educationalStage;
	
	/**
	 * 专业
	 */
	@ColumnWidth(40)
	@ExcelProperty("专业")
	private String major;
	
	/**
	 * 学习起止年月
	 */
	@ColumnWidth(40)
	@ExcelProperty("学习起止年月")
	private String studyTime;
	
	/**
	 * 证明人
	 */
	@ColumnWidth(40)
	@ExcelProperty("证明人")
	private String certifier;
	
	/**
	 * 证明人联系电话
	 */
	@ColumnWidth(40)
	@ExcelProperty("证明人联系电话")
	private String certifierPhone;
	
}
