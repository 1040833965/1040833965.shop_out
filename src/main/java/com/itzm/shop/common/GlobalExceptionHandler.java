package com.itzm.shop.common;

import com.itzm.shop.service.ex.CustomException;
import com.itzm.shop.service.ex.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author : 张金铭
 * @description :全局的异常处理,只要某个类加入了 RestController Controller注解就会被这个类处理
 * @create :2022-09-23 16:21:00
 */

@ResponseBody
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 异常处理方法
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public JsonResult<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());
        if (ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String name = split[2];
            return JsonResult.error(name+"已存在");
        }
        return JsonResult.error("添加用户失败,未知错误");
    }

    /**
     * 业务层异常处理方法
     * @return
     */
    @ExceptionHandler(ServiceException.class)
    public JsonResult<String> exceptionHandler(ServiceException ex){
        log.error(ex.getMessage());

        //返回前端相关异常报错
        return JsonResult.error(ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    public JsonResult<String> exceptionHandler(CustomException ex){
        log.info(ex.getMessage());

        //返回前端相关异常报错
        return JsonResult.error(ex.getMessage());
    }
}
