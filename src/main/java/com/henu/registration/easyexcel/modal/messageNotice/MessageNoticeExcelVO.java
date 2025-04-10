package com.henu.registration.easyexcel.modal.messageNotice;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息通知Excel视图
 *
 * @author stephen
 */
@Data
public class MessageNoticeExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -2054040827014586206L;
	
	/**
	 * 用户姓名
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户姓名")
	private String userName;
	
	/**
	 * 联系电话
	 */
	@ColumnWidth(40)
	@ExcelProperty("联系电话")
	private String userPhone;
	
	/**
	 * 面试内容
	 */
	@ColumnWidth(60)
	@ExcelProperty("面试内容")
	private String content;
	
	/**
	 * 报名登记表id
	 */
	@ColumnWidth(40)
	@ExcelProperty("报名登记表id")
	private Long registrationId;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	@ColumnWidth(40)
	@ExcelProperty("推送状态")
	private String pushStatus;

}
