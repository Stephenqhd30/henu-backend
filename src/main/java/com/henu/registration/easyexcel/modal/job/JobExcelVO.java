package com.henu.registration.easyexcel.modal.job;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.Job;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 岗位信息Excel视图
 *
 * @author stephen
 */
@Data
public class JobExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5441845198915192108L;
	
	/**
	 * 岗位名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("岗位名称")
	private String jobName;
	
	/**
	 * 岗位说明
	 */
	@ColumnWidth(40)
	@ExcelProperty("岗位说明")
	private String jobExplanation;
	
	/**
	 * 截止日期
	 */
	@ColumnWidth(40)
	@ExcelProperty("截止日期")
	private String deadlineTime;
}
