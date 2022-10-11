package com.itzm.shop.dto;

import com.itzm.shop.entity.OrderDetail;
import com.itzm.shop.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
