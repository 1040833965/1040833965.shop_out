package com.itzm.shop.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author : 张金名
 * @description : 发送邮件工具类
 * @create :2022-10-08 11:07:00
 */
public class EmailUtil {
    private static final String FROM = "1040833965@qq.com";
    private final static String SUB = "验证码：";



    /**
     * 发送邮件工具类方法
     * @param javaMailSender javamail的实体类
     * @param context 发送邮件的正文  可以是验证码
     * @param to 邮件接收者
     */
    public static void sendEmail(JavaMailSender javaMailSender,String context,String to){
        SimpleMailMessage message = new SimpleMailMessage();//简单邮件消息
        message.setFrom(FROM+"(RJ外卖)");
        message.setTo(to);
        message.setSubject("您的 RJ外卖 "+SUB);
        message.setText(to+",您好! ,您本次登入验证码为"+ context+" ,请妥善保管！");
        javaMailSender.send(message);
    }
}
