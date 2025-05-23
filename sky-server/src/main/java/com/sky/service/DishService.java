package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {
    /**
     * 菜品分页查询
     * */
    PageResult page(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * */
    void saveWithFlavor(DishDTO dishDTO);
    /**
     * 菜品起售停售
     * */
    void startOrStop(Integer status, Long id);

    /**
     * 根据ID查询菜品
     * */
    DishVO findByIdWithFlavor(Long id);
    /**
     * 修改菜品
     * */
    void updateDishWithFlavor(DishDTO dishDTO);
    /**
     * 批量删除菜品
     * */
    void deleteDish(List<Long> ids);
}
