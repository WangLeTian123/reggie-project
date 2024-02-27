package com.itletian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.dto.SetmealDto;
import com.itletian.entity.Category;
import com.itletian.entity.Setmeal;
import com.itletian.entity.SetmealDish;
import com.itletian.mapper.SetmealMapper;
import com.itletian.service.CategoryService;
import com.itletian.service.SetmealDishService;
import com.itletian.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    // 套餐-菜品关系
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveSetmeal(SetmealDto setmealDto) {
        // 首先添加套餐基本信息
        this.save(setmealDto);
        // 然后添加菜品信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealDto.getId());
        }
        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public SetmealDto getSetmealById(Long id) {
        SetmealDto setmealDto = new SetmealDto();

        // 首先获取套餐基本信息
        Setmeal setmeal = this.getById(id);
        BeanUtils.copyProperties(setmeal, setmealDto);

        // 然后获取套餐菜品信息
        Category category = categoryService.getById(setmeal.getCategoryId());
        setmealDto.setCategoryName(category.getName());
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDishService.list(queryWrapper);
        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    @Override
    public void updateSetmeal(SetmealDto setmealDto) {
        // 首先修改套餐基本信息
        this.updateById(setmealDto);

        // 然后修改套餐-菜品对应关系信息(先删除后添加的思想)
        Setmeal setmeal = this.getById(setmealDto.getId());
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());
        setmealDishService.remove(queryWrapper);
        for (SetmealDish setmealDish : setmealDto.getSetmealDishes()) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishService.saveBatch(setmealDto.getSetmealDishes());
    }
}
