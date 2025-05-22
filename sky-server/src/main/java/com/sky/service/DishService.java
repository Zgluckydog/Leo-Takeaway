package com.sky.service;

import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

public interface DishService {
    /**
     * 菜品分页查询
     * */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);
}
