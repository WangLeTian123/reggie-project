package com.itletian;

import cn.hutool.core.date.DateUtil;
import com.itletian.entity.Employee;
import com.itletian.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
@SpringBootTest
public class Test {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 操作String类型数据
     */
    @org.junit.jupiter.api.Test
    public void Test01() {
        redisTemplate.opsForValue().set("address", "kaifeng");

    }
}
