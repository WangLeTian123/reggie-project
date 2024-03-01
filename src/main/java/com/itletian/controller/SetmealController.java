package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.dto.SetmealDto;
import com.itletian.entity.Category;
import com.itletian.entity.Setmeal;
import com.itletian.entity.SetmealDish;
import com.itletian.service.CategoryService;
import com.itletian.service.SetmealDishService;
import com.itletian.service.SetmealService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 分页查询套餐
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        pageParam = setmealService.page(pageParam, queryWrapper);

        Page<SetmealDto> setmealDtoPage = new Page<>();
        BeanUtils.copyProperties(pageParam, setmealDtoPage, "records");
        ArrayList<SetmealDto> list = new ArrayList<>();
        for (Setmeal setmeal : pageParam.getRecords()) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);
            // 根据分类id查询分类名称
            Category category = categoryService.getById(setmeal.getCategoryId());
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }
            list.add(setmealDto);
        }
        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 新增套餐
     */
    @PostMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> addSetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.saveSetmeal(setmealDto);
        return R.success("套餐添加成功");
    }

    /**
     * 根据套餐id查询套餐
     */
    @GetMapping("/{id}")
    public R<SetmealDto> querySetmealById(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSetmealById(id);
        return R.success(setmealDto);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public R<String> modifySetmeal(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmeal(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 修改套餐售卖状态（单修改/批量修改）
     */
    @PostMapping("/status/{status}")
    public R<String> modifySetmealStatus(@PathVariable Integer status, String ids) {
        setmealService.updateSetmealStatus(status, ids);
        return R.success("修改售卖状态成功");
    }

    /**
     * 删除套餐（单删除/批量删除）
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache", allEntries = true)
    public R<String> removeSetmeal(String ids) {
        setmealService.removeSetmeal(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 根据条件查询套餐数据
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache", key = "#setmeal.categoryId + '_' + #setmeal.status")
    public R<List<Setmeal>> querySetmealList(Setmeal setmeal) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null, Setmeal::getStatus, setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

    /**
     * 根据套餐id查看该套餐对应的菜品
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDish>> querySetmealDishListById(@PathVariable Long id) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, id);
        queryWrapper.orderByDesc(SetmealDish::getUpdateTime);
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);
        return R.success(setmealDishList);
    }

}
