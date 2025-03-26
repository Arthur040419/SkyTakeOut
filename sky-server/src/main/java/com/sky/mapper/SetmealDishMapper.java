package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐信息
     * @param DishIds
     * @return
     */
    List<Long> selectByDishId(List<Long> DishIds);

    /**
     * 批量添加套餐的菜品信息
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);


    /**
     * 根据套餐id查询套餐相关菜品
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> selectBySetmealId(Long setmealId);


    /**
     * 根据套餐id删除关联菜品
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id=#{setmealId}")
    void deleteBySetmealId(Long setmealId);


    /**
     * 批量删除与套餐关联的菜品
     * @param ids
     */
    void deleteBySetmealIds(List<Long> ids);


    /**
     * 获取与套餐关联的菜品信息
     * @param setmealId
     * @return
     */
    List<Dish> getDishes(Long setmealId);
}
