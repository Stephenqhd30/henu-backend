package com.henu.registration.config.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.henu.registration.config.sms.condition.SmsCondition;
import com.henu.registration.config.sms.properties.SmsProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * Sms 客户端配置
 *
 * @author stephenqiu
 */
@Slf4j
@Conditional(SmsCondition.class)
@Component
public class SmsClientConfiguration {
	
	@Resource
	private SmsProperties smsProperties;
	
	/**
	 * 配置阿里云短信客户端
	 */
	@Bean
	public IAcsClient smsClient() {
		DefaultProfile profile = DefaultProfile.getProfile(
				smsProperties.getRegionId(),
				smsProperties.getAccessKeyId(),
				smsProperties.getAccessKeySecret());
		return new DefaultAcsClient(profile);
	}
	
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
}