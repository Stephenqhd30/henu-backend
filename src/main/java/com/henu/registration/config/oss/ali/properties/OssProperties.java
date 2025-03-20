package com.henu.registration.config.oss.ali.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: stephenqiu
 * @create: 2024-11-07 12:58
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "oss.ali")
public class OssProperties {
	
	/**
	 * 是否开启阿里云对象存储客户端功能
	 */
	private Boolean enable = false;
	
	/**
	 * 域名
	 */
	private String endpoint;
	
	/**
	 * 密钥ID
	 */
	private String secretId;
	
	/**
	 * 密钥KEY
	 */
	private String secretKey;
	
	/**
	 * 桶名称
	 */
	private String bucketName;
}
