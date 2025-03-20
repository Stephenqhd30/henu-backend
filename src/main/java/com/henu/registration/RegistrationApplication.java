package com.henu.registration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 主类（项目启动入口）
 *
 * @author stephenqiu
 */
@SpringBootApplication
@MapperScan("com.henu.registration.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class RegistrationApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RegistrationApplication.class, args);
	}
	
}
