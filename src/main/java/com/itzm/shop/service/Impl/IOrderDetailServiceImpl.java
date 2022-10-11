package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.OrderDetail;
import com.itzm.shop.mapper.OrderDetailMapper;
import com.itzm.shop.service.IOrderDetailService;
import com.itzm.shop.service.IOrdersService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

/**
 * @author : 张金铭
 * @description :订单细节 表
 * @create :2022-10-10 09:22:00
 */
@Service
public class IOrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper,OrderDetail> implements IOrderDetailService {
}
