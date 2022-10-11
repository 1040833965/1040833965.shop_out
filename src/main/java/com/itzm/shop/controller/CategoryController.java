package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.entity.Category;
import com.itzm.shop.service.ICategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : 张金铭
 * @description : 分类的控制层
 * @create :2022-09-25 18:06:00
 */
@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Resource
    private ICategoryService categoryService;

    /**
     * 新增菜品、套餐分类
     * @param category
     * @return
     */
    @PostMapping
    public JsonResult<String> save(@RequestBody Category category){
        log.info("category，{}",category);
        categoryService.save(category);
        return JsonResult.success("新增分类成功");
    }

    /**
     * 分类管理的分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public JsonResult<Page> page(int page,int pageSize){
        //分页构造器
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件,根据sort进行排序
        lambdaQueryWrapper.orderByAsc(Category::getSort);

        //分页查询
        Page<Category> pageData = categoryService.page(pageInfo, lambdaQueryWrapper);
        //返回顺序
        return JsonResult.success(pageData);
    }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
    @DeleteMapping
    public JsonResult<String> delete(Long id){
//        log.info("id为{}",id);
        categoryService.remove(id);
        return JsonResult.success("OK");
    }


    /**
     * 根据id修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public JsonResult<String> edit(@RequestBody Category category){
        log.info("修改分类信息：{}",category);
        categoryService.updateById(category);
        return JsonResult.success("分类修改成功");
    }


    @GetMapping("/list")
    public JsonResult<List<Category>> list(Category category){
        //根据条件来查询分类数据
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        categoryLambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        categoryLambdaQueryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        //查询
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return JsonResult.success(list);
    }
}
