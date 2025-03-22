package com.henu.registration.model.dto.fileType;

import com.henu.registration.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询文件上传类型请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FileTypeQueryRequest extends PageRequest implements Serializable {
	
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
	
	/**
	 * 管理员id
	 */
	private Long adminId;
	
	
	private static final long serialVersionUID = 1L;
}