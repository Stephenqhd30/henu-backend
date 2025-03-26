package com.henu.registration.easyexcel.modal.fileType;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 文件上传类型Excel视图
 *
 * @author stephen
 */
@Data
public class FileTypeExcelVO implements Serializable {
	
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
	
	/**
	 * 最大可上传文件大小（字节）
	 */
	@ColumnWidth(40)
	@ExcelProperty("最大可上传文件大小（字节）")
	private Long maxFileSize;
}
