package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//套餐mapper
@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id，查询指定分类下的套餐种类数
     * @param categoryId
     * @return
     */
    @Select("select count(0) from setmeal where category_id=#{categoryId}")
    Integer countSetMeal(Long categoryId);
}
