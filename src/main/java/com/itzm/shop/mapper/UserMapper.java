package com.itzm.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzm.shop.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : 张金铭
 * @description : 用户表dao层
 * @create :2022-10-08 14:05:00
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
