package com.itletian.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itletian.entity.Category;

public interface CategoryService extends IService<Category> {
    void removeCategoryServiceById(Long id);
}
