package com.sky.mapper;


import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 批量删除菜品
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param dishId
     * @return
     */
    @Select("select * from dish where id=#{dishId}")
    Dish selectById(Long dishId);

    /**
     * 更新菜品
     * @param dish
     */
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
//    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByDishId(Long categoryId);

    /**
     * 根据套餐id查询菜品
     * @param setmealId
     * @return
     */
    List<DishItemVO> selectBySetmealId(Long setmealId);


    /**
     * 条件查询菜品
     * @param dish
     * @return
     */
    List<DishVO> select(Dish dish);


    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @Select("select * from dish where category_id = #{categoryId}")
    List<Dish> selectByCategoryId(Long categoryId);

    /**
     * 根据状态查询菜品数量
     * @param status
     * @return
     */
    @Select("select count(0) from dish where status=#{status}")
    Integer countByStatus(Integer status);
}
