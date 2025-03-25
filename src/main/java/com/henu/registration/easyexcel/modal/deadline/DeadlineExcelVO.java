package com.henu.registration.easyexcel.modal.deadline;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.Deadline;
import com.henu.registration.model.vo.job.JobVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 截止时间视图
 *
 * @author stephenqiu
 */
@Data
public class DeadlineExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 7816078208695245470L;
	
	/**
	 * 截止日期
	 */
	@ColumnWidth(40)
	@ExcelProperty("截止日期")
	private Date deadlineTime;
	
	/**
	 * 岗位名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("岗位名称")
	private String jobName;
	
}
