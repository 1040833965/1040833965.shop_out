package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.entity.User;
import com.itzm.shop.service.IUserService;
import com.itzm.shop.utils.EmailUtil;
import com.itzm.shop.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : 张金铭
 * @description :用户控制层
 * @create :2022-10-08 14:10:00
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private RedisTemplate redisTemplate;


    /**
     * 发送验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public JsonResult<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取到邮箱号
        String phone =user.getPhone();
        if (!StringUtils.isNotEmpty(phone)){
            return JsonResult.error("验证码发送失败，请检查邮箱");
        }
        //生成随机四位验证码
        String code = ValidateCodeUtils.generateValidateCode(4).toString();


        //调用邮箱发送的工具类完成验证码的发送，测试阶段网络不行暂时不用
        EmailUtil.sendEmail(javaMailSender,code,phone);
        log.info("验证码为： {}",code);
        //验证码存入session
//        session.setAttribute(phone,code);
        //将验证码存入redis中,设置过期时间为5分钟
        redisTemplate.opsForValue().set(phone,code,3, TimeUnit.MINUTES);


        return JsonResult.success("验证码发送成功，请注意接收");
    }

    @PostMapping("/login")
    public JsonResult<User> login(@RequestBody Map map,HttpSession session){
//        log.info("map ，{}" ,map);
        //获取手机号

        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取保存的验证码进行比对
//        Object codeInSession = session.getAttribute(phone);
        //从redis中获取验证码
        Object codeInSession = redisTemplate.opsForValue().get(phone);

        if (!(codeInSession!=null&&codeInSession.equals(code))){
            //不一致，登入失败
            return JsonResult.error("验证码错误");
        }
        //一致，登入成功
        //判断当前用户是否是新用户，新用户就自动完成注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if (user==null){
            //用户为新用户，自动完成注册
            user = new User();
            user.setPhone(phone);
            //可以给用户添加一个默认用户名
            String name ="RJ--"+ValidateCodeUtils.generateValidateCode4String(6);
            user.setName(name);
            userService.save(user);
        }
        User userOver = userService.getOne(queryWrapper);
        //将员工id存入Session并返回登录成功结果
        session.setAttribute("user",userOver.getId());
        //用户登入成功，删除验证码
        redisTemplate.delete(phone);
        return JsonResult.success(user);
    }
}
