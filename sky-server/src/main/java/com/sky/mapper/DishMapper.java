package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 添加菜品信息
     * @param dish
     */
    @AutoFill(OperationType.INSERT)
    void save(Dish dish);
}
