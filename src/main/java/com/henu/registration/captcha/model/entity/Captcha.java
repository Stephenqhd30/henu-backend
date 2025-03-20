package com.henu.registration.captcha.model.entity;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 校验验证码参数实体类
 *
 * @author stephenqiu
 */
@Data
public class Captcha implements Serializable {
	
	/**
	 * 验证码
	 */
	private String code;
	
	/**
	 * 验证码UUID，用于组成Redis中的键
	 */
	private String uuid;
	
	/**
	 * 验证码图片
	 */
	private String image;
	
	/**
	 * 验证码图片的BufferedImage形式
	 */
	private BufferedImage imageBuffer;
	
	private static final long serialVersionUID = -6070139749121232739L;
	
}