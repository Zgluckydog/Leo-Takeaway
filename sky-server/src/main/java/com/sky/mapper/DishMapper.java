package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 新增菜品
     * */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 菜品起售停售
     * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
    /**
     * 根据ID查询菜品
     * */

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

//    @Delete("delete from dish where id = #{id}")
//    void deleteById(Long id);

    void deleteByIds(List<Long> ids);


    /**
     * 修改菜品
     * */

}
