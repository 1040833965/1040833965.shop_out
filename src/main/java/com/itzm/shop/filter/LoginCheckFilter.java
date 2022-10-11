package com.itzm.shop.filter;

import com.alibaba.fastjson.JSON;
import com.itzm.shop.common.BaseContext;
import com.itzm.shop.common.JsonResult;
import com.itzm.shop.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : 张金铭
 * @description :过滤器，检查用户是否完成登入
 * @create :2022-09-23 11:44:00
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //用来路径匹配，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //过滤器具体的处理逻辑如下:
        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();
//        log.info("拦截到请求：{}",request.getRequestURI());
        //2、判断本次请求是否需要处理
        //定义不需要处理的路径
        String[] urls = new String[]{
            "/employee/login",//管理员登入请求
            "/employee/logout",//管理员登出请求
            "/backend/**",//管理员页面资源请求
            "/front/**",//用户页面资源请求
            "/common/**",
                //移动端登入和登出的请求
            "/user/sendMsg",//用户发送验证码请求
            "/user/login"//用户登入请求
        };
        //判断
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if (check){
//            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1、判断员工登录状态，如果已登录，则直接放行
       if( request.getSession().getAttribute("employee")!=null){
//           log.info("用户已登入，用户名为{}",request.getSession().getAttribute("employee"));
//           long id = Thread.currentThread().getId();
           //将用户id封装进线程
           BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
           filterChain.doFilter(request,response);
           return;
        }
       //4-2 判断用户登入状态
        if( request.getSession().getAttribute("user")!=null){
//           log.info("用户已登入，用户名为{}",request.getSession().getAttribute("employee"));
//           long id = Thread.currentThread().getId();
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }



        //5、如果未登录则返回未登录结果,通过输出流的方式向客户端页面相应数据，前端通过返回的json判断是否拦截
        log.info("用户未登入");
        response.getWriter().write(JSON.toJSONString(JsonResult.error("NOTLOGIN")));
       return;
//        log.info("拦截到请求：{}",request.getRequestURI());

    }

    /**
     * 路径匹配，检查此时请求是否放行
     * @param urls
     * @param requestUrl
     * @return
     */
    public boolean check(String[] urls,String requestUrl){
        for (String url: urls
             ) {
            boolean match = PATH_MATCHER.match(url, requestUrl);
            if (match){
                //匹配上了返回true
                return true;
            }
        }
        return false;
    }


}
