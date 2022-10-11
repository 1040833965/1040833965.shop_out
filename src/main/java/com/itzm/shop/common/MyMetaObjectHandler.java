package com.itzm.shop.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author : 张金铭
 * @description : 该类为 为公共字段填充的方法类， 实习了MetaObjectHandler ,insertFill,updateFill,可以分别在更新和插入时，
 *                自动更新在实体类被注解的字段
 * @create :2022-09-24 16:48:00
 */
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
//        log.info("公共字段自动填充");
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("createUser",BaseContext.getCurrentId());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        log.info(metaObject.toString());
    }

    /**
     * 更改时自动填充公共字段
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
//        log.info("公共字段自动填充");
//        long id = Thread.currentThread().getId();
//        log.info("id为{}",id);
        metaObject.setValue("updateTime",LocalDateTime.now());
        metaObject.setValue("updateUser",BaseContext.getCurrentId());
        log.info(metaObject.toString());
    }


}
