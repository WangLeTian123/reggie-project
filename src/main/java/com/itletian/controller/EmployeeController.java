package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.entity.Employee;
import com.itletian.service.EmployeeService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

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
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        // 清理session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工功能
     */
    @PostMapping
    public R<String> saveEmployee(@RequestBody Employee employee, HttpServletRequest request) {
        // 默认初始密码是123456 需要md5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置当前时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        // 将创建用户信息设置为该用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * @param page: 当前页数
     * @param pageSize: 每页最大记录数
     * @param name: 根据名字查询信息的名字（模糊查询实现）
     * @description 员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        // 创建分页构造器
        Page<Employee> pageParams = new Page<>(page, pageSize);
        // 创建条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        // 执行查询
        employeeService.page(pageParams, queryWrapper);
        return R.success(pageParams);
    }

    /**
     * 根据id修改员工信息
     */
    @PutMapping
    public R<String> modifyEmployee(HttpServletRequest request, @RequestBody Employee employee) {
        // 获取当前员工id
        Long empId = (Long) request.getSession().getAttribute("employee");
        // 设置员工信息
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> queryEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if(employee != null){
            return R.success(employee);
        }
            return R.error("没有查询到对应员工信息");

    }

}
