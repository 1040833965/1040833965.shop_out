package com.itzm.shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itzm.shop.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : 张金铭
 * @description :地址簿dao层
 * @create :2022-10-08 15:39:00
 */
@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
