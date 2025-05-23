package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    /**
     * 菜品分页查询
     * */
    @ApiOperation("菜品分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询，参数为：{}", dishPageQueryDTO);
        PageResult pageResult = dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增菜品
     * */
    @PostMapping
    @ApiOperation("菜品相关接口")
    public Result save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品起售停售
     * */
    @ApiOperation("菜品起售停售")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("菜品起售停售：{}", status);
        dishService.startOrStop(status,id);
        return Result.success();
    }
    /**
     * 根据ID查询菜品
     * */
    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> findById(@PathVariable Long id) {
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.findById(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * */
    public Result updateDish(@RequestBody DishVO dishVO) {
        log.info("修改菜品：{}", dishVO);
        dishService.updateDish(dishVO);
        return Result.success();
    }

    /**
     * 批量删除菜品
     * */
    @ApiOperation("批量删除接口")
    @DeleteMapping
    public Result deleteDish(@RequestParam List<Long> ids) {
        log.info("批量删除菜品：{}", ids);
        dishService.deleteDish(ids);
        return Result.success();
    }

}
