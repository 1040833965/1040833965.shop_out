package com.itzm.shop.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itzm.shop.dto.SetmealDto;
import com.itzm.shop.entity.Setmeal;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:21:00
 */
public interface ISetmealService extends IService<Setmeal> {

    /**
     * 保存套餐
     * @param setmealDto
     */
    void saveSetmealAndDish(SetmealDto setmealDto);


    /**
     * 将分页数据以列表形式返回
     * @param page
     * @param queryWrapper
     * @return
     */
    List<SetmealDto> pageSetmealDto(Page page, LambdaQueryWrapper queryWrapper);


    SetmealDto getSetMealByID(Long id);

    /**
     * 更新套餐的信息和他的菜品信息
     * @param setmealDto
     * @return
     */
    boolean updateWithDish(SetmealDto setmealDto);

    /**
     * 通过id修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    boolean updateStatus(Integer status,Long [] ids);

    /**
     * 通过id删除套餐信息和套餐餐品信息
     * @param ids
     * @return
     */
    boolean removeById(Long[] ids);
}
