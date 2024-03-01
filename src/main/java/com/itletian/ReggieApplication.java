package com.itletian;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 主启动类
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching  // 开启spring cache 注解式缓存功能
public class ReggieApplication {
    public static void main( String[] args ) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("启动成功！！！");
    }
}
