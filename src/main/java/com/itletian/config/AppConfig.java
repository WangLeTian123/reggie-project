package com.itletian.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Autowired
    private LoginIntercept loginIntercept;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginIntercept).
                addPathPatterns("/**").
                excludePathPatterns("/employee/login").
                excludePathPatterns("/employee/logout").
                excludePathPatterns("/login.html").
                excludePathPatterns("/**/*.css").
                excludePathPatterns("/**/*.jpg");
    }
}

