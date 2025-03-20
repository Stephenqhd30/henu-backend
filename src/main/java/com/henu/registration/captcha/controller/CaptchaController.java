package com.henu.registration.captcha.controller;

import com.henu.registration.captcha.model.entity.Captcha;
import com.henu.registration.captcha.service.CaptchaService;
import com.henu.registration.common.BaseResponse;
import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.ResultUtils;
import com.henu.registration.common.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 验证码控制器
 *
 * @author stephenqiu
 */
@RestController()
@RequestMapping("/captcha")
public class CaptchaController {
	
	@Resource
	private CaptchaService captchaService;
	
	/**
	 * 生成验证码并返回图片
	 *
	 * @param response HttpServletResponse 用于返回图片
	 */
	@GetMapping("/create")
	public void createCaptcha(HttpServletResponse response) {
		try {
			captchaService.createCaptchaImage(response);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.CAPTCHA_ERROR, "验证码生成失败");
		}
	}
	
	/**
	 * 获取验证码（返回 Base64 编码）
	 *
	 * @return 返回验证码信息，包括 UUID 和 Base64 编码的验证码图片
	 */
	@PostMapping("/captcha")
	public BaseResponse<Captcha> getCaptcha() {
		return ResultUtils.success(captchaService.createCaptchaBase64());
	}
	
	/**
	 * 校验验证码
	 *
	 * @param code 用户输入的验证码
	 * @return 校验结果
	 */
	@PostMapping("/check")
	public BaseResponse<Boolean> checkCaptchaCode(@RequestParam String code) {
		if (StringUtils.isEmpty(code)) {
			return ResultUtils.error(ErrorCode.CAPTCHA_ERROR, "验证码未生成或已过期");
		}
		// 创建一个 Captcha 对象，进行校验
		Captcha captcha = new Captcha();
		captcha.setCode(code);
		// 调用服务层进行验证码校验
		captchaService.validateCaptcha(captcha);
		return ResultUtils.success(true);
	}
	
	
}