package com.itletian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.Category;
import com.itletian.entity.Dish;
import com.itletian.entity.Setmeal;
import com.itletian.mapper.CategoryMapper;
import com.itletian.service.CategoryService;
import com.itletian.service.DishService;
import com.itletian.service.SetmealService;
import com.itletian.util.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    // 菜品
    @Lazy
    @Autowired
    private DishService dishService;
    // 套餐
    @Lazy
    @Autowired
    private SetmealService setmealService;
    /**
     * 自定义删除分类功能(根据id删除分类，删除之前需要进行判断)
     */
    @Override
    public void removeCategoryServiceById(Long id) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1 > 0) {
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0) {
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(id);
    }
}
