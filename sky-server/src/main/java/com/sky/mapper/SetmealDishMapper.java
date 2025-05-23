package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据分类查询套餐的数量
     * */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 根据菜品ID查询对应的套餐ID
     * */

    // select setmeal_id from dish where dish_id in ()
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);
}
