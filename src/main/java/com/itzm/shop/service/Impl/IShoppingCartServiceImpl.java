package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.ShoppingCart;
import com.itzm.shop.mapper.ShoppingCartMapper;
import com.itzm.shop.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-09 14:27:00
 */
@Slf4j
@Service
public class IShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
}
