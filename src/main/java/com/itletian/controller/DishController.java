package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.dto.DishDto;
import com.itletian.entity.Category;
import com.itletian.entity.Dish;
import com.itletian.entity.Employee;
import com.itletian.service.CategoryService;
import com.itletian.service.DishService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询菜品
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);
        wrapper.orderByAsc(Dish::getUpdateTime);
        pageParam = dishService.page(pageParam, wrapper);

        Page<DishDto> dishDtoPage = new Page<>();
        BeanUtils.copyProperties(pageParam, dishDtoPage, "records");

        List<DishDto> list = new ArrayList<>();
        for (Dish dish : pageParam.getRecords()) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);
            // 根据分类id查询分类名称
            Category category = categoryService.getById(dish.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            list.add(dishDto);
        }
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 新增菜品
     */
    @PostMapping
    public R<String> saveDish(@RequestBody DishDto dishDto) {
        log.info(dishDto.toString());
        dishService.saveDish(dishDto);
        return R.success("新增菜品成功");
    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     */
    @GetMapping("/{id}")
    public R<DishDto> queryDishById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishById(id);
        return R.success(dishDto);
    }

    /**
     * 修改菜品
     */
    @PutMapping
    public R<String> modifyDish(@RequestBody DishDto dishDto) {
        dishService.updateDish(dishDto);
        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品(单删除/批量删除)
     */
    @DeleteMapping
    public R<String> removeDish(String ids) {
        log.info(ids);
        dishService.removeDish(ids);
        return R.success("删除菜品成功");
    }

    /**
     * 修改菜品状态(停售/启售)
     */
    @PostMapping("/status/{status}")
    public R<String> modifyDishStatus(@PathVariable int status, String ids) {
        dishService.updateDishStatus(status, ids);
        return R.success("修改成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish) {
        List<DishDto> dishDtoList = dishService.getList(dish);
        return R.success(dishDtoList);
    }

}
