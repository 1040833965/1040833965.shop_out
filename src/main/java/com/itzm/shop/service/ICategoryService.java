package com.itzm.shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itzm.shop.entity.Category;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-25 17:52:00
 */
public interface ICategoryService extends IService<Category> {

    public void remove(Long id);
}
