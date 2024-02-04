package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itletian.entity.Employee;
import com.itletian.service.EmployeeService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录功能
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String username = employee.getUsername();
        String password = employee.getPassword();
        // 将密码进行MD5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        // 用户名比对，用户名唯一所以数据库表中最多只有一个该用户
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, username);
        Employee emp = employeeService.getOne(queryWrapper);

        // 如果没有查询到则返回登陆失败结果
        if (emp == null) {
            return R.error("登录失败");
        }
        // 判断密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("登陆失败");
        }
        // 判断当前用户状态是否被停用（status属性：0-》停用  1-》正常）
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }
        // 到这里为止说明一切都没问题
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出功能
     */
    @RequestMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }
}
