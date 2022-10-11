package com.itzm.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzm.shop.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : 张金铭
 * @description :订单的dao层
 * @create :2022-10-10 09:14:00
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
