package com.sky.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//菜品mapper
@Mapper
public interface DishMapper {

    /**
     * 根据分类id，查询该分类下的菜品个数
     * @param categoryId
     * @return
     */
    @Select("select count(0) from dish where category_id=#{categoryId}")
    Integer countDish(Long categoryId);

}
