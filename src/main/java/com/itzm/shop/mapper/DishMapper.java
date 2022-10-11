package com.itzm.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzm.shop.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:18:00
 */
@Mapper
public interface DishMapper extends BaseMapper<Dish> {

}
