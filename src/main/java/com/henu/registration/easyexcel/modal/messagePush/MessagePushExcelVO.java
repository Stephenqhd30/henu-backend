package com.henu.registration.easyexcel.modal.messagePush;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.MessagePush;
import com.henu.registration.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 消息推送Excel视图
 *
 * @author stephen
 */
@Data
public class MessagePushExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 2664132887873087037L;
	
	/**
	 * 用户姓名
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户姓名")
	private String userName;
	
	/**
	 * 消息通知id
	 */
	@ColumnWidth(40)
	@ExcelProperty("消息通知id")
	private Long registrationFormId;
	
	/**
	 * 推送方式(websocket/email/sms/other)
	 */
	@ColumnWidth(20)
	@ExcelProperty("推送方式")
	private String pushType;
	
	/**
	 * 推送状态(0-未推送,1-成功,2-失败,3-重试中)
	 */
	@ColumnWidth(20)
	@ExcelProperty("推送状态")
	private String pushStatus;
	
	/**
	 * 推送消息内容
	 */
	@ColumnWidth(40)
	@ExcelProperty("推送消息内容")
	private String pushMessage;
	
	/**
	 * 失败重试次数
	 */
	@ColumnWidth(40)
	@ExcelProperty("失败重试次数")
	private Integer retryCount;
	
	/**
	 * 失败原因
	 */
	@ColumnWidth(40)
	@ExcelProperty("失败原因")
	private String errorMessage;
}
