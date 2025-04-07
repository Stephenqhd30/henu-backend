package com.henu.registration.manager.oss;

import cn.hutool.core.io.resource.InputStreamResource;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ThrowUtils;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.oss.minio.condition.MinioCondition;
import com.henu.registration.config.oss.minio.properties.MinioProperties;
import com.henu.registration.constants.FileConstant;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * MinIO配置
 *
 * @author stephenqiu
 */
@Component
@Slf4j
@Conditional(MinioCondition.class)
public class MinioManager {
	
	@Resource
	private MinioProperties minioProperties;
	
	@Resource
	private MinioClient minioClient;
	
	/**
	 * 上传文件到 MinIO
	 *
	 * @param file 待上传的文件
	 * @param path 上传的路径
	 * @return {@link String}
	 */
	@Transactional(rollbackFor = Exception.class)
	public String uploadToMinio(MultipartFile file, String path) throws IOException {
		ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件为空");
		
		// 获取文件的原始名称和后缀
		String originalName = StringUtils.defaultIfBlank(file.getOriginalFilename(), file.getName());
		String suffix = FilenameUtils.getExtension(originalName);
		
		// 生成唯一键
		String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
		String filePath = String.format("%s/%s", path, fileName);
		
		try (InputStream inputStream = file.getInputStream()) {
			// 读取文件内容
			byte[] dataBytes = inputStream.readAllBytes();
			String key = StringUtils.isBlank(filePath) ? fileName : filePath;
			
			// 上传文件到 MinIO
			minioClient.putObject(PutObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key)
					.stream(new ByteArrayInputStream(dataBytes), dataBytes.length, -1)
					.build());
			
		} catch (Exception e) {
			log.error("文件上传失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
		}
		
		return FileConstant.MINIO_HOST + filePath;
	}
	
	/**
	 * 从MinIO中彻底删除文件
	 *
	 * @param url 文件URL
	 */
	public void deleteInMinioByUrl(String url) {
		ThrowUtils.throwIf(StringUtils.isEmpty(url), ErrorCode.NOT_FOUND_ERROR, "被删除地址为空");
		String[] split = url.split(minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/");
		ThrowUtils.throwIf(split.length != 2, ErrorCode.NOT_FOUND_ERROR, "文件不存在");
		String key = split[1];
		try {
			minioClient.removeObject(RemoveObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key).build());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
	}
	
	/**
	 * 下载文件从 MinIO
	 *
	 * @param url 文件URL
	 * @return {@link ResponseEntity} 文件流
	 */
	public ResponseEntity<InputStreamResource> downloadFromMinio(String url) {
		ThrowUtils.throwIf(StringUtils.isEmpty(url), ErrorCode.NOT_FOUND_ERROR, "下载地址为空");
		// 提取文件路径（key）
		String[] split = url.split(minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/");
		ThrowUtils.throwIf(split.length != 2, ErrorCode.NOT_FOUND_ERROR, "文件不存在");
		String key = split[1];
		try {
			// 从 MinIO 获取文件的输入流
			InputStream inputStream = minioClient.getObject(GetObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key)
					.build());
			// 设置响应头（可根据需要修改文件名）
			String fileName = key.substring(key.lastIndexOf("/") + 1);
			// 使用 InputStreamResource 返回文件流
			return ResponseEntity.ok()
					.header("Content-Disposition", "attachment; filename=" + fileName)
					.body(new InputStreamResource(inputStream));
		} catch (Exception e) {
			log.error("文件下载失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件下载失败: " + e.getMessage());
		}
	}
	
	/**
	 * 获取文件流
	 *
	 * @param fileUrl 文件URL
	 * @return InputStream
	 */
	public InputStream getFileStream(String fileUrl) {
		String[] split = fileUrl.split(minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/");
		String key = split[1];
		try {
			return minioClient.getObject(GetObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key)
					.build());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件下载失败: " + e.getMessage());
		}
	}
}
