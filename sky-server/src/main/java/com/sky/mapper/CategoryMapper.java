package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
public interface CategoryMapper {
    /**
     * 分页查询
     * @return
     * */
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 新增分类
     * */
    @AutoFill(value = OperationType.INSERT)
    @Insert("insert into category (type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);
    /**
     * 1.修改分类
     * 2.启用和禁用分类
     * */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);

    /**
     * 根据类型查询分类
     * */
    @Select("select * from category where type = #{type}")
    Category getByType(Integer type);
    /**
     * 根据id删除分类
     * */
    @Delete("delete from category where id = #{id}")
    void deleteById(Long id);
}
