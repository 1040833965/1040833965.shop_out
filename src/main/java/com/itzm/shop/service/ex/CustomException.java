package com.itzm.shop.service.ex;

/**
 * @author : 张金铭
 * @description :自定义业务异常
 * @create :2022-10-02 00:33:00
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
