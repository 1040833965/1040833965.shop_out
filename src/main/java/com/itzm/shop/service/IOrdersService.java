package com.itzm.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzm.shop.entity.Orders;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-10 09:15:00
 */
public interface IOrdersService extends IService<Orders> {
    /**
     * 下单功能
     * @param orders 订单信息
     */
    void submit(Orders orders);
}
