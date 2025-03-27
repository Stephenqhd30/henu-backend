package com.henu.registration.config.sms.condition;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 短信配置条件
 *
 * @author stephenqiu
 */
public class SmsCondition implements Condition {
	
	@Override
	public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
		String property = context.getEnvironment().getProperty("aliyun.sms.enabled");
		return StringUtils.equals(Boolean.TRUE.toString(), property);
	}
	
}