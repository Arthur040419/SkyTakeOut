package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    /**
     * 保存菜品
     * @param dishDTO
     */
    void saveDishAndFlavor(DishDTO dishDTO);


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);


    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteByIds(List<Long> ids);

    /**
     * 根据id查询菜品信息
     * @param id
     * @return
     */
    DishVO selectById(Long id);


    /**
     * 更新菜品
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 菜品起售、停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> selectBySetmealId(Long setmealId);


    /**
     * 用户获取起售菜品信息
     * @param dish
     * @return
     */
    List<DishVO> userGetDishes(Dish dish);


    /**
     * 根据分类查询菜品
     * @param categoryId
     * @return
     */
    List<Dish> selectByCategoryId(Long categoryId);
}
