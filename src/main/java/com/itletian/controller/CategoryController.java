package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.entity.Category;
import com.itletian.entity.Employee;
import com.itletian.service.CategoryService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<Category> pageParams = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageParams, queryWrapper);
        return R.success(pageParams);
    }

    /**
     * 新增分类
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category, HttpServletRequest request) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(empId);
        category.setUpdateUser(empId);
        categoryService.save(category);
        return R.success("新增分类成功");

    }

    /**
     * 删除分类
     */
    @DeleteMapping
    public R<String> removeCategoryById(Long id) {
        categoryService.removeCategoryServiceById(id);
        return R.success("删除成功");
    }


    /**
     * 修改分类
     */
    @PutMapping
    public R<String> modifyCategory(HttpServletRequest request, @RequestBody Category category) {
        // 获取当前员工id
        Long empId = (Long) request.getSession().getAttribute("employee");
        // 设置员工信息
        category.setUpdateUser(empId);
        category.setUpdateTime(LocalDateTime.now());
        categoryService.updateById(category);
        return R.success("分类信息修改成功");
    }

    /**
     * 查询分类列表
     */
    @GetMapping("/list")
    public R<List<Category>> queryCategory(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(category.getType() != null, Category::getType, category.getType());
        wrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = categoryService.list(wrapper);
        return R.success(categoryList);
    }
}
