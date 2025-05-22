package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public PageResult page(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);
        long total = page.getTotal();
        List<Category> records = page.getResult();
        return new PageResult(total,records);
    }
    /**
     * 新增分类
     * */
    @Override
    public void addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        // 对象拷贝属性
        BeanUtils.copyProperties(categoryDTO,category);
        // 设置分类状态
        category.setStatus(StatusConstant.ENABLE);
        // 设置创建时间和修改时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        // 设置当前创建创建人id和修改人id
        category.setCreateUser(BaseContext.getCurrentId());
        category.setUpdateUser(BaseContext.getCurrentId());

        // Mapper层连接SQL
        categoryMapper.insert(category);
    }
    /**
     * 修改分类
     * */
    @Override
    public void updateCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        categoryMapper.update(category);
    }

    /**
     * 启用和禁用分类
     * */
    @Override
    public void startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .build();
        categoryMapper.update(category);
    }

    /**
     * 根据类型查询分类
     * */
    @Override
    public Category getByType(Integer type) {
        Category category = categoryMapper.getByType(type);
        return category;
    }
    /**
     * 根据id删除分类
     * */
    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }

}
