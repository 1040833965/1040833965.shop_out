package com.itzm.shop.common;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : 张金铭
 * @description :返回类
 * @create :2022-09-23 09:23:00
 */
@Data
public class JsonResult<T> {
    private Integer code;//编码，1成功，0和其他为失败

    private String msg;//返回的信息

    private T data;//返回数据

    private Map map = new HashMap();//动态数据


    /**
     * 成功时条调用该方法
     * @param object
     * @param <T>
     * @return
     */
    public static <T> JsonResult<T> success(T object){
        JsonResult<T> result = new JsonResult<>();
        result.data = object;
        result.code = 1;
        return result;
    }

    /**
     * 失败时调用该方法
     * @param msg
     * @param <T>
     * @return
     */
    public static  <T> JsonResult<T> error(String msg){
        JsonResult result = new JsonResult();
        result.msg = msg;
        result.code = 0;
        return result;
    }

    /**
     * 待续
     * @param key
     * @param value
     * @return
     */
    public JsonResult<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }

}
