package com.sky.service.impl;

import com.alibaba.druid.support.spring.stat.annotation.Stat;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 菜品分页查询
     * */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        // 分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 新增菜品
     * */
    @Override
    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {

        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        // 向菜品表插入1条数据
        dishMapper.insert(dish);

        // 获取insert语句生成的主键值
        Long id = dish.getId();

        // 向口味表插入n条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(id);
            });
            // 插入数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品起售停售
     * */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);

        // 如果停售的话，包含此菜品的套餐也将停售
        if (status == StatusConstant.DISABLE) {
            List<Long> dishIds = new ArrayList<>();
            dishIds.add(id);

            List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(dishIds);
            if(setmealIds != null && setmealIds.size() > 0){
                for(Long setmealId : setmealIds){
                    Setmeal setmeal = Setmeal.builder()
                            .id(setmealId)
                            .status(StatusConstant.DISABLE)
                            .build();
                    setmealMapper.update(setmeal);
                }
            }
        }
    }

    /**
     * 根据ID查询菜品
     * */
    @Override
    public DishVO findByIdWithFlavor(Long id) {
        // 根据ID查询菜品数据
        Dish dish = dishMapper.getById(id);
        // 根据ID查询口味数据
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        // 将查询的数据封装到VO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    /**
     * 修改菜品
     * */
    @Override
    public void updateDishWithFlavor(DishDTO dishDTO) {
        // 其他信息修改
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        // 口味信息修改
        // 先删除
        dishFlavorMapper.deleteByDishId(dishDTO.getId());
        // 再修改
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });
            // 插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }


    }

    /**
     * 批量删除菜品
     * */
    @Transactional
    @Override
    public void deleteDish(List<Long> ids) {
        // 当前菜品是否能够删除---存在起售中的菜品？
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                // 当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 当前菜品是否能够删除---是否被套餐关联？
        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if (setmealIds != null && setmealIds.size() > 0) {
            // 菜品关联套餐，不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品表中的菜品数据
//        for (Long id : ids) {
//            dishMapper.deleteById(id);
//            // 删除菜品关联的口味数据
//            dishFlavorMapper.deleteByDishId(id);
//        }

        // 批量删除菜品表中的菜品数据
        dishMapper.deleteByIds(ids);
        // 批量删除菜品关联的口味数据
        dishFlavorMapper.deleteByDishIds(ids);


    }

    /**
     * 根据分类ID查询查询菜品
     * */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 根据分类ID查询菜品和口味
     * */
    @Override
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            // 根据菜品id查询对应口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());
            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }
}
