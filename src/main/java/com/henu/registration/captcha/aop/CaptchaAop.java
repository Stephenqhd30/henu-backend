package com.henu.registration.captcha.aop;


import com.henu.registration.common.ErrorCode;
import com.henu.registration.common.exception.BusinessException;
import com.henu.registration.config.captcha.condition.CaptchaCondition;
import com.henu.registration.captcha.model.entity.Captcha;
import com.henu.registration.captcha.service.CaptchaService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * 处理需要验证码接口的切面类
 *
 * @author stephenqiu
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Conditional(CaptchaCondition.class)
public class CaptchaAop {

    @Resource
    private CaptchaService captchaService;

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(com.henu.registration.captcha.annotation.EnableCaptcha)")
    private void pointCutMethod() {

    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()")
    public void doBefore(JoinPoint joinPoint) {
        String uuid = "";
        String code = "";
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Class<?> argClass = arg.getClass();
            try {
                Field captchaField = argClass.getDeclaredField("captcha");
                captchaField.setAccessible(true);
                Captcha captcha = (Captcha) captchaField.get(arg);
                uuid = captcha.getUuid();
                code = captcha.getCode();
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "缺少验证码");
            }
        }
        Captcha captcha = new Captcha();
        captcha.setUuid(uuid);
        captcha.setCode(code);
        
        captchaService.validateCaptcha(captcha);
    }

}