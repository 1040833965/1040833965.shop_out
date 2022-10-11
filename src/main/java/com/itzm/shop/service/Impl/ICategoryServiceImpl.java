package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.Category;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.entity.Setmeal;
import com.itzm.shop.mapper.CategoryMapper;
import com.itzm.shop.service.ICategoryService;
import com.itzm.shop.service.IDishService;
import com.itzm.shop.service.ISetmealService;
import com.itzm.shop.service.ex.ServiceException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author : 张金铭
 * @description : 分类服务类
 * @create :2022-09-25 17:58:00
 */
@Service
public class ICategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Resource
    private ISetmealService setmealService;

    @Resource
    private IDishService dishService;


    /**
     * 根据id删除分类，删除前需要进行判断是否影响菜品和套餐的表单数据
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，关联了，抛出一个错误
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if (dishCount > 0){
            //抛出一个异常
            throw new ServiceException("当前分类已关联菜品,请先删除相关菜品");
        }
        //查询是否关联了套餐，关联，抛出一个异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount = setmealService.count(setmealLambdaQueryWrapper);
        if (setmealCount > 0){
            throw new ServiceException("当前分类已关联了套餐,请先删除相关套餐");
        }
        //一切正常，进行删除操作
        super.removeById(id);
    }
}
