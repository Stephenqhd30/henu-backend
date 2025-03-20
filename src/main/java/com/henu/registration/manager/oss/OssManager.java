package com.henu.registration.manager.oss;


import com.alibaba.excel.util.StringUtils;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.config.oss.ali.condition.OssCondition;
import com.henu.registration.config.oss.ali.properties.OssProperties;
import com.henu.registration.constants.FileConstant;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.utils.encrypt.SHA3Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author: stephenqiu
 * @create: 2024-10-20 17:01
 **/
@Component
@Slf4j
@Conditional(OssCondition.class)
public class OssManager {
	@Resource
	private OssProperties ossProperties;
	
	@Resource
	private OSS ossClient;
	
	
	/**
	 * 上传文件到 OSS
	 *
	 * @param file 待上传的文件
	 * @param path 上传的路径
	 * @return 文件在 OSS 的 URL
	 */
	@Transactional(rollbackFor = Exception.class)
	public String uploadToOss(MultipartFile file, String path) throws IOException {
		ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件为空");
		// 获取文件的原始名称和后缀
		String originalName = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(originalName);
		long fileSize = file.getSize();
		
		// 生成唯一键
		String uniqueKey = SHA3Utils.encrypt(Arrays.toString(file.getBytes()) + originalName + suffix);
		String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
		String filePath = (StringUtils.isBlank(path) ? "" : path + "/") + fileName;
		
		try (InputStream inputStream = file.getInputStream()) {
			// 上传到 OSS
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(fileSize);
			PutObjectRequest putRequest = new PutObjectRequest(ossProperties.getBucketName(), filePath, inputStream, metadata);
			ossClient.putObject(putRequest);
		} catch (IOException | OSSException e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传失败" + e.getMessage());
		}
		
		return FileConstant.OSS_HOST + filePath;
	}
	
	/**
	 * 通过文件的url从OSS中删除文件
	 *
	 * @param url 文件URL
	 */
	private void deleteInOssByUrl(String url) {
		ThrowUtils.throwIf(org.apache.commons.lang3.StringUtils.isEmpty(url), ErrorCode.PARAMS_ERROR, "被删除地址为空");
		String bucket = ossProperties.getBucketName();
		String[] split = url.split(bucket);
		if (split.length != 2) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "URL格式错误");
		}
		// 移除前导斜杠
		String key = split[1].startsWith("/") ? split[1].substring(1) : split[1];
		try {
			ossClient.deleteObject(bucket, key);
		} catch (ClientException e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败: " + e.getMessage());
		}
	}
}
