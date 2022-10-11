package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.User;
import com.itzm.shop.mapper.UserMapper;
import com.itzm.shop.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * @author : 张金铭
 * @description :User服务层
 * @create :2022-10-08 14:07:00
 */
@Service
public class IUserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
}
