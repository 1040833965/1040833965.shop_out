package com.itzm.shop.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.entity.Employee;
import com.itzm.shop.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.sound.sampled.Line;
import java.time.LocalDateTime;

/**
 * @author : 张金铭
 * @description :
 * @RestController： 标注成控制层
 * @create :2022-09-23 09:18:00
 */
@Slf4j
@RestController
@RequestMapping("/employee")
@ResponseBody
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;


    /**
     * 员工登录方法
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public JsonResult<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将提交的密码进行Md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

//        2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

//        3、如果没有查询到则返回登录失败结果
        if (emp==null){
            return JsonResult.error("登入失败，用户名不存在");
        }
//        4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return JsonResult.error("用户名或密码不匹配");
        }
//        5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()!=1){
            return JsonResult.error("该账号已锁定");
        }

//        6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        emp.setPassword(null);
        return JsonResult.success(emp);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public JsonResult logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return JsonResult.success("退出成功");
    }

    /**
     * 新增员工方法
     * @param employee
     * @return
     */
    @PostMapping
    public JsonResult<String> save(HttpServletRequest request , @RequestBody Employee employee){



        //设置员工初始密码123456
        //进行MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        if (!employee.getUsername().equals("admin")){
            return JsonResult.error("权限不够");
        }
        //设置创建时间
//        employee.setCreateTime(LocalDateTime.now());
        //更新时间
//        employee.setUpdateTime(LocalDateTime.now());

        //获得当前操作用户id
//        Long creat = (Long) request.getSession().getAttribute("employee");
        //更新操作用户
//        employee.setUpdateUser(creat);
//        employee.setCreateUser(creat);

        employeeService.save(employee);
        return JsonResult.success("操作成功");
    }

    /**
     * 员工信息的分页查询
     * @param page：多少页
     * @param pageSize：每页多少
     * @param name：需要查询的姓名
     * @return
     */
    @GetMapping("/page")
    public JsonResult<Page<Employee>> page(int page,int pageSize,String name){
//        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        //name不为空，添加条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询

        return JsonResult.success(employeeService.page(pageInfo, queryWrapper));
    }

    /**
     * 更新员工的方法
     * @param employee
     * @return
     */
    @PutMapping
    public JsonResult<String> update(HttpServletRequest request, @RequestBody Employee employee){
//      log.info(employee.toString());
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser((Long) request.getSession().getAttribute("employee"));
//        long id = Thread.currentThread().getId();
//        log.info("id为{}",id);

//        if (request.getSession().getAttribute("employee").equals("1")){
//            System.out.println(request.getSession().getAttribute("employee"));
//            return JsonResult.error("权限不够");
//        }
//        long id = Thread.currentThread().getId();
//        log.info("id为{}",id);
        employeeService.updateById(employee);
        return JsonResult.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public JsonResult<Employee> getById(@PathVariable Long id){
        log.info("查询员工信息");
        Employee byId = employeeService.getById(id);
        return JsonResult.success(byId);
    }
}
