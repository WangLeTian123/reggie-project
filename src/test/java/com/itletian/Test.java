package com.itletian;

import cn.hutool.core.date.DateUtil;
import com.itletian.entity.Employee;
import com.itletian.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
@SpringBootTest
public class Test {
    @Autowired
    private EmployeeService employeeService;

    @org.junit.jupiter.api.Test
    public void Test01() {
        System.out.println(employeeService.getById(1L).getUpdateTime());
        String pattern = "yyyy-MM-dd HH:mm:ss";
        LocalDateTime time = employeeService.getById(1L).getUpdateTime();
        DateUtil.format(time, pattern);
        System.out.println(time);
    }
}
