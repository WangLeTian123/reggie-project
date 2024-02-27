package com.itletian.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itletian.dto.SetmealDto;
import com.itletian.entity.Category;
import com.itletian.entity.Setmeal;
import com.itletian.service.CategoryService;
import com.itletian.service.SetmealService;
import com.itletian.util.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

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


}
