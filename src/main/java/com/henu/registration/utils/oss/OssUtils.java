package com.henu.registration.utils.oss;

import com.henu.registration.config.bean.SpringContextHolder;
import com.henu.registration.manager.oss.OssManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 阿里云OSS工具类
 *
 * @author stephenqiu
 */
@Slf4j
public class OssUtils {
	
	/**
	 * 被封装的OSS对象
	 */
	private static final OssManager OSS_MANAGER = SpringContextHolder.getBean(OssManager.class);
	
	/**
	 * 上传文件
	 *
	 * @param file     上传的文件数据
	 * @param rootPath 文件根目录（注意不需要首尾斜杠，即如果保存文件到"/root/a/"文件夹中，只需要传入"root/a"字符串即可）
	 * @return {@link String}
	 */
	public static String uploadFile(MultipartFile file, String rootPath) throws IOException {
		return OSS_MANAGER.uploadToOss(file, rootPath);
	}
	
}