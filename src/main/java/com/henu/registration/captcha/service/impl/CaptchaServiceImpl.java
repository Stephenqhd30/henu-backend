package com.henu.registration.captcha.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.ReflectUtil;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.henu.registration.captcha.model.entity.Captcha;
import com.henu.registration.captcha.model.enums.CaptchaType;
import com.henu.registration.captcha.service.CaptchaService;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.bean.SpringContextHolder;
import com.henu.registration.config.captcha.properties.CaptchaProperties;
import com.henu.registration.utils.redisson.KeyPrefixConstants;
import com.henu.registration.utils.redisson.cache.CacheUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类（基于 Redis 缓存）
 * @author stephenqiu
 */
@Service
public class CaptchaServiceImpl implements CaptchaService {
	
	@Resource
	private CaptchaProperties captchaProperties;
	
	@Resource
	private DefaultKaptcha defaultKaptcha;
	
	/**
	 * 生成验证码并返回 Base64
	 */
	@Override
	public Captcha createCaptchaBase64() {
		Captcha captcha = this.generateCaptchaData();
		String redisKey = KeyPrefixConstants.CAPTCHA_PREFIX + captcha.getCode().toLowerCase();
		CacheUtils.putWithExpiration(redisKey, captcha.getCode(), TimeUnit.SECONDS.toMillis(captchaProperties.getExpired()));
		Captcha newCaptcha = new Captcha();
		newCaptcha.setCode(captcha.getCode());
		newCaptcha.setImageBuffer(captcha.getImageBuffer());
		return newCaptcha;
	}
	
	/**
	 * 生成验证码并直接返回图片
	 *
	 * @param response response
	 */
	@Override
	public void createCaptchaImage(HttpServletResponse response) {
		
		Captcha captcha = this.generateCaptchaData();
		String redisKey = KeyPrefixConstants.CAPTCHA_PREFIX + captcha.getCode().toLowerCase();
		CacheUtils.putWithExpiration(redisKey, captcha.getCode(), TimeUnit.SECONDS.toMillis(captchaProperties.getExpired()));
		
		try {
			ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
			ServletOutputStream responseOutputStream = response.getOutputStream();
			ImageIO.write(captcha.getImageBuffer(), "jpg", jpegOutputStream);
			byte[] captchaAsJpeg = jpegOutputStream.toByteArray();
			
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpeg");
			responseOutputStream.write(captchaAsJpeg);
			responseOutputStream.flush();
			
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码图片生成失败: " + e.getMessage());
		}
	}
	
	/**
	 * 生成验证码数据
	 */
	@Override
	public Captcha generateCaptchaData() {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		String code = null;
		BufferedImage image = null;
		
		if (captchaProperties.getType() == CaptchaType.MATH) {
			CodeGenerator codeGenerator = ReflectUtil.newInstance(CaptchaType.MATH.getClazz(), captchaProperties.getNumberLength());
			AbstractCaptcha captcha = SpringContextHolder.getBean(captchaProperties.getCategory().getClazz());
			captcha.setGenerator(codeGenerator);
			captcha.createCode();
			code = captcha.getCode();
			image = captcha.getImage();
		} else {
			code = defaultKaptcha.createText();
			image = defaultKaptcha.createImage(code);
		}
		Captcha captcha = new Captcha();
		captcha.setCode(code);
		captcha.setUuid(uuid);
		captcha.setImageBuffer(image);
		captcha.setImage(imageToBase64(image));
		return captcha;
	}
	
	/**
	 * 将 BufferedImage 转换为 Base64
	 *
	 * @param image image
	 * @return String
	 */
	@Override
	public String imageToBase64(BufferedImage image) {
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			ImageIO.write(image, "jpg", outputStream);
			return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码图片转换失败: " + e.getMessage());
		}
	}
	
	
	/**
	 * 校验验证码
	 *
	 * @param captcha 用户提交的验证码
	 */
	@Override
	public void validateCaptcha(Captcha captcha) {
		String code = captcha.getCode();
		// 从缓存中获取验证码
		String redisKey = KeyPrefixConstants.CAPTCHA_PREFIX + code.toLowerCase();
		String cachedCaptcha = CacheUtils.get(redisKey);
		if (cachedCaptcha == null) {
			throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码已过期或无效！");
		}
		// 校验用户输入的验证码与缓存中的验证码是否匹配（忽略大小写）
		if (!cachedCaptcha.equalsIgnoreCase(code)) {
			throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码错误！");
		}
		// 校验通过后，删除缓存中的验证码
		CacheUtils.remove(redisKey);
	}
}