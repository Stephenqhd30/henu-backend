package com.henu.registration;

import com.henu.registration.config.wx.WxOpenConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


/**
 * 主类测试
 *
 * @author stephenqiu
 * 
 */
@SpringBootTest
class RegistrationApplicationTests {

    @Resource
    private WxOpenConfiguration wxOpenConfiguration;

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfiguration);
    }

}
