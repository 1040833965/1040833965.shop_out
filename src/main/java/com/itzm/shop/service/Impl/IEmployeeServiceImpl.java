package com.itzm.shop.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itzm.shop.entity.Employee;
import com.itzm.shop.mapper.EmployeeMapper;
import com.itzm.shop.service.IEmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author : 张金铭
 * @description :  员工表服务层，继承ServiceImpl  两个泛型  员工表dao层，员工表实体类
 * @create :2022-09-23 09:16:00
 */
@Service
public class IEmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
}
