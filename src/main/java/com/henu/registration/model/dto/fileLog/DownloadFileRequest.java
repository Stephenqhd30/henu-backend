package com.henu.registration.model.dto.fileLog;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 文件上传请求
 *
 * @author stephenqiu
 */
@Data
public class DownloadFileRequest implements Serializable {
	
	
	@Serial
	private static final long serialVersionUID = -5417751365400980964L;
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 用户id列表
	 */
	private List<Long> userIds;
}