package com.itletian.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itletian.entity.Setmeal;
import com.itletian.mapper.SetmealMapper;
import com.itletian.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
}
