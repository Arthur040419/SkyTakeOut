package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 批量添加口味信息
     *
     * @param flavors
     */
    void saveBatch(List<DishFlavor> flavors);


    /**
     * 根据菜品id批量删除口味信息
     * @param ids
     */
    void deleteBatchByDishId(List<Long> ids);

    /**
     * 根据菜品id删除口味信息
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id=#{dishId}")
    void deleteByDishId(Long dishId);

    /**
     * 根据菜品id查询口味信息
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id=#{dishID}")
    List<DishFlavor> selectByDishID(Long dishId);



}
