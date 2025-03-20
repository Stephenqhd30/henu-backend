package com.henu.registration.captcha.annotation;

import java.lang.annotation.*;

/**
 * 需要验证码的方法注解
 *
 * @author stephenqiu
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableCaptcha {

}