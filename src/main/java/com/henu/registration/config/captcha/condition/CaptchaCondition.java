package com.henu.registration.config.captcha.condition;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 验证码自定义配置条件
 *
 * @author stephenqiu
 */
public class CaptchaCondition implements Condition {
	
	@Override
	public boolean matches(ConditionContext context, @NotNull AnnotatedTypeMetadata metadata) {
		String captchaProperty = context.getEnvironment().getProperty("captcha.enable");
		return StringUtils.equals(Boolean.TRUE.toString(), captchaProperty);
	}
	
}