package com.itletian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itletian.dto.SetmealDto;
import com.itletian.entity.Setmeal;

public interface SetmealService extends IService<Setmeal> {
    void saveSetmeal(SetmealDto setmealDto);

    SetmealDto getSetmealById(Long id);

    void updateSetmeal(SetmealDto setmealDto);

    void updateSetmealStatus(Integer status, String ids);

    void removeSetmeal(String ids);
}
