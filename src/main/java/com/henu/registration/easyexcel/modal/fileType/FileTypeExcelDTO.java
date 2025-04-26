package com.henu.registration.easyexcel.modal.fileType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传类型Excel视图DTO
 *
 * @author stephen
 */
@Data
public class FileTypeExcelDTO implements Serializable {
	
	@Serial
	private static final long serialVersionUID = -6462552847172165110L;
	
	/**
	 * 文件上传类型名称
	 */
	@ColumnWidth(40)
	@ExcelProperty("文件上传类型名称")
	private String typeName;
	
	/**
	 * 文件上传类型值(JSON如['jpg','png'])
	 */
	@ColumnWidth(40)
	@ExcelProperty("文件上传类型值")
	private String typeValues;
}
