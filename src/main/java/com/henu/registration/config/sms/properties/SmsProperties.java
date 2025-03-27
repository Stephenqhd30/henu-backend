package com.henu.registration.config.sms.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * redisson 配置
 *
 * @author: stephenqiu
 * @create: 2024-11-07 12:42
 **/
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.sms")
public class SmsProperties {
	
	/**
	 * 是否启用
	 */
	private Boolean enable = false;
	
	/**
	 * 阿里云短信accessKeyId
	 */
	private String accessKeyId;
	
	/**
	 * 阿里云短信accessKeySecret
	 */
	private String accessKeySecret;
	
	/**
	 * 阿里云短信regionId
	 */
	private String regionId;
	
}
