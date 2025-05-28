package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.scripting.xmltags.ForEachSqlNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 新增套餐，需要保存套餐和菜品的关联关系
     * */
    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        // 向套餐表中插入数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        // 获取套餐ID
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishs = setmealDTO.getSetmealDishes();
        setmealDishs.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });
        // 保存套餐和菜品的关联关系
        setmealDishMapper.insertBatch(setmealDishs);
    }

    /**
     * 套餐分页查询
     * */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 分页参数
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 套餐起售、停售
     * */
    @Override
    public void startOrStop(Integer status, Long id) {
        // 起售套餐时，判断套餐内是否有停售菜品，有停售菜品 提示“套餐内包含未起售菜品，无法起售”
        // 查询当前套餐包含菜品，根据dish_id查询菜品表中该id菜品是否起售
        if(status == StatusConstant.ENABLE) {
            List<Dish> dishList = dishMapper.getBySetmealId(id);
            if (dishList != null && dishList.size()>0) {
                dishList.forEach(dish -> {
                    if(dish.getStatus() == StatusConstant.DISABLE) {
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                    }
                });
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 根据ID查询套餐
     * */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes = setmealDishMapper.getSetmealIdsByDishId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
     * 修改套餐,需要同时修改套餐表和菜品关联关系
     * */
    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        // 修改套餐表
        setmealMapper.update(setmeal);
        // 删除套餐与菜品关联，重新创建
        // 删除套餐关联表，setmeal_dish
        Long setmealId = setmeal.getId();
        // 删除套餐关联表
        setmealDishMapper.delete(setmealId);
        // 重新插入
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        // 为套餐中菜品插入套餐ID
        setmealDishes.forEach(setmealDish -> {
           setmealDish.setSetmealId(setmealId);
        });
        // 插入到setmeal_dish表中
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        ids.forEach(id -> {
            Setmeal setmeal = setmealMapper.getById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });
        ids.forEach(id -> {
            // 删除套餐
            setmealMapper.deleteById(id);
            // 删除套餐关联的菜品setmeal_dish
            setmealDishMapper.delete(id);
        });
    }

    /**
     * 根据分类id查询套餐
     * */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }


    @Override
    public List<DishItemVO> getDishItemById(Long id) {
        List<DishItemVO> list = setmealDishMapper.getDishItemBySetmealId(id);
        return list;
    }
}
