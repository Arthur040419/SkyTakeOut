package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
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
    DishMapper dishMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    /**
     * 保存菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional      //开启事务
    public void saveDishAndFlavor(DishDTO dishDTO) {
        //添加菜品需要两个接口，一个添加菜品信息，另一个添加菜品的口味信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //1.添加菜品信息
        dishMapper.save(dish);

        //添加菜品口味信息需要先获取刚插入的菜品的id，主键返回
        Long id = dish.getId();

        //2.添加菜品口味信息
        //先判断有没有口味信息，没有的话就不要添加了
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() != 0) {
            //将菜品的主键id放入口味信息中
            flavors.forEach(flavor -> {
                flavor.setDishId(id);
            });
            //批量添加口味信息
            dishFlavorMapper.saveBatch(dishDTO.getFlavors());

        }

    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    /**
     * 批量删除菜品
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteByIds(List<Long> ids) {
        //判断菜品是否可以删除--菜品是否关联套餐
        List<Long> list = setmealDishMapper.selectByDishId(ids);
        if (list != null && list.size() > 0) {
            //菜品有关联套餐
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //判断菜品是否可以删除--菜品处于起售状态
        for (Long dishId : ids) {
            Dish dish = dishMapper.selectById(dishId);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //批量删除菜品
        dishMapper.deleteBatch(ids);

        //删除菜品对应的口味信息
        dishFlavorMapper.deleteBatchByDishId(ids);
    }


    /**
     * 根据id查询菜品信息
     *
     * @param id
     * @return
     */
    @Override
    public DishVO selectById(Long id) {
        //查询菜品信息
        Dish dish = dishMapper.selectById(id);

        //查询口味信息
        List<DishFlavor> dishFlavors = dishFlavorMapper.selectByDishID(id);
        //封装查询数据
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    /**
     * 更新菜品
     *
     * @param dishDTO
     */
    @Transactional
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);

        //删除原本的口味信息
        dishFlavorMapper.deleteByDishId(dishDTO.getId());

        //插入新的口味信息
        //先判断有没有口味信息，没有的话就不要添加了
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() != 0) {
            //将菜品的主键id放入口味信息中
            flavors.forEach(flavor -> {
                flavor.setDishId(dishDTO.getId());
            });
            //批量添加口味信息
            dishFlavorMapper.saveBatch(dishDTO.getFlavors());

        }
    }

    /**
     * 菜品起售、停售
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();
        dishMapper.update(dish);
    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> selectByCategoryId(Long categoryId) {
        return dishMapper.selectByDishId(categoryId);
    }
}
