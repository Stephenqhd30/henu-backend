package com.henu.registration.model.dto.fileLog;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询文件上传日志表请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileLogQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 搜索词
	 */
	private String searchText;
	
	/**
	 * 附件类型id
	 */
	private Long fileTypeId;
	
	/**
	 * 附件名称
	 */
	private String fileName;
	
	/**
	 * 附件存储路径
	 */
	private String filePath;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	private static final long serialVersionUID = 1L;
}