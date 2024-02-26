package com.itletian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itletian.dto.DishDto;
import com.itletian.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    void saveDish(DishDto dishDto);

    DishDto getDishById(Long id);

    void updateDish(DishDto dishDto);

    void removeDish(String ids);

    void updateDishStatus(int status, String ids);

    List<DishDto> getList(Dish dish);
}
