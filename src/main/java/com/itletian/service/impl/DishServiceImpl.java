package com.itletian.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.dto.DishDto;
import com.itletian.entity.Category;
import com.itletian.entity.Dish;
import com.itletian.entity.DishFlavor;
import com.itletian.mapper.DishMapper;
import com.itletian.service.CategoryService;
import com.itletian.service.DishFlavorService;
import com.itletian.service.DishService;
import com.itletian.util.CustomException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void saveDish(DishDto dishDto) {
        // 先添加菜品基本信息
        this.save(dishDto);

        // 然后添加菜品口味信息
        // 添加前需要先设置菜品id
        for (DishFlavor dishFlavor : dishDto.getFlavors()) {
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public DishDto getDishById(Long id) {
        //查询菜品基本信息，从dish表查询
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateDish(DishDto dishDto) {
        // 先修改菜品基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味数据---dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // 然后修改菜品口味信息
        for (DishFlavor dishFlavor : dishDto.getFlavors()) {
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(dishDto.getFlavors());
    }

    @Override
    public void removeDish(String ids) {
        String[] list = ids.split(",");
        for (String id : list) {
            Dish dish = this.getById(Long.parseLong(id));
            if (dish.getStatus() == 1) {
                throw new CustomException("菜品正在售卖，不能删除！");
            } else {
                // 删除菜品
                this.removeById(Long.parseLong(id));
                // 删除菜品对应口味信息
                LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(DishFlavor::getDishId, id);
                dishFlavorService.remove(queryWrapper);
            }

        }
    }

    @Override
    public void updateDishStatus(int status, String ids) {
        String[] list = ids.split(",");
        for (String id : list) {
            Dish dish = this.getById(Long.parseLong(id));
            dish.setStatus(status);
            this.updateById(dish);
        }

    }

    @Override
    public List<DishDto> getList(Dish dish) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotEmpty(dish.getName()), Dish::getName, dish.getName());
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus, 1);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(queryWrapper);

        List<DishDto> list = new ArrayList<>();
        for (Dish d : dishList) {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(d, dishDto);
            Category category = categoryService.getById(d.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }
            LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DishFlavor::getDishId, d.getId());
            List<DishFlavor> flavors = dishFlavorService.list(wrapper);
            dishDto.setFlavors(flavors);
            list.add(dishDto);
        }
        return list;
    }


}
