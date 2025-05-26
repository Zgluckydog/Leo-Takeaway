package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
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
    /**
     * 批量保存菜品与套餐的关联关系
     * */
    void insertBatch(List<SetmealDish> setmealDishs);

    /**
     * 根据ID查询套餐和菜品的关系
     * */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getSetmealIdsByDishId(Long setmealId);

    /**
     * 修改套餐，删除套餐关联的菜品
     * */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void delete(Long setmealId);
}
