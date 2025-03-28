package com.henu.registration.easyexcel.modal.systemMessages;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 系统消息Excel视图
 *
 * @author stephen
 */
@Data
public class SystemMessagesExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -451875547520767350L;
	
	/**
	 * 通知标题
	 */
	@ColumnWidth(40)
	@ExcelProperty("通知标题")
	private String title;
	
	/**
	 * 消息内容
	 */
	@ColumnWidth(40)
	@ExcelProperty("消息内容")
	private String content;
	
	/**
	 * 推送时间
	 */
	@ColumnWidth(40)
	@ExcelProperty("推送时间")
	private Date pushTime;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	@ColumnWidth(40)
	@ExcelProperty("推送状态")
	private String pushStatus;
	
	/**
	 * 消息类型
	 */
	@ColumnWidth(40)
	@ExcelProperty("消息类型")
	private String type;
}
