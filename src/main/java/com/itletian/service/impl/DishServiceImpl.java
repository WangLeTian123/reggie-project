package com.itletian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.Dish;
import com.itletian.mapper.DishMapper;
import com.itletian.service.DishService;
import org.springframework.stereotype.Service;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
}
