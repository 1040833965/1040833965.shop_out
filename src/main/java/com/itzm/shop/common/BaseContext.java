package com.itzm.shop.common;

/**
 * @author : 张金铭
 * @description :基于ThreadLocal封装的工具类，用户保存和获取当前登入用户id
 * @create :2022-09-24 17:46:00
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
