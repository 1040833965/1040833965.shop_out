package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.dto.SetmealDto;
import com.itzm.shop.entity.*;
import com.itzm.shop.mapper.SetmealMapper;
import com.itzm.shop.service.ICategoryService;
import com.itzm.shop.service.ISetmealDishService;
import com.itzm.shop.service.ISetmealService;
import com.itzm.shop.service.ex.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:31:00
 */
@Service
public class ISetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
    @Resource
    private ISetmealDishService setmealDishService;


    @Resource
    private ICategoryService categoryService;
    /**
     * 保存套餐
     * @param setmealDto
     */
    @Transactional
    @Override
    public void saveSetmealAndDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        //取出id
        Long id = setmealDto.getId();
        //取出设置的菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //给每个套餐中的菜品存上套餐的id
        setmealDishes = setmealDishes.stream().map(itms ->{
            itms.setSetmealId(id);
            return itms;
        }).collect(Collectors.toList());
        //将菜品存入
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 分页查询
     * @param page
     * @param queryWrapper
     * @return
     */
    @Override
    public List<SetmealDto> pageSetmealDto(Page page, LambdaQueryWrapper queryWrapper) {
        //先分页查询Setmeal数据，拿出Page里存放数据的集合
        List records = this.page(page, queryWrapper).getRecords();
        //创建一个setmealDto集合存放数据
        ArrayList<SetmealDto> setmealDtos = new ArrayList<>();
        //查询套餐类型
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //遍历数据
        for (int i = 0; i < records.size(); i++) {

            Setmeal o = (Setmeal) records.get(i);

//            categoryLambdaQueryWrapper.eq(Category::getId,o.getCategoryId());

            Category byId = categoryService.getById(o.getCategoryId());

            SetmealDto setmealDto = new SetmealDto();
            setmealDto.setId(o.getId());

            setmealDto.setName(o.getName());
            setmealDto.setStatus(o.getStatus());
            setmealDto.setCode(o.getCode());
            if (byId!=null){
                setmealDto.setCategoryName(byId.getName());
            }
            setmealDto.setImage(o.getImage());
            setmealDto.setPrice(o.getPrice());
            setmealDto.setUpdateTime(o.getUpdateTime());
            setmealDtos.add(setmealDto);
        }
        return setmealDtos;
    }

    /**
     * 获取到单独的套餐详细信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetMealByID(Long id) {
        SetmealDto setmealDto = new SetmealDto();
        //通过查询到的id
        Setmeal byId = this.getById(id);
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getDishId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
//        Category category = categoryService.getById(byId.getCategoryId());

        setmealDto.setId(byId.getId());
        setmealDto.setName(byId.getName());
        setmealDto.setPrice(byId.getPrice());
        setmealDto.setImage(byId.getImage());
        setmealDto.setSetmealDishes(list);
        setmealDto.setCode(byId.getCode());
        setmealDto.setCategoryId(byId.getCategoryId());

        return setmealDto;
    }

    /**
     * 更新数据
     * @param setmealDto
     * @return
     */
    @Transactional
    @Override
    public boolean updateWithDish(SetmealDto setmealDto) {
        //根据id直接更新
        this.updateById(setmealDto);
        //取出id
        Long id = setmealDto.getId();
        //取出套餐中的菜品，给每个菜品加上该套餐的id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map(itms ->{
            itms.setSetmealId(id);
            return itms;
        }).collect(Collectors.toList());

        //将之前套餐的菜品删除，再将新的菜品加入
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        setmealDishService.remove(queryWrapper);
        //存入新菜品
        return setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 通过id更改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public boolean updateStatus(Integer status, Long[] ids) {
        for (Long id: ids
        ) {
            Setmeal byId = this.getById(id);
            if (byId!=null){
                byId.setStatus(status);
                this.updateById(byId);
            }else {
                return false;
            }
        }
        return true;
    }


    /**
     * 将需要删除的ids遍历循环删除
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public boolean removeById(Long[] ids) {
        //遍历所有的id
        for (Long id:ids) {
            //获取套餐的信息，不为空的情况下且在停售的情况下才能删除
            Setmeal byId = this.getById(id);
            if (byId==null){
                return false;
            }

            //判断套餐售卖状态
            if (byId.getStatus()==1){
                throw new CustomException(byId.getName() +" 处于在售状态，请先停售!");
            }
            //设置条件查询菜品口味
            LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
            //根据菜品口味中对应的菜品id删除菜品口味
            setmealDishService.remove(dishLambdaQueryWrapper);
            //删除套餐
            if (this.removeById(id)==false) {
               throw new CustomException("菜品异常");
            }
        }
        return true;
    }
}
