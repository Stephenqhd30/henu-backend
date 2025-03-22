package com.henu.registration.services;

import com.henu.registration.service.AdminService;
import com.henu.registration.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author: stephen qiu
 * @create: 2025-03-21 00:21
 **/
@SpringBootTest
public class UserServiceTest {
	
	@Resource
	private UserService userService;
	
	@Test
	public void getEncryptUserIdCardTest() {
		String password = userService.getEncryptIdCard("");
		System.out.println(password);
	}
	
	@Test
	public void getDecryptUserIdCardTest() {
		String password = userService.getDecryptIdCard("e0b2089d54fab95e9e08cc8391cc04b4");
		System.out.println(password);
	}
	
	
}
