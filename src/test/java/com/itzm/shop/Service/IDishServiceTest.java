package com.itzm.shop.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.entity.Category;
import com.itzm.shop.entity.Dish;
import com.itzm.shop.service.ICategoryService;
import com.itzm.shop.service.IDishService;
import org.apache.commons.lang.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-03 03:18:00
 */
// 表示启动这个单元测试类（单元测试类是不能运行的），需要传递一个参数，必须是SpringRunner的实例类型

////SpringBootTest:表示标注当前的类是一个测试类，不会随同项目一块打包
@SpringBootTest
public class IDishServiceTest {

    @Resource
    IDishService dishService;


    @Resource
    ICategoryService categoryService;
    @Test
    public void test01(){
        String name = "";
        //创建分页构造器
        Page pageInfo = new Page(0, 10);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //若有指定菜品查询
        queryWrapper.eq(StringUtils.isNotEmpty(""),Dish::getName,name);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);

//        Page page = dishService.page(pageInfo, queryWrapper);
//        List records = page.getRecords();
//        for (Object d:records
//             ) {
//            System.out.println((Dish)d);
//        }
        List<DishDto> dishDtos = dishService.pageDishDto(pageInfo, queryWrapper);
//        System.out.println(dishDtos);
        for (DishDto d :
                dishDtos) {
            System.out.println(d);
            d.getName();
        }
    }

    @Test
    public void test02(){
        Category byId = categoryService.getById(1397844263642378242L);
        System.out.println(byId.getName());
    }
}
