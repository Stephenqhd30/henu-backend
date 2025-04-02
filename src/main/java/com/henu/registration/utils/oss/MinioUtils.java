package com.henu.registration.utils.oss;

import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.bean.SpringContextHolder;
import com.henu.registration.manager.oss.MinioManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Stack;

/**
 * MinIO工具类
 *
 * @author stephenqiu
 */
@Slf4j
public class MinioUtils {
	
	/**
	 * 被封装的MinIO对象
	 */
	private static final MinioManager MINIO_MANAGER = SpringContextHolder.getBean(MinioManager.class);
	
	/**
	 * 上传文件
	 *
	 * @param file     上传的文件数据
	 * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
	 * @return {@link String}
	 */
	public static String uploadFile(MultipartFile file, String rootPath) {
		try {
			return MINIO_MANAGER.uploadToMinio(file, rootPath);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
		}
	}
	
	/**
	 * 删除文件
	 *
	 * @param url url
	 */
	public static void deleteInMinioByUrl(String url) {
		MINIO_MANAGER.deleteInMinioByUrl(url);
	}
	
}