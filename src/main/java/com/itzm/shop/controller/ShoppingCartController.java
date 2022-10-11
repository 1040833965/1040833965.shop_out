package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.dto.DishDto;
import com.itzm.shop.entity.ShoppingCart;
import com.itzm.shop.service.IShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : 张金铭
 * @description :
 * @create :2022-10-09 14:29:00
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Resource
    private IShoppingCartService shoppingCartService;


    /**
     * 添加到购物车
     * @param shoppingCart 购物车实体类数据
     * @return
     */
    @PostMapping("/add")
    public JsonResult<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart, HttpSession session){
//        log.info("菜品数据  ：  dishDto:{}",shoppingCart);

        //1.获取当前用户uid
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());
        //2.判断数据库中是否依旧有了相同的数据，若相同则直接数据加1
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId)
                .eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId())
                .eq(StringUtils.isNotEmpty(shoppingCart.getDishFlavor()),ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor())
                .eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId())
                .eq(ShoppingCart::getName,shoppingCart.getName());
        ShoppingCart shop = shoppingCartService.getOne(cartLambdaQueryWrapper);
        //3.shop不为空则存在相同数据
        if (shop!=null){
            //3.1存在相同的数据则数量直接加一
            Integer num = shop.getNumber()+1;
            shop.setNumber(num);
            //3.2更新
            if (shoppingCartService.updateById(shop)==false) {
                return JsonResult.error("错误");
            }
            return JsonResult.success(shoppingCart);
        }
        //4.这是个新数据，直接添加
        if(shoppingCartService.save(shoppingCart)==false){
            return JsonResult.error("加入购物车出错");
        }
        return JsonResult.success(shoppingCart);
    }

    /**
     * 获取购物车数据
     * @param session
     * @return
     */
    @GetMapping("/list")
    public JsonResult<List<ShoppingCart>> getCart(HttpSession session){
        //直接获取当前user的购物车数据
        Long userId = (Long) session.getAttribute("user");
        log.info("userId  :{}",userId);
        //从数据库中搜索直接信息
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> list = shoppingCartService.list(cartLambdaQueryWrapper);
        return JsonResult.success(list);
    }


    @PostMapping("/sub")
    public JsonResult<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart,HttpSession session){
        log.info("shoppingCart:  {}",shoppingCart);
        //获取当前用户uid,加入当前数据
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);
        //通过菜品id或套餐id+用户id查询数据
        LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        cartLambdaQueryWrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        cartLambdaQueryWrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        cartLambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        ShoppingCart cart = shoppingCartService.getOne(cartLambdaQueryWrapper);
        //查到该数据
        if (cart!=null){
            Integer number = cart.getNumber();
            if (number==1){
                //若数量只有1了则直接删除
                shoppingCartService.removeById(cart);
            }
            number-=1;
            cart.setNumber(number);
            //将数据更新到数据库
            shoppingCartService.updateById(cart);
        }
        //数据返回前端
        return JsonResult.success(cart);
    }


    /**
     * 清空当前user购物车
     * @return
     */
     @DeleteMapping("/clean")
     public JsonResult clean(HttpSession session){
         //获取当前用户uid
         Long uid = (Long) session.getAttribute("user");

         //数据库查询
         LambdaQueryWrapper<ShoppingCart> cartLambdaQueryWrapper = new LambdaQueryWrapper<>();
         cartLambdaQueryWrapper.eq(ShoppingCart::getUserId,uid);
         shoppingCartService.remove(cartLambdaQueryWrapper);
         return JsonResult.success("OK");
     }


}
