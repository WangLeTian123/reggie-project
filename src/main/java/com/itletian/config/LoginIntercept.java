package com.itletian.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 拦截器
 */
@Component
public class LoginIntercept implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.得到session对象
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("employee") != null) {
            //说明已经登陆，可以放行
            return true;
        }
        // 执行到这行表示未登录，未登录就重定向到到登陆页面
        response.sendRedirect("/employee/login");
        return false;
    }
}

