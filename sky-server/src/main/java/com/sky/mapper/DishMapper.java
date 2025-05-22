package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DishMapper {
    /**
     * 根据分类ID查询菜品数量
     * */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countCategoryId(Long categoryId);

}
