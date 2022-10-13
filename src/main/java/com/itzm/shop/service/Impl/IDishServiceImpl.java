package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.common.DishStatic;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.entity.Category;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.entity.DishFlavor;
import com.itzm.shop.entity.SetmealDish;
import com.itzm.shop.mapper.DishMapper;
import com.itzm.shop.service.*;
import com.itzm.shop.service.ex.CustomException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:24:00
 */
@Service
public class IDishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {

    @Resource
    private IDishFlavorService flavorService;

    @Resource
    private ICategoryService categoryService;

    @Resource
    private ISetmealDishService setmealDishService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品，保存口味数据
     * @param dishDto
     */
    @Transactional
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //直接将菜品保存到菜品表
        this.save(dishDto);
        Long dishId = dishDto.getId(); //菜品的id
        //将获取到的菜品口味集合取出
        List<DishFlavor> flavors = dishDto.getFlavors();
        //将设置的菜品口味设置上对应的菜品id
        flavors = flavors.stream().map((itms) ->{
            itms.setDishId(dishId);
            return itms;
        }).collect(Collectors.toList());

        //保存菜品口味数据到菜品口味表dish_flavor
        flavorService.saveBatch(flavors);
    }

    /**
     * 分页展示菜品的方法
     * @param page
     * @param queryWrapper
     * @return
     */
    @Override
    public List<DishDto> pageDishDto(Page page, LambdaQueryWrapper queryWrapper) {
        //先分页查询Dish的数据,Page里的Records是存储数据的集合
        List list =  this.page(page,queryWrapper).getRecords();
        ArrayList<DishDto> dishDtos = new ArrayList<>();
        //创建一个DishDto的集合,有多少Dish数据就往里添加多少个数据
        for (int i = 0; i < list.size(); i++) {
            Dish o = (Dish) list.get(i);
            LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorQueryWrapper.eq(DishFlavor::getDishId,o.getId());
//            //执行查询菜品口味
//            List<DishFlavor> dishFlavorList = flavorService.list(dishFlavorQueryWrapper);
            DishDto dishDto = new DishDto();
            //根据Dish中的菜品分类id查询菜品的分类
            Category category = categoryService.getById(o.getCategoryId());
            //往DishDto填充需要的数据
//            dishDto.setFlavors(dishFlavorList);
            if (category!=null){
                dishDto.setCategoryName(category.getName());
            }
            //前端需要的数据，看着填充，不需要就不给
            dishDto.setId(o.getId());
            dishDto.setName(o.getName());
            dishDto.setStatus(o.getStatus());
            dishDto.setCode(o.getCode());
            dishDto.setImage(o.getImage());
            dishDto.setPrice(o.getPrice());
            dishDto.setUpdateTime(o.getUpdateTime());

            dishDtos.add(dishDto);
        }
        return dishDtos;
    }

    /**
     * 更新菜品数据的方法
     * @param dishDto
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //根据id更新
        this.updateById(dishDto);
        //取出id
        Long id = dishDto.getId();
        //取出设置的菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map(itms -> {
            //将每个口味设置上对应的菜品的id
            itms.setDishId(id);
            return itms;
        }).collect(Collectors.toList());

        //先删除所有跟此id相关的菜品口味，再将此次的菜品口味存入更新
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        flavorService.remove(dishFlavorLambdaQueryWrapper);
        flavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息和菜品的口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getDishById(Long id) {
        //根据id获取菜品信息
        Dish byId = this.getById(id);
        //根据id获取菜品口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = flavorService.list(dishFlavorQueryWrapper);
        //将信息封装进DishDto
        DishDto dishDto = new DishDto();
        //菜品id
        dishDto.setId(byId.getId());
        //菜品名字
        dishDto.setName(byId.getName());
        //菜品分类id
        dishDto.setCategoryId(byId.getCategoryId());
        //菜品口味
        dishDto.setFlavors(list);
        //菜品图片地址
        dishDto.setImage(byId.getImage());
        //菜品描述
        dishDto.setDescription(byId.getDescription());
        //菜品价格
        dishDto.setPrice(byId.getPrice());
        return dishDto;
    }

    /**
     * 根据id删除对应的菜品信息和菜品口味
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public boolean removeById(Long[] ids) {
        //遍历所有的id
        for (Long id:ids
             ) {
            //获取菜品的信息，不为空的情况下且没有绑定套餐的情况下才能删除
            Dish byId = this.getById(id);
            if (byId==null){
                return false;
            }
            //菜品状态处于在售时无法删除
            if (byId.getStatus()==1){
                throw new CustomException("菜品"+byId.getName()+"处于销售状态，请先停售");
            }
            //设置条件查询该菜品是否与套餐绑定
            LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            setmealDishLambdaQueryWrapper.eq(SetmealDish::getDishId,id);
            List<SetmealDish> setmealDishList = setmealDishService.list(setmealDishLambdaQueryWrapper);
            //判断
            if (setmealDishList.size()!=0){
                throw new CustomException("菜品 "+byId.getName()+" 与套餐有绑定");
            }


            //设置条件查询菜品口味
            LambdaQueryWrapper<DishFlavor> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            //根据菜品口味中对应的菜品id删除菜品口味
            flavorService.remove(dishLambdaQueryWrapper);
            //删除菜品
            if (this.removeById(id)==false) {
              throw new CustomException("菜品异常");
            }
        }
        return true;
    }

    /**
     * 更新状态
     * @param status
     * @param ids
     * @return
     */
    @Transactional
    @Override
    public boolean updateStatus(Integer status, Long[] ids) {
        for (Long id: ids
             ) {
            Dish byId = this.getById(id);
            if (byId!=null){
                byId.setStatus(status);
                this.updateById(byId);
                //获取到菜品分类cid
                Long cid = byId.getCategoryId();
                //根据cid查询缓存中的数据,删除缓存中的数据
                Set keys = redisTemplate.keys(DishStatic.CATEGORYkEY + cid + "_1");
                redisTemplate.delete(keys);
            }else {
                return false;
            }
        }
        return true;
    }
}
