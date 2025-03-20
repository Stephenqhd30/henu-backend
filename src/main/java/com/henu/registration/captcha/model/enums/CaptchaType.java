package com.henu.registration.captcha.model.enums;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import com.henu.registration.captcha.manager.UnsignedMathManager;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 验证码类型
 *
 * @author stephenqiu
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {
	
	/**
	 * 数字
	 */
	MATH(UnsignedMathManager.class),
	
	/**
	 * 字符
	 */
	CHAR(RandomGenerator.class);
	
	private final Class<? extends CodeGenerator> clazz;
	
}