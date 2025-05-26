package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    /**
     * 新增套餐
     * */
    void save(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * */
    PageResult page(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 套餐起售、停售
     * */
    void startOrStop(Integer status, Long id);

    /**
     * 根据ID查询套餐
     * */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 修改套餐
     * */
    void update(SetmealDTO setmealDTO);

    /**
     * 批量删除套餐
     * */
    void deleteByIds(List<Long> ids);
}
