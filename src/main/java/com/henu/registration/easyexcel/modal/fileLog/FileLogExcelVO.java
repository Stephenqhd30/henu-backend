package com.henu.registration.easyexcel.modal.fileLog;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传日志Excel视图
 *
 * @author stephen
 */
@Data
public class FileLogExcelVO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -5502227404138966496L;
	
	/**
	 * 附件类型
	 */
	@ColumnWidth(40)
	@ExcelProperty("附件类型")
	private String fileTypeName;
	
	/**
	 * 附件名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("附件名称")
	private String fileName;
	
	/**
	 * 附件存储路径
	 */
	@ColumnWidth(40)
	@ExcelProperty("附件存储路径")
	private String filePath;
	
	/**
	 * 用户名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("用户名称")
	private String userName;
}
