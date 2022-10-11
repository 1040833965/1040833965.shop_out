package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.AddressBook;
import com.itzm.shop.mapper.AddressBookMapper;
import com.itzm.shop.service.IAddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author : 张金铭
 * @description :地址簿服务层实现类
 * @create :2022-10-08 15:40:00
 */
@Service
public class IAddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {
}
