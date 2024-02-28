package com.itletian.config.filter;

import com.alibaba.fastjson.JSON;
import com.itletian.config.mybaitPlus.BaseContext;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器：
 *      检查用户是否已经完成登录
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1、获取本次请求的URI
        String uri = request.getRequestURI();       // /backend/index.html
        log.info("拦截到请求：{}", uri);

        // 定义不需要处理的请求路径
        String[] urls = new String[]{
          "/employee/login",
          "/employee/logout",
          "/backend/**",
          "/front/**",
          "/user/sendMsg",
          "/user/login"
        };

        // 2、判断本次请求是否需要处理
        boolean check = check(urls, uri);

        //3、如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4-1、判断后台登陆状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("employee") != null) {

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }
        // 4-2、判断移动端登陆状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("user") != null) {

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }

        log.info("用户未登录");
        // 5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public boolean check(String[] urls, String uri) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, uri);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
