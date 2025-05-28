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
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("adminDishController")
@Slf4j
@Api(tags = "菜品相关接口")
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


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
        // 清理redis缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
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
        // 清理所有dish开头的key
        cleanCache("*dish_*");
        return Result.success();
    }
    /**
     * 根据ID查询菜品
     * */
    @ApiOperation("根据ID查询菜品")
    @GetMapping("/{id}")
    public Result<DishVO> findById(@PathVariable Long id) {
        log.info("根据ID查询菜品：{}", id);
        DishVO dishVO = dishService.findByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * */
    @ApiOperation("修改菜品")
    @PutMapping()
    public Result updateDish(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateDishWithFlavor(dishDTO);
        // 清理所有dish开头的key
        cleanCache("*dish_*");
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

        // 清理所有dish开头的key
        cleanCache("*dish_*");
        return Result.success();
    }

    /**
     * 根据分类ID查询查询菜品
     * */
    @ApiOperation("根据分类ID查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> list(Long categoryId) {
        log.info("根据分类ID查询：{}", categoryId);
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    private void cleanCache(String pattern) {
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);

    }
}
