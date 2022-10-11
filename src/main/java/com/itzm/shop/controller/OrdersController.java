package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.common.BaseContext;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.dto.OrdersDto;
import com.itzm.shop.entity.OrderDetail;
import com.itzm.shop.entity.Orders;
import com.itzm.shop.service.IOrderDetailService;
import com.itzm.shop.service.IOrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-10 09:18:00
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Resource
    private IOrdersService ordersService;

    @Resource
    private IOrderDetailService orderDetailService;

    /**
     * 用户提交订单api
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public JsonResult<String> submit(@RequestBody Orders orders){
        Long uid = BaseContext.getCurrentId();
//        log.info("订单数据:  {}",orders);
//        log.info("uid :{}",uid);

        if (orders==null){
            return JsonResult.error("数据错误");
        }
        ordersService.submit(orders);
        return JsonResult.success("订单下单成功");
    }


    @GetMapping("/page")
    public JsonResult<Page<OrdersDto>> getOrderMage(int page,int pageSize){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersService.page(ordersPage,ordersLambdaQueryWrapper);
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");

        List<Orders> records = ordersPage.getRecords();
        //搜索订单详情
        List<OrdersDto> collect = records.stream().map(itms -> {
            OrdersDto ordersDto = new OrdersDto();
            Long id = itms.getId();
            LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, id);
            List<OrderDetail> list = orderDetailService.list(detailLambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            BeanUtils.copyProperties(itms, ordersDto);
            return ordersDto;
        }).collect(Collectors.toList());
        ordersDtoPage.setRecords(collect);
        return JsonResult.success(ordersDtoPage);
    }


    @GetMapping("/userPage")
    public JsonResult<Page<OrdersDto>> getOrder(int page, int pageSize){
        //创建分页
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>(page, pageSize);

        //获取当前用户uid
        Long uid = BaseContext.getCurrentId();

        //设置条件查询
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Orders::getUserId,uid);
        //分页查询，数据查询到 ordersPage中
        Page<Orders> pageList = ordersService.page(ordersPage, queryWrapper);
        //拷贝除了数据外的元素到 ordersDtoPage 中
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");

        //取出分页的数据
        List<Orders> records = ordersPage.getRecords();
        //做处理填充dto需要的数据
        List<OrdersDto> collect = records.stream().map(itms -> {
            OrdersDto ordersDto = new OrdersDto();
            //拷贝
            BeanUtils.copyProperties(itms, ordersDto);
            //查询详情表，将具体数据填充
            LambdaQueryWrapper<OrderDetail> detailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            detailLambdaQueryWrapper.eq(OrderDetail::getOrderId, itms.getId());
            List<OrderDetail> list = orderDetailService.list(detailLambdaQueryWrapper);
            ordersDto.setOrderDetails(list);
            return ordersDto;
        }).collect(Collectors.toList());

        //将填充后的数据放如分页数据中
        ordersDtoPage.setRecords(collect);
        //返回分页数据
        return JsonResult.success(ordersDtoPage);
    }

}
