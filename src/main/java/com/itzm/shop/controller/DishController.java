package com.itzm.shop.controller;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.common.DishStatic;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.entity.Category;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.entity.DishFlavor;
import com.itzm.shop.service.IDishFlavorService;
import com.itzm.shop.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description : 菜品的控制层
 * @create :2022-09-26 00:32:00
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Resource
    private IDishService dishService;


    @Resource
    private IDishFlavorService dishFlavorService;


    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 分页查询和指定菜名
     * @param page
     * @param pageSize
     * @param name
     * @return 返回分页查询后的DishDto数据
     */
    @GetMapping("/page")
    public JsonResult<Page<DishDto>> page(int page , int pageSize, String name){

        //  log.info("第几页{},每页多少{},是否指定{}",page,pageSize,name);
        //创建分页构造器
        Page pageInfo = new Page(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //若有指定菜品查询
        queryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        //服务层执行并返回
        Page pageDish = dishService.page(pageInfo, queryWrapper);
        //将分好页的Page里的数据替换成dishDto的列表数据
        pageDish.setRecords(dishService.pageDishDto(pageInfo,queryWrapper));
        return JsonResult.success(pageDish);
    }

    /**
     * 新增菜品
     * TODO 服务层判断成功与否待优化
     * @param dishDto
     * @return
     */
    @PostMapping
    public JsonResult<String> save(@RequestBody DishDto dishDto){
//        log.info("dishDto:{}",dishDto);
//        log.info("dishDto名字:{}",dishDto.getName());
        //新增菜品后清理所有的菜品缓存
//        Long categoryId = dishDto.getCategoryId();
//        Set keys = redisTemplate.keys(DishStatic.CATEGORYkEY + "*");
//        Long delete = redisTemplate.delete(keys);

        //新增菜品后应该清除缓存中该菜品的分类数据的缓存
        Long categoryId = dishDto.getCategoryId();
        String key = DishStatic.CATEGORYkEY + categoryId + "_1";
        redisTemplate.delete(key);

        dishService.saveWithFlavor(dishDto);
        return JsonResult.success("新增成功");
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public JsonResult<String> update(@RequestBody DishDto dishDto){
        //更新菜品后应该清除缓存中该菜品的分类数据的缓存
        Long categoryId = dishDto.getCategoryId();
        String key = DishStatic.CATEGORYkEY + categoryId + "_1";
        redisTemplate.delete(key);


        dishService.updateWithFlavor(dishDto);
        return JsonResult.success("修改成功");
    }

    /**
     * 根据id查询对应的菜品信息并反给前端页面
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public JsonResult<DishDto> getById(@PathVariable Long id){
        DishDto dishById = dishService.getDishById(id);
        return JsonResult.success(dishById);
    }

    /**
     * 批量删除和单个删除的方法
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> deleteById(Long[] ids){
            boolean removeById = dishService.removeById(ids);
            if (removeById==false){
                return JsonResult.error("删除失败，请刷新页面重试");
            }

        return JsonResult.success("操作成功");
    }

    /**
     * 根据前端传来的status来判断是改变什么状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public JsonResult<String> updateStatus(@PathVariable Integer status,Long[] ids){
//        log.info("status的值,{}",status);
//        log.info("id的值，{}",ids);
        //修改状态，将停售的菜品分类的缓存清除


        if (dishService.updateStatus(status, ids)){
            return JsonResult.success("状态更新成功");
        }
        return JsonResult.error("状态更新错误，刷新看看");
    }

    /**
     * 通过菜品分类id查询该分类下的所有菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    public JsonResult<List<DishDto>> getListById(Long categoryId){
//        log.info("categoryId:{}",categoryId);
        //事先构建返回
        List<DishDto> dishDtoList = null;

        //动态构造redis中的key
        String key = DishStatic.CATEGORYkEY +categoryId+"_1";
        //先从redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        //若存在，则直接返回缓存，无需查询数据库
        if (dishDtoList!=null){
            return JsonResult.success(dishDtoList);
        }
        //如果不存在，查询数据库，将菜品信息存到redis

        ArrayList<DishFlavor> dishFlavors = new ArrayList<>();
        //设置条件查询
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //查询状态在售，categoryid指定按照更新时间倒序排序的
        queryWrapper.eq(Dish::getCategoryId,categoryId).eq(Dish::getStatus,1).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        if (dishList.size()==0){
            return JsonResult.success(null);
        }
        dishDtoList = dishList.stream().map(itms->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(itms,dishDto);
            Long dishId = itms.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavorList);
            return dishDto;
        }).collect(Collectors.toList());
//        log.info("dishDtoList: {}",dishDtoList);
        //存入缓存,设置时间为30分钟固定清理一次
        redisTemplate.opsForValue().set(key,dishDtoList,30, TimeUnit.MINUTES);
        return JsonResult.success(dishDtoList);
    }
}
