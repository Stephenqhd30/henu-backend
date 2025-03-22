package com.henu.registration.model.dto.fileType;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建文件上传类型请求
 *
 * @author stephen qiu
 */
@Data
public class FileTypeAddRequest implements Serializable {
	
	/**
	 * 文件上传类型名称
	 */
	private String typeName;
	
	/**
	 * 文件上传类型值(JSON如['jpg','png'])
	 */
	private List<String> typeValues;
	
	/**
	 * 最大可上传文件大小（字节）
	 */
	private Long maxFileSize;
	
	private static final long serialVersionUID = 1L;
}