package com.henu.registration.config.captcha;

import com.henu.registration.captcha.aop.CaptchaInterceptor;
import com.henu.registration.config.captcha.condition.CaptchaCondition;
import com.henu.registration.config.captcha.properties.CaptchaProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证码拦截器Web配置类
 *
 * @author stephenqiu
 */
@EnableConfigurationProperties(CaptchaProperties.class)
@Configuration
@Conditional(CaptchaCondition.class)
public class CaptchaInterceptorConfiguration implements WebMvcConfigurer {
	
	@Resource
	private CaptchaProperties captchaProperties;
	
	/**
	 * 定义Captcha需要拦截的URI
	 */
	private static final List<String> CAPTCHA_NEED_INTERCEPT_URI = new ArrayList<>() {
		private static final long serialVersionUID = 8628497591238887468L;
		
		{
			add("/captcha");
		}
	};
	
	@Override
	public void addInterceptors(@NotNull InterceptorRegistry registry) {
		// 如果开启了验证码相关配置，则进行拦截
		if (captchaProperties.getEnable()) {
			registry.addInterceptor(new CaptchaInterceptor()).addPathPatterns(CAPTCHA_NEED_INTERCEPT_URI);
		}
	}
	
}