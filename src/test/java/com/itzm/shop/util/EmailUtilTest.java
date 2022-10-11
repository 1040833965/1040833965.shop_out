package com.itzm.shop.util;

import com.itzm.shop.utils.EmailUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-08 11:13:00
 */
@SpringBootTest
public class EmailUtilTest {


    @Autowired
    private JavaMailSender javaMailSender;

    @Test
    public void sent01(){
        EmailUtil.sendEmail(javaMailSender,"测试正文","3180139563@qq.com");
    }
}
