package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.common.BaseContext;
import com.itzm.shop.entity.*;
import com.itzm.shop.mapper.OrdersMapper;
import com.itzm.shop.service.*;
import com.itzm.shop.service.ex.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description :订单表服务层
 * @create :2022-10-10 09:16:00
 */
@Service
public class IOrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {

    @Resource
    private IShoppingCartService cartService;

    @Resource
    private IOrderDetailService orderDetailService;

    @Resource
    private IUserService userService;

    @Resource
    private IAddressBookService addressService;


    /**
     * 下单功能，订单信息
     * @param orders 订单信息
     */
    @Transactional
    @Override
    public void submit(Orders orders) {
        //获取当前uid
        Long uid = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId,uid);
        List<ShoppingCart> list = cartService.list(cartLambdaQueryWrapper);
        if (list == null ||list.size()==0){
            throw new CustomException("购物车为空不能下单");
        }
        //查询用户信息
        User user = userService.getById(uid);
        //查询地址信息
        AddressBook address = addressService.getById(orders.getAddressBookId());
        if (address==null){
            throw new CustomException("地址信息有错误");
        }

        //将需要的数据取出来存入订单实体类中

        long orderId = IdWorker.getId();//订单号

        //计算总金额
        AtomicInteger amount = new AtomicInteger(0);

        //将购物车集合中的数据封装到订单详情集合中
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderId);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());

        //向订单类中填充数据
        orders.setId(orderId);        //设置订单号
        orders.setOrderTime(LocalDateTime.now());//设置创建时间
        orders.setCheckoutTime(LocalDateTime.now());//设置结账时间
        orders.setStatus(2); //设置订单状态
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(uid); //设置uid
        orders.setNumber(String.valueOf(orderId));//设置订单号
        orders.setUserName(user.getName());//设置用户名
        orders.setConsignee(address.getConsignee());//设置收货人
        orders.setPhone(address.getPhone());  //设置收货人电话
        orders.setAddress((address.getProvinceName() == null ? "" : address.getProvinceName())
                + (address.getCityName() == null ? "" : address.getCityName())
                + (address.getDistrictName() == null ? "" : address.getDistrictName())
                + (address.getDetail() == null ? "" : address.getDetail()));//设置详细地址

        //向订单表插入数据，一条数据
        this.save(orders);
        //向订单明细表插入数据，有多少插入多少条
        orderDetailService.saveBatch(orderDetails);
        //清空购物车数据
        cartService.remove(cartLambdaQueryWrapper);
    }
}
