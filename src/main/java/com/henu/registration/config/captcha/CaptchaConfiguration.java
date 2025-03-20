package com.henu.registration.config.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.henu.registration.config.captcha.condition.CaptchaCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.util.Properties;

/**
 * 验证码配置
 *
 * @author stephenqiu
 */
@Configuration
@Conditional(CaptchaCondition.class)
@Slf4j
public class CaptchaConfiguration {
	
	/**
	 * 验证码图片宽度
	 */
	private static final int WIDTH = 160;
	
	/**
	 * 验证码图片高度
	 */
	private static final int HEIGHT = 60;
	
	/**
	 * 验证码图片背景颜色
	 */
	private static final Color BACKGROUND = Color.WHITE;
	
	/**
	 * 验证码字体
	 */
	private static final Font FONT = new Font("Arial", Font.BOLD, 48);
	
	@Bean
	public DefaultKaptcha getDefaultKaptcha() {
		com.google.code.kaptcha.impl.DefaultKaptcha defaultKaptcha = new com.google.code.kaptcha.impl.DefaultKaptcha();
		Properties properties = new Properties();
		// 图片边框
		properties.setProperty("kaptcha.border", "yes");
		// 边框颜色
		properties.setProperty("kaptcha.border.color", "105,179,90");
		// 字体颜色
		properties.setProperty("kaptcha.textproducer.font.color", "red");
		// 图片宽
		properties.setProperty("kaptcha.image.width", "135");
		// 图片高
		properties.setProperty("kaptcha.image.height", "50");
		// 使用哪些字符生成验证码
		properties.setProperty("kaptcha.textproducer.char.string", "ACEHKTW247");
		// 字体大小
		properties.setProperty("kaptcha.textproducer.font.size", "43");
		// session key
		properties.setProperty("kaptcha.session.key", "code");
		// 验证码长度
		properties.setProperty("kaptcha.textproducer.char.length", "4");
		// 字体
		properties.setProperty("kaptcha.textproducer.font.names", "Arial");
		// 干扰线颜色
		properties.setProperty("kaptcha.noise.color", "red");
		
		Config config = new Config(properties);
		defaultKaptcha.setConfig(config);
		
		return defaultKaptcha;
	}
	
	/**
	 * 圆圈干扰的字符验证码
	 */
	@Lazy
	@Bean
	public CircleCaptcha circleCharCaptcha() {
		CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(WIDTH, HEIGHT);
		captcha.setBackground(BACKGROUND);
		captcha.setFont(FONT);
		return captcha;
	}
	
	/**
	 * 线段干扰的字符验证码
	 */
	@Lazy
	@Bean
	public LineCaptcha lineCharCaptcha() {
		LineCaptcha captcha = CaptchaUtil.createLineCaptcha(WIDTH, HEIGHT);
		captcha.setBackground(BACKGROUND);
		captcha.setFont(FONT);
		return captcha;
	}
	
	/**
	 * 扭曲干扰的字符验证码
	 */
	@Lazy
	@Bean
	public ShearCaptcha shearCharCaptcha() {
		ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(WIDTH, HEIGHT);
		captcha.setBackground(BACKGROUND);
		captcha.setFont(FONT);
		return captcha;
	}
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
	
}