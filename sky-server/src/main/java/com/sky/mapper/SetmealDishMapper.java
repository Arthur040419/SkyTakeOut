package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐信息
     * @param DishIds
     * @return
     */
    List<Long> selectByDishId(List<Long> DishIds);
}
