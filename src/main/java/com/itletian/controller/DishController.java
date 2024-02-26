package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.dto.DishDto;
import com.itletian.entity.Dish;
import com.itletian.entity.Employee;
import com.itletian.service.DishService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 分页查询菜品
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        wrapper.orderByAsc(Dish::getSort);
        pageParam = dishService.page(pageParam, wrapper);
        return R.success(pageParam);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public R<Dish> saveDish(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        return null;
    }

}
