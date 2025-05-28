package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.ResultMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminCategoryController")
@Slf4j
@Api(tags = "分类相关接口")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分类分页查询
     * */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分类分页查询，参数为：{}", categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增分类
     * */
    @ApiOperation("新增分类")
    @PostMapping
    public Result addCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("新增分类：{}", categoryDTO);
        categoryService.addCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 修改分类
     * */
    @ApiOperation("修改分类")
    @PutMapping
    public Result updateCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类：{}", categoryDTO);
        categoryService.updateCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 启用和禁用分类
     * */
    @ApiOperation("分类状态修改")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("分类状态修改为：{},{}", id,status);
        categoryService.startOrStop(status, id);
        return Result.success();
    }
    /**
     * 根据类型查询分类
     * */
    @ApiOperation("根据类型查询分类")
    @GetMapping("/list")
    public Result<List<Category>> getByType(Integer type) {
        log.info("根据类型查询分类：{}", type);
        List<Category> category = categoryService.getByType(type);
        return Result.success(category);
    }
    /**
     * 根据id删除分类
     * */
    @ApiOperation("根据ID删除分类")
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("删除分类ID为：{}", id);
        categoryService.deleteById(id);
        return Result.success();
    }
}
