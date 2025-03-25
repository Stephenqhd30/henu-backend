package com.henu.registration.easyexcel.modal.operationLog;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.henu.registration.model.entity.OperationLog;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志表视图
 *
 * @author stephen
 */
@Data
public class OperationLogExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 5766091595794163615L;
	/**
	 * id
	 */
	@ColumnWidth(40)
	@ExcelProperty("id")
	private Long id;
	
	/**
	 * 请求唯一id
	 */
	@ColumnWidth(40)
	@ExcelProperty("请求唯一id")
	private String requestId;
	
	/**
	 * 请求路径
	 */
	@ColumnWidth(40)
	@ExcelProperty("请求路径")
	private String requestPath;
	
	/**
	 * 请求方法（GET, POST等）
	 */
	@ColumnWidth(40)
	@ExcelProperty("请求方法")
	private String requestMethod;
	
	/**
	 * 请求IP地址
	 */
	@ColumnWidth(40)
	@ExcelProperty("请求IP地址")
	private String requestIp;
	
	/**
	 * 请求参数
	 */
	@ColumnWidth(60)
	@ExcelProperty("请求参数")
	private String requestParams;
	
	/**
	 * 响应时间（毫秒）
	 */
	@ColumnWidth(40)
	@ExcelProperty("响应时间（毫秒）")
	private Long responseTime;
	
	/**
	 * 用户代理（浏览器信息）
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户代理（浏览器信息）")
	private String userAgent;
	
	/**
	 * 创建时间
	 */
	@ColumnWidth(40)
	@ExcelProperty("创建时间")
	private Date createTime;
	
}
