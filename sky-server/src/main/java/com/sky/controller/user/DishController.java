package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.UserService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@Api(tags = "c端-根据分类id查询菜品")
@Slf4j
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据分类ID查询菜品和口味
     * */
    @ApiOperation("根据分类ID查询菜品")
    @GetMapping("/list")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类ID查询：{}", categoryId);
        // 构造redis中的key，规则：dish_分类id
        String key = "dish_"+categoryId;

        // 查询redis中是否存在菜品数据
        List<DishVO> list = (List<DishVO>)redisTemplate.opsForValue().get(key);
        // 如果存在，直接返回，无须查询数据库
        if (list != null && list.size() > 0) {
            return Result.success(list);
        }
        // 如果不存在，查询数据库，将查询到的数据放入redis中
        Dish dish = new Dish();
        dish.setCategoryId(categoryId);
        dish.setStatus(StatusConstant.ENABLE); // 查询起售的菜品
        list = dishService.listWithFlavor(dish);
        // 并且把本次查询到的数据放入redis中
        redisTemplate.opsForValue().set(key, list);
        return Result.success(list);
    }
}
