package com.itzm.shop.service.ex;

/**
 * @author : 张金铭
 * @description :业务层异常基类
 * @create :2022-10-02 00:34:00
 */
public class ServiceException extends RuntimeException{
    public ServiceException(String message) {
        super(message);
    }
}
