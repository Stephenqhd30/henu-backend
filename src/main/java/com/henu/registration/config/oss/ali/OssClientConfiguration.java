package com.henu.registration.config.oss.ali;

import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.Protocol;
import com.henu.registration.config.oss.ali.condition.OssCondition;
import com.henu.registration.config.oss.ali.properties.OssProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 阿里云OSS配置属性
 *
 * @author stephenqiu
 */
@Data
@Configuration
@Conditional(OssCondition.class)
@Slf4j
public class OssClientConfiguration {
	
	@Resource
	private OssProperties ossProperties;
	
	/**
	 * 获取OSSClient客户端
	 *
	 * @return 返回OSSClient客户端
	 */
	@Bean("ossClientBean")
	public OSS ossClient() {
		CredentialsProvider credentialsProvider = new DefaultCredentialProvider(ossProperties.getSecretId(), ossProperties.getSecretKey());
		// 获取配置类中的域名
		// 创建ClientBuilderConfiguration。
		// ClientBuilderConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
		ClientBuilderConfiguration conf = new ClientBuilderConfiguration();
		conf.setProtocol(Protocol.HTTPS);
		return new OSSClientBuilder().build(ossProperties.getEndpoint(), credentialsProvider, conf);
	}
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
	
}