package com.itzm.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.entity.DishFlavor;

import java.util.List;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:21:00
 */

public interface IDishService extends IService<Dish> {


    /**
     * 更新菜品数据的方法
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);

    /**
     * 将分页数据以列表形式返回
     * @param page
     * @param queryWrapper
     * @return
     */
    List<DishDto> pageDishDto(Page page, LambdaQueryWrapper queryWrapper);

//    List<Dish> pageDish(int page,int pageSize);

    void updateWithFlavor(DishDto dishDto);

    /**
     * 根据id查询菜品信息和菜品的口味信息
     * @param id
     * @return
     */
    DishDto getDishById(Long id);

    /**
     * 根据id删除对应的菜品信息和菜品口味
     * @param ids
     * @return
     */
    boolean removeById(Long[] ids);

    /**
     * 更新菜品状态
     * @param status
     * @param ids
     * @return
     */
    boolean updateStatus(Integer status,Long[] ids);

}
