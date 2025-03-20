package com.henu.registration.captcha.service;

import com.henu.registration.captcha.model.entity.Captcha;

import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

/**
 * 验证码服务接口
 *
 * @author stephenqiu
 */
public interface CaptchaService {
	
	/**
	 * 生成验证码Base64字符串
	 *
	 * @return CaptchaCreate
	 */
	Captcha createCaptchaBase64();
	
	/**
	 * 生成验证码图片
	 *
	 * @param response response
	 */
	void createCaptchaImage(HttpServletResponse response);
	
	/**
	 * 生成验证码数据
	 *
	 * @return CaptchaData
	 */
	Captcha generateCaptchaData();
	
	/**
	 * 将 BufferedImage 转换为 Base64
	 *
	 * @param image image
	 * @return String
	 */
	String imageToBase64(BufferedImage image);
	
	/**
	 * 校验验证码
	 *
	 * @param captcha captcha
	 */
	void validateCaptcha(Captcha captcha);
}