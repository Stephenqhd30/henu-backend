package com.henu.registration.model.dto.fileLog;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author stephenqiu
 */
@Data
public class UploadFileRequest implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 6149704783947487687L;
	/**
	 * 业务
	 */
	private String biz;
}