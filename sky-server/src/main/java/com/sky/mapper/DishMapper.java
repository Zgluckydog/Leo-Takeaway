package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    /**
     * 根据分类ID查询菜品数量
     * */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countCategoryId(Long categoryId);
    /**
     * 菜品分页查询
     * */
    Page<Dish> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);
}
