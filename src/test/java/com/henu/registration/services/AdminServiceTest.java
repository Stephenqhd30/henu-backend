package com.henu.registration.services;

import com.henu.registration.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: stephen qiu
 * @create: 2025-03-21 00:21
 **/
@SpringBootTest
public class AdminServiceTest {
	
	@Resource
	private AdminService adminService;
	
	@Test
	public void getEncryptPasswordTest() {
		String password = adminService.getEncryptPassword("12345678");
		System.out.println(password);
	}
	
	
}
