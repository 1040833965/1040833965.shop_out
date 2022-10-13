package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.common.DishStatic;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.dto.SetmealDto;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.entity.Setmeal;
import com.itzm.shop.entity.SetmealDish;
import com.itzm.shop.service.IDishService;
import com.itzm.shop.service.ISetmealDishService;
import com.itzm.shop.service.ISetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-09-26 00:33:00
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {


    @Resource
    private ISetmealService setmealService;

    @Resource
    private ISetmealDishService setmealDishService;

    @Resource
    private IDishService dishService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 保存新套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public JsonResult<String> saveSetmealAndDish(@RequestBody SetmealDto setmealDto){
//        log.info("setmealDto:{}",setmealDto);
        setmealService.saveSetmealAndDish(setmealDto);
        return JsonResult.success("菜单保存成功");
    }


    @GetMapping("/page")
    public JsonResult<Page<SetmealDto>> page(int page,int pageSize,String name){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        Page setmealPage = new Page(page, pageSize);
        //条件构造
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //若有指定套餐名
        setmealLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //排序条件根据更新时间倒序排序
        setmealLambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //服务层执行并返回
        Page pageInfo = setmealService.page(setmealPage,setmealLambdaQueryWrapper);
        if (pageInfo.getRecords()==null){
            return null;
        }
        pageInfo.setRecords(setmealService.pageSetmealDto(setmealPage, setmealLambdaQueryWrapper));

        return JsonResult.success(pageInfo);
    }


    @GetMapping("/list")
    public JsonResult<List<Setmeal>> getSetmealDish(@RequestParam("categoryId") Long id,@RequestParam("status") Integer status){
//        log.info("数据：{},   {}" ,id,status);
        List<Setmeal> list = null;
        //动态构造redis中的key
        String key = DishStatic.CATEGORYkEY +id+"_1";
        //先从redis中获取缓存数据
        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
        //若存在，则直接返回缓存，无需查询数据库
        if (list!=null){
            return JsonResult.success(list);
        }

        //不存在执行查询数据库操作
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,id).eq(Setmeal::getStatus,1);
        list = setmealService.list(queryWrapper);
        //存入缓存,设置时间为30分钟固定清理一次
        redisTemplate.opsForValue().set(key,list,30, TimeUnit.MINUTES);
        return JsonResult.success(list);
    }


    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public JsonResult<SetmealDto> getSetMealById(@PathVariable Long id){
//        log.info("id:{}",id);
        SetmealDto setMealByID = setmealService.getSetMealByID(id);
        return JsonResult.success(setMealByID);
    }

    /**
     * 更新套餐详情
     * @param setmealDto
     * @return
     */
    @PutMapping
    public JsonResult<String> update(@RequestBody SetmealDto setmealDto){

        if (setmealService.updateWithDish(setmealDto)){
            return JsonResult.success("更新成功！");
        }
        return JsonResult.error("更新出错，请重新检查网络");
    }


    /**
     * 通过id修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public JsonResult<String> updateStatus(@PathVariable Integer status,Long[] ids){
//        log.info("status的值,{}",status);
//        log.info("id的值，{}",ids);

        if (setmealService.updateStatus(status, ids)){
            return JsonResult.success("状态更新成功");
        }
        return JsonResult.error("状态更新错误，刷新看看");
    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public JsonResult<String> deleteById(Long[] ids){
        boolean removeById = setmealService.removeById(ids);
        if (removeById==false){
            return JsonResult.error("菜品异常请刷新页面重试");
        }
        return JsonResult.success("操作成功");
    }


    /**
     * 获取套餐的菜品详情
     * @param id 套餐id
     * @return
     */
    @GetMapping("/dish/{id}")
    public JsonResult<List<DishDto>> getSetmealDishDish(@PathVariable Long id){
//        log.info("Dish id:  {}",id);
        //通过条件判断获取套餐中菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        //将菜品通过id查询菜品表获取菜品详细信息后封装到DishDto中
        List<DishDto> collect = list.stream().map(itms -> {
            Long dishId = itms.getDishId();
            DishDto dishById = dishService.getDishById(dishId);
            dishById.setCopies(itms.getCopies());
            return dishById;
        }).collect(Collectors.toList());
        //返回一个套餐中菜品的数据
        return JsonResult.success(collect);
    }

}
