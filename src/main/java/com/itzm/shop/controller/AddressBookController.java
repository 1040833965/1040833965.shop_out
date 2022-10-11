package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.itzm.shop.common.BaseContext;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.entity.AddressBook;
import com.itzm.shop.service.IAddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * @author : 张金铭
 * @description :地址簿控制层
 * @create :2022-10-08 15:42:00
 */
@RestController
@Slf4j
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 新增
     */
    @PostMapping
    public JsonResult<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return JsonResult.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public JsonResult<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook:{}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return JsonResult.success(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public JsonResult get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return JsonResult.success(addressBook);
        } else {
            return JsonResult.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public JsonResult<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);

        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = addressBookService.getOne(queryWrapper);

        if (null == addressBook) {
            return JsonResult.error("没有找到该对象");
        } else {
            return JsonResult.success(addressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public JsonResult<List<AddressBook>> list(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("addressBook:{}", addressBook);

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        //SQL:select * from address_book where user_id = ? order by update_time desc
        return JsonResult.success(addressBookService.list(queryWrapper));
    }

    /**
     * 更新地址的方法
     * @param addressBook
     * @return
     */
    @PutMapping
    public JsonResult<String> update(@RequestBody AddressBook addressBook){
//        log.info("address:  {}",addressBook);
        if (addressBookService.updateById(addressBook)==false) {
            return JsonResult.error("修改信息失败");
        }
        return JsonResult.success("修改信息成功");
    }


    /**
     * 删除选中的地址
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> delete(Long[] ids){
//        log.info("ids: {}" ,ids);
        addressBookService.removeByIds(Arrays.asList(ids));
        return JsonResult.success("删除成功") ;

    }
}
