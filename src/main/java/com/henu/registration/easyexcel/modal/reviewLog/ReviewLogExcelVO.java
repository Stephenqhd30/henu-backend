package com.henu.registration.easyexcel.modal.reviewLog;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 审核记录视图
 *
 * @author stephen
 */
@Data
public class ReviewLogExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 8741670101284551406L;
	
	/**
	 * 报名登记表id
	 */
	@ColumnWidth(40)
	@ExcelProperty("报名登记表id")
	private Long registrationId;
	
	/**
	 * 审核人
	 */
	@ColumnWidth(20)
	@ExcelProperty("审核人")
	private String reviewer;
	
	/**
	 * 审核状态(0-待审核,1-审核通过,2-审核不通过)
	 */
	@ColumnWidth(20)
	@ExcelProperty("审核状态")
	private String reviewStatus;
	
	/**
	 * 审核意见
	 */
	@ColumnWidth(40)
	@ExcelProperty("审核意见")
	private String reviewComments;
	
	/**
	 * 审核时间
	 */
	@ColumnWidth(40)
	@ExcelProperty("审核时间")
	private Date reviewTime;
}
